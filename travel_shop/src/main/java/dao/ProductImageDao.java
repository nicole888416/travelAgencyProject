package dao;

import java.util.List;

public interface ProductImageDao {
    boolean upsert(int productId, int imgIndex, String imgPath);
    boolean delete(int productId, int imgIndex);
    List<String> findPaths(int productId);
    String findFirstPath(int productId);
}
