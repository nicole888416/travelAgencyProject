package dao.impl;

import dao.CustomerDao;
import model.Customer;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setCustomerNo(rs.getString("customer_no"));
        c.setCustomerName(rs.getString("customer_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setBirthday(rs.getDate("birthday"));
        c.setAddressCity(rs.getString("address_city"));
        c.setAddressDetail(rs.getString("address_detail"));
        c.setUsername(rs.getString("username"));
        c.setPassword(rs.getString("password"));
        c.setMemberLevel(rs.getString("member_level"));
        c.setTotalSpent(rs.getInt("total_spent"));
        c.setPhotoPath(rs.getString("photo_path"));
        return c;
    }

    @Override
    public Customer findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM customer WHERE username=? AND password=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM customer WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Customer findByUsername(String username) {
        String sql = "SELECT * FROM customer WHERE username=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private boolean exists(String sql, String val) {
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, val);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean existsUsername(String username) { return exists("SELECT 1 FROM customer WHERE username=?", username); }
    @Override public boolean existsPhone(String phone) { return exists("SELECT 1 FROM customer WHERE phone=?", phone); }
    @Override public boolean existsEmail(String email) { return exists("SELECT 1 FROM customer WHERE email=?", email); }

    @Override
    public int create(Customer c) {
        String sql = "INSERT INTO customer(customer_no,customer_name,phone,email,birthday,address_city,address_detail,username,password,member_level,total_spent,photo_path) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getCustomerNo());
            ps.setString(2, c.getCustomerName());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setDate(5, new java.sql.Date(c.getBirthday().getTime()));
            ps.setString(6, c.getAddressCity());
            ps.setString(7, c.getAddressDetail());
            ps.setString(8, c.getUsername());
            ps.setString(9, c.getPassword());
            ps.setString(10, c.getMemberLevel());
            ps.setInt(11, c.getTotalSpent());
            ps.setString(12, c.getPhotoPath());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(Customer c) {
        String sql = "UPDATE customer SET customer_no=?,customer_name=?,phone=?,email=?,birthday=?,address_city=?,address_detail=?,password=?,member_level=?,total_spent=?,photo_path=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerNo());
            ps.setString(2, c.getCustomerName());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setDate(5, new java.sql.Date(c.getBirthday().getTime()));
            ps.setString(6, c.getAddressCity());
            ps.setString(7, c.getAddressDetail());
            ps.setString(8, c.getPassword());
            ps.setString(9, c.getMemberLevel());
            ps.setInt(10, c.getTotalSpent());
            ps.setString(11, c.getPhotoPath());
            ps.setInt(12, c.getId());
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean updatePhoto(int customerId, String photoPath) {
        String sql = "UPDATE customer SET photo_path=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, photoPath);
            ps.setInt(2, customerId);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean deletePhoto(int customerId) {
        return updatePhoto(customerId, null);
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customer ORDER BY id DESC";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
