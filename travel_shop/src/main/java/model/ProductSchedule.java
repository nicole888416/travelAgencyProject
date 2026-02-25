package model;

import java.util.Date;

public class ProductSchedule {
    private int id;
    private int productId;
    private Date departDate;
    private Date returnDate;
    private int seatStock;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public Date getDepartDate() { return departDate; }
    public void setDepartDate(Date departDate) { this.departDate = departDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public int getSeatStock() { return seatStock; }
    public void setSeatStock(int seatStock) { this.seatStock = seatStock; }
}
