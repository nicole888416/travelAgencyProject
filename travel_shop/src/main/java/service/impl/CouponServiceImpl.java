package service.impl;

import dao.CouponDao;
import dao.impl.CouponDaoImpl;
import model.Coupon;

import java.util.List;

public class CouponServiceImpl implements service.CouponService {

    private final CouponDao dao = new CouponDaoImpl();

    @Override public List<Coupon> listActive() { return dao.findAllActive(); }

    @Override
    public Coupon findByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        return dao.findByCode(code.trim().toUpperCase());
    }
}
