package model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private int scheduleId;
    private int qty;
    private int unitPrice;
    private int subtotal;

    // display fields
    private String productName;
    private String productImgPath;
    private String departDate;
    private String returnDate;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImgPath() { return productImgPath; }
    public void setProductImgPath(String productImgPath) { this.productImgPath = productImgPath; }
    public String getDepartDate() { return departDate; }
    public void setDepartDate(String departDate) { this.departDate = departDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
}
