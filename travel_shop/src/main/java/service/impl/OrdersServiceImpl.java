package service.impl;

import dao.*;
import dao.impl.*;
import model.*;
import service.OrdersService;
import util.IdUtil;
import util.Tool;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class OrdersServiceImpl implements OrdersService {

    private final OrdersDao ordersDao = new OrdersDaoImpl();
    private final CouponDao couponDao = new CouponDaoImpl();
    private final CustomerDao customerDao = new CustomerDaoImpl();

    @Override
    public PricingResult calculate(Customer customer, int amount, String couponCode) {
        if (customer == null) throw new IllegalArgumentException("customer 不可為 null");
        if (amount < 0) throw new IllegalArgumentException("amount 不可為負");

        int bestFinal = amount;
        Integer bestCouponId = null;

        // member discount
        int memberPayPercent = customer.levelEnum().payPercent();
        int memberFinal = (int)Math.round(amount * (memberPayPercent / 100.0));
        bestFinal = Math.min(bestFinal, memberFinal);

        // coupon discount (NOT stack)
        Coupon coupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            coupon = couponDao.findByCode(couponCode.trim().toUpperCase());
        }
        if (coupon != null && coupon.isActive() && amount >= coupon.getMinAmount()) {
            int couponFinal = amount;
            if (coupon.typeEnum() == CouponType.PERCENT && coupon.getPercentOff() != null) {
                couponFinal = (int)Math.round(amount * (coupon.getPercentOff() / 100.0));
            } else if (coupon.typeEnum() == CouponType.AMOUNT && coupon.getAmountOff() != null) {
                couponFinal = Math.max(0, amount - coupon.getAmountOff());
            }
            if (couponFinal < bestFinal) {
                bestFinal = couponFinal;
                bestCouponId = coupon.getId();
            }
        }

        int discount = amount - bestFinal;
        return new PricingResult(amount, discount, bestFinal, bestCouponId);
    }

    @Override
    public Orders placeOrder(String orderNo, int customerId, int employeeId, String couponCode, List<OrderItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) throw new IllegalArgumentException("購物車不可為空");
        Customer customer = customerDao.findById(customerId);
        if (customer == null) throw new IllegalArgumentException("會員不存在");
        if (employeeId <= 0) throw new IllegalArgumentException("必須選擇營業員");

        int amount = 0;
        for (OrderItem it : cartItems) amount += it.getSubtotal();

        PricingResult pricing = calculate(customer, amount, couponCode);

        Orders o = new Orders();
        o.setOrderNo(orderNo == null ? IdUtil.orderNo() : orderNo);
        o.setCustomerId(customerId);
        o.setEmployeeId(employeeId);
        o.setCouponId(pricing.couponId);
        o.setStatus(OrderStatus.PENDING.name());
        o.setAmount(pricing.amount);
        o.setDiscountAmount(pricing.discountAmount);
        o.setFinalAmount(pricing.finalAmount);
        o.setOrderDate(new Date());

        Connection conn = null;
        try {
            conn = Tool.getDb();
            conn.setAutoCommit(false);

            int orderId = insertOrder(conn, o);

            for (OrderItem it : cartItems) {
                it.setOrderId(orderId);
                insertItem(conn, it);
                decreaseSeat(conn, it.getScheduleId(), it.getQty());
            }

            int newTotal = customer.getTotalSpent() + pricing.finalAmount;
            String newLevel = upgradeLevel(newTotal).name();
            updateCustomerSpentAndLevel(conn, customerId, newTotal, newLevel);

            conn.commit();

            return ordersDao.findByOrderNo(o.getOrderNo());
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            throw new RuntimeException("下單失敗: " + e.getMessage(), e);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ignore) {}
        }
    }

    private int insertOrder(Connection conn, Orders o) throws SQLException {
        String sql = "INSERT INTO orders(order_no,customer_id,employee_id,coupon_id,status,amount,discount_amount,final_amount,order_date) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getOrderNo());
            ps.setInt(2, o.getCustomerId());
            ps.setInt(3, o.getEmployeeId());
            if (o.getCouponId() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, o.getCouponId());
            ps.setString(5, o.getStatus());
            ps.setInt(6, o.getAmount());
            ps.setInt(7, o.getDiscountAmount());
            ps.setInt(8, o.getFinalAmount());
            ps.setTimestamp(9, new Timestamp(o.getOrderDate().getTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("No generated key for orders");
            }
        }
    }

    private void insertItem(Connection conn, OrderItem it) throws SQLException {
        String sql = "INSERT INTO order_item(order_id,product_id,schedule_id,qty,unit_price,subtotal) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, it.getOrderId());
            ps.setInt(2, it.getProductId());
            ps.setInt(3, it.getScheduleId());
            ps.setInt(4, it.getQty());
            ps.setInt(5, it.getUnitPrice());
            ps.setInt(6, it.getSubtotal());
            ps.executeUpdate();
        }
    }

    private void decreaseSeat(Connection conn, int scheduleId, int qty) throws SQLException {
        String check = "SELECT seat_stock FROM product_schedule WHERE id=? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("行程不存在 scheduleId=" + scheduleId);
                int stock = rs.getInt(1);
                if (stock < qty) throw new SQLException("該出發日期已售完/機位不足");
            }
        }

        String sql = "UPDATE product_schedule SET seat_stock=seat_stock-? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, scheduleId);
            ps.executeUpdate();
        }
    }

    private void updateCustomerSpentAndLevel(Connection conn, int customerId, int total, String level) throws SQLException {
        String sql = "UPDATE customer SET total_spent=?, member_level=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, total);
            ps.setString(2, level);
            ps.setInt(3, customerId);
            ps.executeUpdate();
        }
    }

    private MemberLevel upgradeLevel(int totalSpent) {
        if (totalSpent >= 100000) return MemberLevel.PLATINUM;
        if (totalSpent >= 70000) return MemberLevel.GOLD;
        if (totalSpent >= 30000) return MemberLevel.SILVER;
        return MemberLevel.BRONZE;
    }

    @Override public List<Orders> listByCustomer(int customerId) { return ordersDao.findByCustomerId(customerId); }
    @Override public List<Orders> listByEmployee(int employeeId) { return ordersDao.findByEmployeeId(employeeId); }
    @Override public List<Orders> listByDateRange(Date from, Date to) { return ordersDao.findByDateRange(from, to); }
    @Override public List<Orders> listByMonth(int year, int month) { return ordersDao.findByMonth(year, month); }
    @Override public Orders findByOrderNo(String orderNo) { return ordersDao.findByOrderNo(orderNo); }
    @Override public boolean updateStatus(int orderId, String status) { return ordersDao.updateStatus(orderId, status); }

    @Override
    public boolean delete(int orderId) {
        // order_item has ON DELETE CASCADE
        return ordersDao.delete(orderId);
    }
}
