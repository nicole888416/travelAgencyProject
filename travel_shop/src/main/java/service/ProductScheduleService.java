package service;

import model.ProductSchedule;
import java.util.Date;
import java.util.List;

public interface ProductScheduleService {
    List<ProductSchedule> listByProduct(int productId);
    ProductSchedule create(ProductSchedule s);
    boolean update(ProductSchedule s);
    boolean delete(int id);
    ProductSchedule findById(int id);
    List<ProductSchedule> listByDateRange(Date from, Date to);
}
