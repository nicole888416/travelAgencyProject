package dao;

import model.Orders;
import java.util.Date;
import java.util.List;

public interface OrdersDao {
    int create(Orders o);
    boolean updateStatus(int orderId, String status);
    Orders findById(int id);
    Orders findByOrderNo(String orderNo);
    List<Orders> findByCustomerId(int customerId);
    List<Orders> findByEmployeeId(int employeeId);
    List<Orders> findByDateRange(Date from, Date to);
    List<Orders> findByMonth(int year, int month);
    List<Orders> findAll();
    boolean delete(int id);

}
