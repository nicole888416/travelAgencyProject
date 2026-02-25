package dao.impl;

import dao.OrdersDao;
import model.Orders;
import util.Tool;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class OrdersDaoImpl implements OrdersDao {

    private Orders map(ResultSet rs) throws SQLException {
        Orders o = new Orders();
        o.setId(rs.getInt("id"));
        o.setOrderNo(rs.getString("order_no"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setEmployeeId(rs.getInt("employee_id"));
        int cid = rs.getInt("coupon_id");
        o.setCouponId(rs.wasNull()? null: cid);
        o.setStatus(rs.getString("status"));
        o.setAmount(rs.getInt("amount"));
        o.setDiscountAmount(rs.getInt("discount_amount"));
        o.setFinalAmount(rs.getInt("final_amount"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        return o;
    }

    @Override
    public int create(Orders o) {
        String sql = "INSERT INTO orders(order_no,customer_id,employee_id,coupon_id,status,amount,discount_amount,final_amount,order_date) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getOrderNo());
            ps.setInt(2, o.getCustomerId());
            ps.setInt(3, o.getEmployeeId());
            if (o.getCouponId()==null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, o.getCouponId());
            ps.setString(5, o.getStatus());
            ps.setInt(6, o.getAmount());
            ps.setInt(7, o.getDiscountAmount());
            ps.setInt(8, o.getFinalAmount());
            ps.setTimestamp(9, new Timestamp(o.getOrderDate().getTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return keys.next()? keys.getInt(1):0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Orders findById(int id) {
        String sql="SELECT * FROM orders WHERE id=?";
        try(Connection conn=Tool.getDb(); PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,id);
            try(ResultSet rs=ps.executeQuery()){ return rs.next()? map(rs):null; }
        } catch(SQLException e){ throw new RuntimeException(e); }
    }

    @Override
    public Orders findByOrderNo(String orderNo) {
        String sql="SELECT * FROM orders WHERE order_no=?";
        try(Connection conn=Tool.getDb(); PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,orderNo);
            try(ResultSet rs=ps.executeQuery()){ return rs.next()? map(rs):null; }
        } catch(SQLException e){ throw new RuntimeException(e); }
    }

    private List<Orders> list(String sql, Object... args){
        List<Orders> list=new ArrayList<>();
        try(Connection conn=Tool.getDb(); PreparedStatement ps=conn.prepareStatement(sql)){
            for(int i=0;i<args.length;i++){
                Object a=args[i];
                if(a instanceof Integer) ps.setInt(i+1,(Integer)a);
                else if(a instanceof java.sql.Timestamp) ps.setTimestamp(i+1,(Timestamp)a);
                else ps.setObject(i+1,a);
            }
            try(ResultSet rs=ps.executeQuery()){
                while(rs.next()) list.add(map(rs));
                return list;
            }
        } catch(SQLException e){ throw new RuntimeException(e); }
    }

    @Override public List<Orders> findByCustomerId(int customerId){ return list("SELECT * FROM orders WHERE customer_id=? ORDER BY order_date DESC", customerId); }
    @Override public List<Orders> findByEmployeeId(int employeeId){ return list("SELECT * FROM orders WHERE employee_id=? ORDER BY order_date DESC", employeeId); }

    @Override
    public List<Orders> findByDateRange(Date from, Date to) {
        return list("SELECT * FROM orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC",
                new Timestamp(from.getTime()), new Timestamp(to.getTime()));
    }

    @Override
    public List<Orders> findByMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.HOUR_OF_DAY,0);cal.set(Calendar.MINUTE,0);cal.set(Calendar.SECOND,0);cal.set(Calendar.MILLISECOND,0);
        Date start=cal.getTime();
        cal.add(Calendar.MONTH,1);
        Date end=cal.getTime();
        return findByDateRange(start,end);
    }

    @Override public List<Orders> findAll(){ return list("SELECT * FROM orders ORDER BY order_date DESC"); }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM orders WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

}
