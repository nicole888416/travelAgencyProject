package model;

public class Product {
    private int id;
    private String productNo;
    private String productName;
    private int productPrice;
    private int productStock;
    private String description;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductNo() { return productNo; }
    public void setProductNo(String productNo) { this.productNo = productNo; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getProductPrice() { return productPrice; }
    public void setProductPrice(int productPrice) { this.productPrice = productPrice; }
    public int getProductStock() { return productStock; }
    public void setProductStock(int productStock) { this.productStock = productStock; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
