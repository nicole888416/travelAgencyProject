package model;

public class Coupon {
    private int id;
    private String couponCode;
    private String couponName;
    private String couponType; // PERCENT/AMOUNT
    private int minAmount;
    private Integer percentOff; // payPercent
    private Integer amountOff;
    private boolean active;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }
    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }
    public int getMinAmount() { return minAmount; }
    public void setMinAmount(int minAmount) { this.minAmount = minAmount; }
    public Integer getPercentOff() { return percentOff; }
    public void setPercentOff(Integer percentOff) { this.percentOff = percentOff; }
    public Integer getAmountOff() { return amountOff; }
    public void setAmountOff(Integer amountOff) { this.amountOff = amountOff; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public CouponType typeEnum() { return CouponType.fromDb(couponType); }
}
