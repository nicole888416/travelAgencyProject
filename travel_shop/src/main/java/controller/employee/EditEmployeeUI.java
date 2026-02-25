package controller.employee;

import controller.AppSession;
import controller.common.BaseFrame;
import controller.common.Message;
import model.Employee;
import model.Role;
import service.EmployeeService;
import service.impl.EmployeeServiceImpl;
import util.ImageUtil;
import util.TaiwanCity;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EditEmployeeUI extends BaseFrame {

    private final EmployeeService service = new EmployeeServiceImpl();

    private JTextField txtEmployeeNo, txtName, txtPhone, txtEmail, txtAddressDetail, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbCity, cmbRole;
    private JDateChooser dcBirthday;
    private JLabel lblPhoto;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public EditEmployeeUI() {

        setTitle("修改員工資料");
        setSize(1100, 650);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        Employee e = AppSession.getEmployee();
        if (e == null) {
            Message.error(this, "尚未登入");
            go(new controller.common.LoginUI());
            return;
        }

        // ===== 左側表單卡片 =====
        JPanel leftCard = new JPanel(new GridBagLayout());
        leftCard.setBackground(NAVY);
        leftCard.setBorder(new CompoundBorder(
                new LineBorder(GOLD,2),
                new EmptyBorder(25,30,25,30)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int y=0;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("employee_no(不可更改):"), gc);
        gc.gridx=1; txtEmployeeNo = new JTextField(); txtEmployeeNo.setEnabled(false); leftCard.add(txtEmployeeNo, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("employee_name:"), gc);
        gc.gridx=1; txtName = new JTextField(); leftCard.add(txtName, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("phone:"), gc);
        gc.gridx=1; txtPhone = new JTextField(); leftCard.add(txtPhone, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("email:"), gc);
        gc.gridx=1; txtEmail = new JTextField(); leftCard.add(txtEmail, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("birthday:"), gc);
        gc.gridx=1; dcBirthday = new JDateChooser(); dcBirthday.setDateFormatString("yyyy-MM-dd"); leftCard.add(dcBirthday, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("address_city:"), gc);
        gc.gridx=1; cmbCity = new JComboBox<>(TaiwanCity.CITIES); leftCard.add(cmbCity, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("address_detail:"), gc);
        gc.gridx=1; txtAddressDetail = new JTextField(); leftCard.add(txtAddressDetail, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("username(不可更改):"), gc);
        gc.gridx=1; txtUsername = new JTextField(); txtUsername.setEnabled(false); leftCard.add(txtUsername, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("password:"), gc);
        gc.gridx=1; txtPassword = new JPasswordField(); leftCard.add(txtPassword, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("role:"), gc);
        gc.gridx=1; cmbRole = new JComboBox<>(new String[]{Role.STAFF.name(), Role.GM.name()}); leftCard.add(cmbRole, gc); y++;

        add(leftCard, BorderLayout.CENTER);

        // ===== 右側照片區 =====
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(320, 0));
        right.setBackground(NAVY);
        right.setBorder(new LineBorder(GOLD,2));

        lblPhoto = new JLabel("", SwingConstants.CENTER);
        lblPhoto.setForeground(Color.WHITE);
        right.add(lblPhoto, BorderLayout.CENTER);

        JPanel photoBtns = new JPanel(new GridLayout(1,3,10,10));
        photoBtns.setBorder(new EmptyBorder(10,10,10,10));
        photoBtns.setBackground(NAVY);

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

        // ===== 底部按鈕區 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,15));
        bottom.setBackground(NAVY);

        JButton btnSave = goldBtn("儲存");
        JButton btnBack = goldBtn("返回員工中心");

        bottom.add(btnSave);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        btnSave.addActionListener(e1 -> doSave());
        btnBack.addActionListener(e1 -> go(new EmployeeUI()));

        setLocationRelativeTo(null);
        load();
    }

    private JLabel whiteLabel(String text){
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton goldBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        return b;
    }

    private void load() {
        Employee e = service.refresh(AppSession.getEmployee().getId());
        AppSession.setEmployee(e);

        txtEmployeeNo.setText(e.getEmployeeNo());
        txtName.setText(e.getEmployeeName());
        txtPhone.setText(e.getPhone());
        txtEmail.setText(e.getEmail());
        dcBirthday.setDate(e.getBirthday());
        cmbCity.setSelectedItem(e.getAddressCity());
        txtAddressDetail.setText(e.getAddressDetail());
        txtUsername.setText(e.getUsername());
        txtPassword.setText(e.getPassword());
        cmbRole.setSelectedItem(e.getRole());

        ImageIcon icon = ImageUtil.scaledIcon(e.getPhotoPath(), 240, 240);
        if (icon == null) {
            lblPhoto.setText("預設人像");
        } else {
            lblPhoto.setText("");
            lblPhoto.setIcon(icon);
        }
    }

    private void doSave() {
        try {
            Employee e = AppSession.getEmployee();
            e.setEmployeeName(txtName.getText());
            e.setPhone(txtPhone.getText());
            e.setEmail(txtEmail.getText());
            e.setBirthday(dcBirthday.getDate());
            e.setAddressCity(String.valueOf(cmbCity.getSelectedItem()));
            e.setAddressDetail(txtAddressDetail.getText());
            e.setPassword(new String(txtPassword.getPassword()));
            e.setRole(String.valueOf(cmbRole.getSelectedItem()));

            service.update(e);
            Message.info(this, "更新成功");
            go(new EmployeeUI());

        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }

    private void chooseAndSavePhoto() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            service.updatePhoto(AppSession.getEmployee().getId(), path);
            load();
        }
    }

    private void deletePhoto() {
        service.deletePhoto(AppSession.getEmployee().getId());
        load();
    }
}