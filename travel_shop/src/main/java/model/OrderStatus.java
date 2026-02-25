package model;

public enum OrderStatus {
    PENDING, CONFIRMING, COMPLETED;

    public static OrderStatus fromDb(String v) {
        if (v == null) return PENDING;
        try { return OrderStatus.valueOf(v.trim().toUpperCase()); }
        catch (Exception e) { return PENDING; }
    }
}
