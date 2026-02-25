package service;

import model.Customer;

public interface CustomerService {
    Customer login(String username, String password);
    Customer register(Customer c);
    Customer refresh(int id);
    boolean update(Customer c);
    boolean updatePhoto(int customerId, String photoPath);
    boolean deletePhoto(int customerId);
}
