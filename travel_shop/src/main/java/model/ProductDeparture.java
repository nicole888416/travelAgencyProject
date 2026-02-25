package model;

import java.sql.Date;

public class ProductDeparture {
    private int id;
    private int productId;
    private Date departDate;
    private Date returnDate;
    private int availableSeats;
    private String note;

    public ProductDeparture() {}

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public Date getDepartDate() { return departDate; }
    public void setDepartDate(Date departDate) { this.departDate = departDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
