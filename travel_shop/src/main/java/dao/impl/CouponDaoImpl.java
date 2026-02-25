package dao.impl;

import dao.CouponDao;
import model.Coupon;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponDaoImpl implements CouponDao {

    private Coupon map(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setId(rs.getInt("id"));
        c.setCouponCode(rs.getString("coupon_code"));
        c.setCouponName(rs.getString("coupon_name"));
        c.setCouponType(rs.getString("coupon_type"));
        c.setMinAmount(rs.getInt("min_amount"));
        int po = rs.getInt("percent_off");
        c.setPercentOff(rs.wasNull()? null: po);
        int ao = rs.getInt("amount_off");
        c.setAmountOff(rs.wasNull()? null: ao);
        c.setActive(rs.getInt("active")==1);
        return c;
    }

    @Override
    public Coupon findByCode(String code) {
        String sql = "SELECT * FROM coupon WHERE coupon_code=? AND active=1";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) { return rs.next()? map(rs): null; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Coupon> findAllActive() {
        String sql = "SELECT * FROM coupon WHERE active=1 ORDER BY id";
        List<Coupon> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
