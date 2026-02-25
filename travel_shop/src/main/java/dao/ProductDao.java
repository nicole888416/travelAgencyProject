package dao;

import model.Product;
import java.util.List;

public interface ProductDao {
    Product findById(int id);
    Product findByProductNo(String productNo);
    boolean existsProductNo(String productNo);
    int create(Product p);
    boolean update(Product p);
    boolean delete(int id);
    java.util.List<Product> findAll();
}
