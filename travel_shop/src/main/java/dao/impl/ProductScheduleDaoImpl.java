package dao.impl;

import dao.ProductScheduleDao;
import model.ProductSchedule;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductScheduleDaoImpl implements ProductScheduleDao {

    private ProductSchedule map(ResultSet rs) throws SQLException {
        ProductSchedule s = new ProductSchedule();
        s.setId(rs.getInt("id"));
        s.setProductId(rs.getInt("product_id"));
        s.setDepartDate(rs.getDate("depart_date"));
        s.setReturnDate(rs.getDate("return_date"));
        s.setSeatStock(rs.getInt("seat_stock"));
        return s;
    }

    @Override
    public int create(ProductSchedule s) {
        String sql = "INSERT INTO product_schedule(product_id,depart_date,return_date,seat_stock) VALUES(?,?,?,?)";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getProductId());
            ps.setDate(2, new java.sql.Date(s.getDepartDate().getTime()));
            ps.setDate(3, new java.sql.Date(s.getReturnDate().getTime()));
            ps.setInt(4, s.getSeatStock());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return keys.next()? keys.getInt(1):0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(ProductSchedule s) {
        String sql = "UPDATE product_schedule SET depart_date=?,return_date=?,seat_stock=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(s.getDepartDate().getTime()));
            ps.setDate(2, new java.sql.Date(s.getReturnDate().getTime()));
            ps.setInt(3, s.getSeatStock());
            ps.setInt(4, s.getId());
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM product_schedule WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<ProductSchedule> findByProductId(int productId) {
        String sql = "SELECT * FROM product_schedule WHERE product_id=? ORDER BY depart_date";
        List<ProductSchedule> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<ProductSchedule> findByDateRange(Date from, Date to) {
        String sql = "SELECT * FROM product_schedule WHERE depart_date BETWEEN ? AND ? ORDER BY depart_date";
        List<ProductSchedule> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public ProductSchedule findById(int id) {
        String sql = "SELECT * FROM product_schedule WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next()? map(rs): null; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
