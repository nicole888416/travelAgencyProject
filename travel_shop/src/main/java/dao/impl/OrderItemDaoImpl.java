package dao.impl;

import dao.OrderItemDao;
import model.OrderItem;
import util.Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {

    @Override
    public int create(OrderItem item) {
        String sql = "INSERT INTO order_item(order_id,product_id,schedule_id,qty,unit_price,subtotal) VALUES(?,?,?,?,?,?)";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getScheduleId());
            ps.setInt(4, item.getQty());
            ps.setInt(5, item.getUnitPrice());
            ps.setInt(6, item.getSubtotal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { return keys.next()? keys.getInt(1):0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        String sql =
                "SELECT oi.*, p.product_name, p.description, pi.img_path AS product_img, " +
                "DATE_FORMAT(ps.depart_date,'%Y-%m-%d') AS depart_date, " +
                "DATE_FORMAT(ps.return_date,'%Y-%m-%d') AS return_date " +
                "FROM order_item oi " +
                "JOIN product p ON oi.product_id=p.id " +
                "JOIN product_schedule ps ON oi.schedule_id=ps.id " +
                "LEFT JOIN product_image pi ON pi.product_id=p.id AND pi.img_index=1 " +
                "WHERE oi.order_id=? ORDER BY oi.id";
        List<OrderItem> list = new ArrayList<>();
        try (Connection conn = Tool.getDb(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem it = new OrderItem();
                    it.setId(rs.getInt("id"));
                    it.setOrderId(rs.getInt("order_id"));
                    it.setProductId(rs.getInt("product_id"));
                    it.setScheduleId(rs.getInt("schedule_id"));
                    it.setQty(rs.getInt("qty"));
                    it.setUnitPrice(rs.getInt("unit_price"));
                    it.setSubtotal(rs.getInt("subtotal"));
                    it.setProductName(rs.getString("product_name"));
                    it.setProductImgPath(rs.getString("product_img"));
                    it.setDepartDate(rs.getString("depart_date"));
                    it.setReturnDate(rs.getString("return_date"));
                    list.add(it);
                }
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
