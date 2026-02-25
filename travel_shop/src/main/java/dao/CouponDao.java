package dao;

import model.Coupon;
import java.util.List;

public interface CouponDao {
    Coupon findByCode(String code);
    List<Coupon> findAllActive();
}
