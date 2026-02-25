package service;

import model.Customer;
import model.OrderItem;
import model.Orders;

import java.util.Date;
import java.util.List;

public interface OrderService {

    class PricingResult {
        public final int amount;
        public final int discountAmount;
        public final int finalAmount;
        public final Integer couponId;

        public PricingResult(int amount, int discountAmount, int finalAmount, Integer couponId) {
            this.amount = amount;
            this.discountAmount = discountAmount;
            this.finalAmount = finalAmount;
            this.couponId = couponId;
        }
    }

    PricingResult calculate(Customer customer, int amount, String couponCode);

    Orders placeOrder(String orderNo, int customerId, int employeeId, String couponCode, List<OrderItem> cartItems);

    List<Orders> listByCustomer(int customerId);
    List<Orders> listByEmployee(int employeeId);
    List<Orders> listByDateRange(Date from, Date to);
    List<Orders> listByMonth(int year, int month);

    Orders findByOrderNo(String orderNo);
    boolean updateStatus(int orderId, String status);
}