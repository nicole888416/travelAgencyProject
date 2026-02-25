package service.impl;

import dao.ProductDao;
import dao.impl.ProductDaoImpl;
import model.Product;
import util.Validator;

import java.util.List;

public class ProductServiceImpl implements service.ProductService {

    private final ProductDao dao = new ProductDaoImpl();

    @Override public List<Product> listAll() { return dao.findAll(); }
    @Override public Product findById(int id) { return dao.findById(id); }

    @Override
    public Product create(Product p) {
        Validator.requireNotBlank(p.getProductNo(), "商品編號");
        Validator.requireNotBlank(p.getProductName(), "商品名稱");
        if (p.getProductPrice() <= 0) throw new IllegalArgumentException("價格必須 > 0");
        if (p.getProductStock() < 0) throw new IllegalArgumentException("庫存不可為負數");
        Validator.requireNotBlank(p.getDescription(), "商品描述");

        if (dao.existsProductNo(p.getProductNo().trim())) throw new IllegalArgumentException("product_no 不可重複");
        p.setProductNo(p.getProductNo().trim());
        int id = dao.create(p);
        return dao.findById(id);
    }

    @Override
    public boolean update(Product p) {
        Validator.requireNotBlank(p.getProductName(), "商品名稱");
        if (p.getProductPrice() <= 0) throw new IllegalArgumentException("價格必須 > 0");
        if (p.getProductStock() < 0) throw new IllegalArgumentException("庫存不可為負數");
        Validator.requireNotBlank(p.getDescription(), "商品描述");
        return dao.update(p);
    }

    @Override public boolean delete(int id) { return dao.delete(id); }
}
