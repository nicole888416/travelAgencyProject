package dao.impl;

import dao.OrderDetailDao;
import model.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderDetailDaoImpl implements OrderDetailDao {

    @Override
    public boolean insert(OrderDetail d, Connection conn) throws Exception {
        String sql = "INSERT INTO order_detail (order_id, product_id, depart_id, unit_price, qty, subtotal) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getOrderId());
            ps.setInt(2, d.getProductId());
            if (d.getDepartId() != null) ps.setInt(3, d.getDepartId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setInt(4, d.getUnitPrice());
            ps.setInt(5, d.getQty());
            ps.setLong(6, d.getSubtotal());
            return ps.executeUpdate() > 0;
        }
    }
}
