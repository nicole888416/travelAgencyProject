package controller;

import model.Customer;
import model.Employee;
import model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class AppSession {
    private static Customer customer;
    private static Employee employee;

    private static final List<OrderItem> cart = new ArrayList<>();
    private static String selectedCouponCode;
    private static Integer selectedEmployeeId;

    public static Customer getCustomer() { return customer; }
    public static void setCustomer(Customer c) { customer = c; }

    public static Employee getEmployee() { return employee; }
    public static void setEmployee(Employee e) { employee = e; }

    public static List<OrderItem> getCart() { return cart; }
    public static void clearCart() { cart.clear(); }

    public static String getSelectedCouponCode() { return selectedCouponCode; }
    public static void setSelectedCouponCode(String code) { selectedCouponCode = code; }

    public static Integer getSelectedEmployeeId() { return selectedEmployeeId; }
    public static void setSelectedEmployeeId(Integer id) { selectedEmployeeId = id; }
}
