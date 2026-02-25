package service.impl;

import dao.OrderDetailDao;
import dao.impl.OrderDetailDaoImpl;
import model.OrderDetail;
import service.OrderDetailService;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import util.Tool;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderDetailServiceImpl implements OrderDetailService {

    private OrderDetailDao dao = new OrderDetailDaoImpl();

    @Override
    public List<OrderDetail> findByOrderId(int orderId) throws Exception {
        // simple implementation: query DB directly
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM order_detail WHERE order_id = ?";
        try (Connection conn = Tool.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setId(rs.getInt("id"));
                    d.setOrderId(rs.getInt("order_id"));
                    d.setProductId(rs.getInt("product_id"));
                    try { d.setDepartId(rs.getInt("depart_id")); } catch (Exception ex) {}
                    d.setUnitPrice(rs.getInt("unit_price"));
                    d.setQty(rs.getInt("qty"));
                    d.setSubtotal(rs.getLong("subtotal"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    @Override
    public boolean insert(OrderDetail od) throws Exception {
        // opens its own connection (non-tx)
        try (Connection conn = Tool.getDb()) {
            return dao.insert(od, conn);
        }
    }
}
