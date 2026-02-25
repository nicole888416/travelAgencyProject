package service;

import model.Employee;
import java.util.List;

public interface EmployeeService {
    Employee login(String username, String password);
    Employee register(Employee e);
    Employee refresh(int id);
    boolean update(Employee e);
    boolean updatePhoto(int employeeId, String photoPath);
    boolean deletePhoto(int employeeId);
    List<Employee> listAll();
}
