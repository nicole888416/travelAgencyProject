package model;

public enum MemberLevel {
    BRONZE, SILVER, GOLD, PLATINUM;

    public static MemberLevel fromDb(String v) {
        if (v == null) return BRONZE;
        try { return MemberLevel.valueOf(v.trim().toUpperCase()); }
        catch (Exception e) { return BRONZE; }
    }

    public int payPercent() {
        switch (this) {
            case SILVER: return 95;
            case GOLD: return 90;
            case PLATINUM: return 85;
            default: return 100;
        }
    }
}
