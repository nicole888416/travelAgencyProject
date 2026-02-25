package service.impl;

import dao.ProductScheduleDao;
import dao.impl.ProductScheduleDaoImpl;
import model.ProductSchedule;

import java.util.Date;
import java.util.List;

public class ProductScheduleServiceImpl implements service.ProductScheduleService {

    private final ProductScheduleDao dao = new ProductScheduleDaoImpl();

    @Override public List<ProductSchedule> listByProduct(int productId) { return dao.findByProductId(productId); }

    @Override
    public ProductSchedule create(ProductSchedule s) {
        if (s.getDepartDate()==null || s.getReturnDate()==null) throw new IllegalArgumentException("日期不可為空白");
        if (s.getSeatStock()<0) throw new IllegalArgumentException("機位不可為負數");
        int id = dao.create(s);
        return dao.findById(id);
    }

    @Override
    public boolean update(ProductSchedule s) {
        if (s.getDepartDate()==null || s.getReturnDate()==null) throw new IllegalArgumentException("日期不可為空白");
        if (s.getSeatStock()<0) throw new IllegalArgumentException("機位不可為負數");
        return dao.update(s);
    }

    @Override public boolean delete(int id) { return dao.delete(id); }
    @Override public ProductSchedule findById(int id) { return dao.findById(id); }
    @Override public List<ProductSchedule> listByDateRange(Date from, Date to) { return dao.findByDateRange(from, to); }
}
