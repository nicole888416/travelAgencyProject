package service;

import model.OrderItem;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> listByOrderId(int orderId);
}
