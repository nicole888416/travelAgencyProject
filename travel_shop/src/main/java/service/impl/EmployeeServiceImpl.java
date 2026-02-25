package service.impl;

import dao.EmployeeDao;
import dao.impl.EmployeeDaoImpl;
import model.Employee;
import model.Role;
import util.IdUtil;
import util.Validator;

import java.util.List;

public class EmployeeServiceImpl implements service.EmployeeService {

    private final EmployeeDao dao = new EmployeeDaoImpl();

    @Override
    public Employee login(String username, String password) {
        Validator.requireNotBlank(username, "帳號");
        Validator.requireNotBlank(password, "密碼");
        return dao.findByUsernameAndPassword(username.trim(), password.trim());
    }

    @Override
    public Employee register(Employee e) {
        Validator.requireNotBlank(e.getEmployeeName(), "姓名");
        Validator.validatePhone(e.getPhone());
        Validator.validateEmail(e.getEmail());
        if (e.getBirthday() == null) throw new IllegalArgumentException("生日不可為空白");
        Validator.requireNotBlank(e.getAddressCity(), "縣市");
        Validator.requireNotBlank(e.getAddressDetail(), "地址");
        Validator.requireNotBlank(e.getUsername(), "帳號");
        Validator.requireNotBlank(e.getPassword(), "密碼");

        String username = e.getUsername().trim();
        if (dao.existsUsername(username)) throw new IllegalArgumentException("此員工帳號已存在");
        if (dao.existsPhone(e.getPhone().trim())) throw new IllegalArgumentException("此手機已被使用");
        if (dao.existsEmail(e.getEmail().trim())) throw new IllegalArgumentException("此 Email 已被使用");

        e.setUsername(username);
        e.setPhone(e.getPhone().trim());
        e.setEmail(e.getEmail().trim());

        if (e.getRole() == null || e.getRole().trim().isEmpty()) e.setRole(Role.STAFF.name());

        e.setEmployeeNo("TEMP");
        int id = dao.create(e);
        if (id <= 0) throw new RuntimeException("建立員工失敗");
        e.setId(id);

        e.setEmployeeNo(IdUtil.employeeNo(id));
        dao.update(e);

        return dao.findById(id);
    }

    @Override public Employee refresh(int id) { return dao.findById(id); }

    @Override
    public boolean update(Employee e) {
        Validator.requireNotBlank(e.getEmployeeName(), "姓名");
        Validator.validatePhone(e.getPhone());
        Validator.validateEmail(e.getEmail());
        if (e.getBirthday() == null) throw new IllegalArgumentException("生日不可為空白");
        Validator.requireNotBlank(e.getAddressCity(), "縣市");
        Validator.requireNotBlank(e.getAddressDetail(), "地址");
        Validator.requireNotBlank(e.getPassword(), "密碼");
        Validator.requireNotBlank(e.getRole(), "角色");
        return dao.update(e);
    }

    @Override public boolean updatePhoto(int employeeId, String photoPath) { return dao.updatePhoto(employeeId, photoPath); }
    @Override public boolean deletePhoto(int employeeId) { return dao.deletePhoto(employeeId); }
    @Override public List<Employee> listAll() { return dao.findAll(); }
}
