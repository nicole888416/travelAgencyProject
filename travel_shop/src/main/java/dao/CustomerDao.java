package dao;

import model.Customer;
import java.util.List;

public interface CustomerDao {
    Customer findByUsernameAndPassword(String username, String password);
    Customer findById(int id);
    Customer findByUsername(String username);
    boolean existsUsername(String username);
    boolean existsPhone(String phone);
    boolean existsEmail(String email);
    int create(Customer c);
    boolean update(Customer c);
    boolean updatePhoto(int customerId, String photoPath);
    boolean deletePhoto(int customerId);
    List<Customer> findAll();
}
