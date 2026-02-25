package model;

import java.util.Date;

public class Orders {
    private int id;
    private String orderNo;
    private int customerId;
    private int employeeId;
    private Integer couponId;
    private String status;
    private int amount;
    private int discountAmount;
    private int finalAmount;
    private Date orderDate;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
    public int getFinalAmount() { return finalAmount; }
    public void setFinalAmount(int finalAmount) { this.finalAmount = finalAmount; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public OrderStatus statusEnum() { return OrderStatus.fromDb(status); }
}
