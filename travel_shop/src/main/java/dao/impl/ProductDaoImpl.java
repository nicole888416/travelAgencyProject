package dao.impl;

import dao.ProductDao;
import model.Product;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setProductNo(rs.getString("product_no"));
        p.setProductName(rs.getString("product_name"));
        p.setProductPrice(rs.getInt("product_price"));
        p.setProductStock(rs.getInt("product_stock"));
        p.setDescription(rs.getString("description"));
        return p;
    }

    @Override
    public Product findById(int id) {
        String sql = "SELECT * FROM product WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next()? map(rs): null; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Product findByProductNo(String productNo) {
        String sql = "SELECT * FROM product WHERE product_no=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productNo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next()? map(rs): null; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsProductNo(String productNo) {
        String sql = "SELECT 1 FROM product WHERE product_no=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productNo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public int create(Product p) {
        String sql = "INSERT INTO product(product_no,product_name,product_price,product_stock,description) VALUES(?,?,?,?,?)";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getProductNo());
            ps.setString(2, p.getProductName());
            ps.setInt(3, p.getProductPrice());
            ps.setInt(4, p.getProductStock());
            ps.setString(5, p.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return keys.next()? keys.getInt(1):0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE product SET product_name=?,product_price=?,product_stock=?,description=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getProductPrice());
            ps.setInt(3, p.getProductStock());
            ps.setString(4, p.getDescription());
            ps.setInt(5, p.getId());
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM product WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM product ORDER BY id DESC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
