package service.impl;

import dao.OrderItemDao;
import dao.impl.OrderItemDaoImpl;
import model.OrderItem;

import java.util.List;

public class OrderItemServiceImpl implements service.OrderItemService {

    private final OrderItemDao dao = new OrderItemDaoImpl();

    @Override public List<OrderItem> listByOrderId(int orderId) { return dao.findByOrderId(orderId); }
}
