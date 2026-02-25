package service;

import model.Product;
import java.util.List;

public interface ProductService {
    java.util.List<Product> listAll();
    Product findById(int id);
    Product create(Product p);
    boolean update(Product p);
    boolean delete(int id);
}
