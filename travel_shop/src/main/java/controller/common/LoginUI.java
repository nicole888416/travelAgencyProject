package controller.common;

import controller.AppSession;
import controller.customer.AddCustomerUI;
import controller.customer.CustomerUI;
import controller.employee.AddEmployeeUI;
import controller.employee.EmployeeUI;
import model.Customer;
import model.Employee;
import service.CustomerService;
import service.EmployeeService;
import service.impl.CustomerServiceImpl;
import service.impl.EmployeeServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class LoginUI extends BaseFrame {

    private final CustomerService customerService = new CustomerServiceImpl();
    private final EmployeeService employeeService = new EmployeeServiceImpl();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbType;
    private Image backgroundImage;

    public LoginUI() {

        setTitle("Luxury Travel - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        try {
            URL bgUrl = getClass().getResource("/images/login_bg.png");
            backgroundImage = new ImageIcon(bgUrl).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        JPanel loginBox = new JPanel();
        loginBox.setPreferredSize(new Dimension(350, 450));
        loginBox.setBackground(new Color(15, 35, 70, 220));
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBorder(new EmptyBorder(30, 40, 30, 40));

        // ===== LOGO (完全不動大小) =====
        try {
            URL logoUrl = getClass().getResource("/images/logo.png");
            ImageIcon logoIcon = new ImageIcon(logoUrl);

            Image scaled = logoIcon.getImage().getScaledInstance(
                    logoIcon.getIconWidth() * 1 / 5,
                    logoIcon.getIconHeight() * 1 / 5,
                    Image.SCALE_SMOOTH
            );

            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginBox.add(lblLogo);
            loginBox.add(Box.createVerticalStrut(20));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== 登入身分 =====
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        typePanel.setOpaque(false);
        JLabel lblType = new JLabel("登入身分:");
        lblType.setForeground(Color.WHITE);
        cmbType = new JComboBox<>(new String[]{"會員", "員工"});
        typePanel.add(lblType);
        typePanel.add(cmbType);
        loginBox.add(typePanel);

        loginBox.add(Box.createVerticalStrut(15));

        // ===== 帳號 (label 左側) =====
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(210, 35));
        txtUsername.setMaximumSize(new Dimension(210, 35));

        loginBox.add(makeRowField("帳號:", txtUsername));
        loginBox.add(Box.createVerticalStrut(15));

        // ===== 密碼 (label 左側) =====
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(210, 35));
        txtPassword.setMaximumSize(new Dimension(210, 35));

        loginBox.add(makeRowField("密碼:", txtPassword));
        loginBox.add(Box.createVerticalStrut(25));

        // ===== 登入按鈕 (縮 30%) =====
        JButton btnLogin = new JButton("登入");
        btnLogin.setBackground(new Color(201, 162, 39));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(210, 40));
        loginBox.add(btnLogin);

        loginBox.add(Box.createVerticalStrut(20));

        // ===== 註冊按鈕改為水平 =====
        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        regPanel.setOpaque(false);

        JButton btnRegCustomer = new JButton("註冊會員");
        JButton btnRegEmployee = new JButton("註冊員工");

        btnRegCustomer.setBackground(new Color(20, 40, 90));
        btnRegEmployee.setBackground(new Color(20, 40, 90));
        btnRegCustomer.setForeground(Color.WHITE);
        btnRegEmployee.setForeground(Color.WHITE);

        regPanel.add(btnRegCustomer);
        regPanel.add(btnRegEmployee);

        loginBox.add(regPanel);

        mainPanel.add(loginBox);

        btnLogin.addActionListener(e -> doLogin());
        btnRegCustomer.addActionListener(e -> go(new AddCustomerUI()));
        btnRegEmployee.addActionListener(e -> go(new AddEmployeeUI()));
    }

    // 橫向 label + field
    private JPanel makeRowField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);

        panel.add(label);
        panel.add(field);

        return panel;
    }

    class BackgroundPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null)
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void doLogin() {
        try {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if ("會員".equals(String.valueOf(cmbType.getSelectedItem()))) {
                Customer c = customerService.login(username, password);
                if (c == null) {
                    Message.error(this, "會員登入失敗，請重新登入或註冊");
                    return;
                }
                AppSession.setCustomer(c);
                AppSession.setEmployee(null);
                go(new CustomerUI());
            } else {
                Employee emp = employeeService.login(username, password);
                if (emp == null) {
                    Message.error(this, "員工登入失敗，請重新登入或註冊");
                    return;
                }
                AppSession.setEmployee(emp);
                AppSession.setCustomer(null);
                go(new EmployeeUI());
            }
        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }
}