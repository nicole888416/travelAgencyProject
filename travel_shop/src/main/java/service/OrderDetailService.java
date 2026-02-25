package service;

import model.OrderDetail;
import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> findByOrderId(int orderId) throws Exception;
    boolean insert(OrderDetail od) throws Exception;
}
