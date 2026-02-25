package controller.customer;

import controller.AppSession;
import controller.common.BaseFrame;
import controller.common.Message;
import model.Customer;
import service.CustomerService;
import service.impl.CustomerServiceImpl;
import util.ImageUtil;
import util.TaiwanCity;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EditCustomerUI extends BaseFrame {

    private final CustomerService service = new CustomerServiceImpl();

    private JTextField txtCustomerNo, txtName, txtPhone, txtEmail, txtAddressDetail, txtUsername, txtMemberLevel;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbCity;
    private JDateChooser dcBirthday;
    private JLabel lblPhoto;

    private final Color DARK_BLUE = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);

    public EditCustomerUI() {
    	


        setTitle("修改會員資料");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230,235,245));

        Customer c = AppSession.getCustomer();
        if (c == null) { Message.error(this, "尚未登入"); go(new controller.common.LoginUI()); return; }

        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(new EmptyBorder(20,30,20,30));
        left.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int y=0;
        gc.gridx=0; gc.gridy=y; left.add(new JLabel("customer_no(不可更改):"), gc);
        gc.gridx=1; txtCustomerNo = new JTextField(); txtCustomerNo.setEnabled(false); left.add(txtCustomerNo, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("customer_name:"), gc);
        gc.gridx=1; txtName = new JTextField(); left.add(txtName, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("phone:"), gc);
        gc.gridx=1; txtPhone = new JTextField(); left.add(txtPhone, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("email:"), gc);
        gc.gridx=1; txtEmail = new JTextField(); left.add(txtEmail, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("birthday:"), gc);
        gc.gridx=1; dcBirthday = new JDateChooser(); dcBirthday.setDateFormatString("yyyy-MM-dd"); left.add(dcBirthday, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("address_city:"), gc);
        gc.gridx=1; cmbCity = new JComboBox<>(TaiwanCity.CITIES); left.add(cmbCity, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("address_detail:"), gc);
        gc.gridx=1; txtAddressDetail = new JTextField(); left.add(txtAddressDetail, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("username(不可更改):"), gc);
        gc.gridx=1; txtUsername = new JTextField(); txtUsername.setEnabled(false); left.add(txtUsername, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("password:"), gc);
        gc.gridx=1; txtPassword = new JPasswordField(); left.add(txtPassword, gc); y++;

        gc.gridx=0; gc.gridy=y; left.add(new JLabel("member_level(顯示):"), gc);
        gc.gridx=1; txtMemberLevel = new JTextField(); txtMemberLevel.setEnabled(false); left.add(txtMemberLevel, gc); y++;

        add(left, BorderLayout.CENTER);
        
    	JPanel top = new JPanel();
        top.setPreferredSize(new Dimension(0,30));
        top.setBackground(DARK_BLUE);
        add(top,BorderLayout.NORTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(300, 0));
        right.setBackground(DARK_BLUE);
        right.setBorder(new LineBorder(GOLD, 2));

        lblPhoto = new JLabel("", SwingConstants.CENTER);
        lblPhoto.setForeground(Color.WHITE);
        right.add(lblPhoto, BorderLayout.CENTER);

        JPanel photoBtns = new JPanel(new GridLayout(1,3,8,8));
        photoBtns.setBackground(DARK_BLUE);
        photoBtns.setBorder(new EmptyBorder(10,10,10,10));

        JButton btnAdd = goldBtn("新增");
        JButton btnEdit = goldBtn("修改");
        JButton btnDel = goldBtn("刪除");

        photoBtns.add(btnAdd);
        photoBtns.add(btnEdit);
        photoBtns.add(btnDel);

        right.add(photoBtns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e1 -> chooseAndSavePhoto());
        btnEdit.addActionListener(e1 -> chooseAndSavePhoto());
        btnDel.addActionListener(e1 -> deletePhoto());

        add(right, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        bottom.setBackground(DARK_BLUE);

        JButton btnSave = goldBtn("儲存");
        JButton btnBack = goldBtn("返回會員中心");

        bottom.add(btnSave);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        btnSave.addActionListener(e1 -> doSave());
        btnBack.addActionListener(e1 -> go(new CustomerUI()));

        setLocationRelativeTo(null);
        load();
    }

    private JButton goldBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        return b;
    }

    private void load() {
        Customer c = service.refresh(AppSession.getCustomer().getId());
        AppSession.setCustomer(c);

        txtCustomerNo.setText(c.getCustomerNo());
        txtName.setText(c.getCustomerName());
        txtPhone.setText(c.getPhone());
        txtEmail.setText(c.getEmail());
        dcBirthday.setDate(c.getBirthday());
        cmbCity.setSelectedItem(c.getAddressCity());
        txtAddressDetail.setText(c.getAddressDetail());
        txtUsername.setText(c.getUsername());
        txtPassword.setText(c.getPassword());
        txtMemberLevel.setText(c.levelEnum().name());

        ImageIcon icon = ImageUtil.scaledIcon(c.getPhotoPath(), 240, 240);
        if (icon == null) { lblPhoto.setIcon(null); lblPhoto.setText("預設人像"); }
        else { lblPhoto.setText(""); lblPhoto.setIcon(icon); }
    }

    private void doSave() {
        try {
            Customer c = AppSession.getCustomer();
            c.setCustomerName(txtName.getText());
            c.setPhone(txtPhone.getText());
            c.setEmail(txtEmail.getText());
            c.setBirthday(dcBirthday.getDate());
            c.setAddressCity(String.valueOf(cmbCity.getSelectedItem()));
            c.setAddressDetail(txtAddressDetail.getText());
            c.setPassword(new String(txtPassword.getPassword()));

            service.update(c);
            Message.info(this, "更新成功");
            go(new CustomerUI());
        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }

    private void chooseAndSavePhoto() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            service.updatePhoto(AppSession.getCustomer().getId(), path);
            load();
        }
    }

    private void deletePhoto() {
        service.deletePhoto(AppSession.getCustomer().getId());
        load();
    }
}