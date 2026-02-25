package service;

import java.util.List;

public interface ProductImageService {
    boolean upsert(int productId, int imgIndex, String imgPath);
    boolean delete(int productId, int imgIndex);
    List<String> listPaths(int productId);
    String firstPath(int productId);
}
