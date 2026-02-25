package dao;

import model.OrderDetail;
import java.sql.Connection;

public interface OrderDetailDao {
    boolean insert(OrderDetail d, Connection conn) throws Exception;
}
