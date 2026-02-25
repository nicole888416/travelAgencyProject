package model;

public enum CouponType {
    PERCENT, AMOUNT;

    public static CouponType fromDb(String v) {
        if (v == null) return AMOUNT;
        try { return CouponType.valueOf(v.trim().toUpperCase()); }
        catch (Exception e) { return AMOUNT; }
    }
}
