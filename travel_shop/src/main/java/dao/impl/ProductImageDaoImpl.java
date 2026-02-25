package dao.impl;

import dao.ProductImageDao;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDaoImpl implements ProductImageDao {

    @Override
    public boolean upsert(int productId, int imgIndex, String imgPath) {
        String sql = "INSERT INTO product_image(product_id,img_index,img_path) VALUES(?,?,?) " +
                "ON DUPLICATE KEY UPDATE img_path=VALUES(img_path)";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, imgIndex);
            ps.setString(3, imgPath);
            return ps.executeUpdate() >= 1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean delete(int productId, int imgIndex) {
        String sql = "DELETE FROM product_image WHERE product_id=? AND img_index=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, imgIndex);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<String> findPaths(int productId) {
        String sql = "SELECT img_path FROM product_image WHERE product_id=? ORDER BY img_index";
        List<String> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) list.add(rs.getString(1));
                return list;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public String findFirstPath(int productId) {
        String sql = "SELECT img_path FROM product_image WHERE product_id=? ORDER BY img_index LIMIT 1";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next()? rs.getString(1): null; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
