package service.impl;

import dao.ProductImageDao;
import dao.impl.ProductImageDaoImpl;

import java.util.List;

public class ProductImageServiceImpl implements service.ProductImageService {

    private final ProductImageDao dao = new ProductImageDaoImpl();

    @Override
    public boolean upsert(int productId, int imgIndex, String imgPath) {
        if (imgIndex < 1 || imgIndex > 5) throw new IllegalArgumentException("圖片最多 5 張，index 必須 1~5");
        if (imgPath == null || imgPath.trim().isEmpty()) throw new IllegalArgumentException("圖片路徑不可空白");
        return dao.upsert(productId, imgIndex, imgPath);
    }

    @Override public boolean delete(int productId, int imgIndex) { return dao.delete(productId, imgIndex); }
    @Override public List<String> listPaths(int productId) { return dao.findPaths(productId); }
    @Override public String firstPath(int productId) { return dao.findFirstPath(productId); }
}
