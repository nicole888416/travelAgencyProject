package dao;

import model.Employee;
import java.util.List;

public interface EmployeeDao {
    Employee findByUsernameAndPassword(String username, String password);
    Employee findById(int id);
    Employee findByUsername(String username);
    boolean existsUsername(String username);
    boolean existsPhone(String phone);
    boolean existsEmail(String email);
    int create(Employee e);
    boolean update(Employee e);
    boolean updatePhoto(int employeeId, String photoPath);
    boolean deletePhoto(int employeeId);
    List<Employee> findAll();
}
