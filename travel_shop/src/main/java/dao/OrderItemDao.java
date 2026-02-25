package dao;

import model.OrderItem;
import java.util.List;

public interface OrderItemDao {
    int create(OrderItem item);
    List<OrderItem> findByOrderId(int orderId);
}
