package service;

import model.Coupon;
import java.util.List;

public interface CouponService {
    List<Coupon> listActive();
    Coupon findByCode(String code);
}
