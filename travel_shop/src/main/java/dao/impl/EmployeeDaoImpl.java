package dao.impl;

import dao.EmployeeDao;
import model.Employee;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements EmployeeDao {

    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setEmployeeNo(rs.getString("employee_no"));
        e.setEmployeeName(rs.getString("employee_name"));
        e.setPhone(rs.getString("phone"));
        e.setEmail(rs.getString("email"));
        e.setBirthday(rs.getDate("birthday"));
        e.setAddressCity(rs.getString("address_city"));
        e.setAddressDetail(rs.getString("address_detail"));
        e.setUsername(rs.getString("username"));
        e.setPassword(rs.getString("password"));
        e.setRole(rs.getString("role"));
        e.setPhotoPath(rs.getString("photo_path"));
        return e;
    }

    @Override
    public Employee findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM employee WHERE username=? AND password=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Employee findById(int id) {
        String sql = "SELECT * FROM employee WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Employee findByUsername(String username) {
        String sql = "SELECT * FROM employee WHERE username=?";
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

    @Override public boolean existsUsername(String username) { return exists("SELECT 1 FROM employee WHERE username=?", username); }
    @Override public boolean existsPhone(String phone) { return exists("SELECT 1 FROM employee WHERE phone=?", phone); }
    @Override public boolean existsEmail(String email) { return exists("SELECT 1 FROM employee WHERE email=?", email); }

    @Override
    public int create(Employee e) {
        String sql = "INSERT INTO employee(employee_no,employee_name,phone,email,birthday,address_city,address_detail,username,password,role,photo_path) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getEmployeeNo());
            ps.setString(2, e.getEmployeeName());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getEmail());
            ps.setDate(5, new java.sql.Date(e.getBirthday().getTime()));
            ps.setString(6, e.getAddressCity());
            ps.setString(7, e.getAddressDetail());
            ps.setString(8, e.getUsername());
            ps.setString(9, e.getPassword());
            ps.setString(10, e.getRole());
            ps.setString(11, e.getPhotoPath());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public boolean update(Employee e) {
        String sql = "UPDATE employee SET employee_no=?,employee_name=?,phone=?,email=?,birthday=?,address_city=?,address_detail=?,password=?,role=?,photo_path=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getEmployeeNo());
            ps.setString(2, e.getEmployeeName());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getEmail());
            ps.setDate(5, new java.sql.Date(e.getBirthday().getTime()));
            ps.setString(6, e.getAddressCity());
            ps.setString(7, e.getAddressDetail());
            ps.setString(8, e.getPassword());
            ps.setString(9, e.getRole());
            ps.setString(10, e.getPhotoPath());
            ps.setInt(11, e.getId());
            return ps.executeUpdate()==1;
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public boolean updatePhoto(int employeeId, String photoPath) {
        String sql = "UPDATE employee SET photo_path=? WHERE id=?";
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, photoPath);
            ps.setInt(2, employeeId);
            return ps.executeUpdate()==1;
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public boolean deletePhoto(int employeeId) { return updatePhoto(employeeId, null); }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employee ORDER BY id DESC";
        List<Employee> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
