package dao;

import model.ProductSchedule;
import java.util.Date;
import java.util.List;

public interface ProductScheduleDao {
    int create(ProductSchedule s);
    boolean update(ProductSchedule s);
    boolean delete(int id);
    List<ProductSchedule> findByProductId(int productId);
    List<ProductSchedule> findByDateRange(Date from, Date to);
    ProductSchedule findById(int id);
}
