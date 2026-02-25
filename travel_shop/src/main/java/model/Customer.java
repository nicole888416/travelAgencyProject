package model;

import java.util.Date;

public class Customer {
    private int id;
    private String customerNo;
    private String customerName;
    private String phone;
    private String email;
    private Date birthday;
    private String addressCity;
    private String addressDetail;
    private String username;
    private String password;
    private String memberLevel; // BRONZE/SILVER/GOLD/PLATINUM
    private int totalSpent;
    private String photoPath;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerNo() { return customerNo; }
    public void setCustomerNo(String customerNo) { this.customerNo = customerNo; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getMemberLevel() { return memberLevel; }
    public void setMemberLevel(String memberLevel) { this.memberLevel = memberLevel; }
    public int getTotalSpent() { return totalSpent; }
    public void setTotalSpent(int totalSpent) { this.totalSpent = totalSpent; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public MemberLevel levelEnum() { return MemberLevel.fromDb(memberLevel); }
}
