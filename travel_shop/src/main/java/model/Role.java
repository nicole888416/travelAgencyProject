package model;

public enum Role {
    GM, STAFF;

    public static Role fromDb(String v) {
        if (v == null) return STAFF;
        try { return Role.valueOf(v.trim().toUpperCase()); }
        catch (Exception e) { return STAFF; }
    }
}
