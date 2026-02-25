package service.impl;

import dao.CustomerDao;
import dao.impl.CustomerDaoImpl;
import model.Customer;
import model.MemberLevel;
import util.IdUtil;
import util.Validator;

public class CustomerServiceImpl implements service.CustomerService {

    private final CustomerDao dao = new CustomerDaoImpl();

    @Override
    public Customer login(String username, String password) {
        Validator.requireNotBlank(username, "帳號");
        Validator.requireNotBlank(password, "密碼");
        return dao.findByUsernameAndPassword(username.trim(), password.trim());
    }

    @Override
    public Customer register(Customer c) {
        Validator.requireNotBlank(c.getCustomerName(), "姓名");
        Validator.validatePhone(c.getPhone());
        Validator.validateEmail(c.getEmail());
        if (c.getBirthday() == null) throw new IllegalArgumentException("生日不可為空白");
        Validator.requireNotBlank(c.getAddressCity(), "縣市");
        Validator.requireNotBlank(c.getAddressDetail(), "地址");
        Validator.requireNotBlank(c.getUsername(), "帳號");
        Validator.requireNotBlank(c.getPassword(), "密碼");

        String username = c.getUsername().trim();
        if (dao.existsUsername(username)) throw new IllegalArgumentException("此會員帳號已存在");
        if (dao.existsPhone(c.getPhone().trim())) throw new IllegalArgumentException("此手機已被使用");
        if (dao.existsEmail(c.getEmail().trim())) throw new IllegalArgumentException("此 Email 已被使用");

        c.setUsername(username);
        c.setPhone(c.getPhone().trim());
        c.setEmail(c.getEmail().trim());
        c.setMemberLevel(MemberLevel.BRONZE.name());
        c.setTotalSpent(0);

        c.setCustomerNo("TEMP");
        int id = dao.create(c);
        if (id <= 0) throw new RuntimeException("建立會員失敗");
        c.setId(id);

        c.setCustomerNo(IdUtil.customerNo(id));
        dao.update(c);

        return dao.findById(id);
    }

    @Override public Customer refresh(int id) { return dao.findById(id); }

    @Override
    public boolean update(Customer c) {
        Validator.requireNotBlank(c.getCustomerName(), "姓名");
        Validator.validatePhone(c.getPhone());
        Validator.validateEmail(c.getEmail());
        if (c.getBirthday() == null) throw new IllegalArgumentException("生日不可為空白");
        Validator.requireNotBlank(c.getAddressCity(), "縣市");
        Validator.requireNotBlank(c.getAddressDetail(), "地址");
        Validator.requireNotBlank(c.getPassword(), "密碼");
        return dao.update(c);
    }

    @Override public boolean updatePhoto(int customerId, String photoPath) { return dao.updatePhoto(customerId, photoPath); }
    @Override public boolean deletePhoto(int customerId) { return dao.deletePhoto(customerId); }
}
