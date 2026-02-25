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
import java.io.File;
import java.util.Objects;

public class AddEmployeeUI extends BaseFrame {

    private final EmployeeService service = new EmployeeServiceImpl();

    private JTextField txtName, txtPhone, txtEmail, txtAddressDetail, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbCity, cmbRole;
    private JDateChooser dcBirthday;

    private JLabel lblPhoto;
    private String photoPath;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public AddEmployeeUI() {

        setTitle("註冊員工");
        setSize(1200, 750);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== Banner =====
        BannerPanel banner = new BannerPanel("/images/hero_admin.png", "/images/logo.png");
        banner.setPreferredSize(new Dimension(1200,180));
        banner.setBorder(BorderFactory.createLineBorder(GOLD,2));
        add(banner, BorderLayout.NORTH);

        // ===== 主內容區 =====
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(LIGHT_BG);
        content.setBorder(new EmptyBorder(25,25,25,25));
        add(content, BorderLayout.CENTER);

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

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("employee_no(自動產生):"), gc);
        gc.gridx=1; leftCard.add(whiteLabel("(建立後自動產生)"), gc); y++;

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

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("username(不可重複):"), gc);
        gc.gridx=1; txtUsername = new JTextField(); leftCard.add(txtUsername, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("password:"), gc);
        gc.gridx=1; txtPassword = new JPasswordField(); leftCard.add(txtPassword, gc); y++;

        gc.gridx=0; gc.gridy=y; leftCard.add(whiteLabel("role:"), gc);
        gc.gridx=1; cmbRole = new JComboBox<>(new String[]{Role.STAFF.name(), Role.GM.name()}); leftCard.add(cmbRole, gc); y++;

        content.add(leftCard, BorderLayout.CENTER);

        // ===== 右側照片區 =====
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(340, 0));
        right.setBackground(NAVY);
        right.setBorder(new LineBorder(GOLD,2));

        lblPhoto = new JLabel("預設人像", SwingConstants.CENTER);
        lblPhoto.setForeground(Color.WHITE);
        right.add(lblPhoto, BorderLayout.CENTER);

        JPanel photoBtns = new JPanel(new GridLayout(1,3,10,10));
        photoBtns.setBorder(new EmptyBorder(15,15,15,15));
        photoBtns.setBackground(NAVY);

        JButton btnAdd = goldBtn("新增");
        JButton btnEdit = goldBtn("修改");
        JButton btnDel = goldBtn("刪除");

        photoBtns.add(btnAdd);
        photoBtns.add(btnEdit);
        photoBtns.add(btnDel);

        right.add(photoBtns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> choosePhoto());
        btnEdit.addActionListener(e -> choosePhoto());
        btnDel.addActionListener(e -> deletePhotoPreview());

        content.add(right, BorderLayout.EAST);

        // ===== 底部 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,25,15));
        bottom.setBackground(NAVY);

        JButton btnSave = goldBtn("註冊");
        JButton btnBack = goldBtn("返回登入");

        bottom.add(btnSave);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> doSave());
        btnBack.addActionListener(e -> go(new controller.common.LoginUI()));

        setLocationRelativeTo(null);
    }

    // ===== Banner Panel =====
    static class BannerPanel extends JPanel {

        private final Image bg;
        private final Image logo;

        BannerPanel(String bgPath, String logoPath) {
            Image _bg=null,_logo=null;
            try {
                _bg = new ImageIcon(Objects.requireNonNull(getClass().getResource(bgPath))).getImage();
                _logo = new ImageIcon(Objects.requireNonNull(getClass().getResource(logoPath))).getImage();
            } catch(Exception e){ e.printStackTrace(); }
            bg=_bg; logo=_logo;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(bg!=null) g.drawImage(bg,0,0,getWidth(),getHeight(),this);
            if(logo!=null){
                int h=(int)(getHeight()*0.6);
                int ow=logo.getWidth(this);
                int oh=logo.getHeight(this);
                if(ow>0&&oh>0){
                    int w=(int)((double)h*ow/oh);
                    int x=(getWidth()-w)/2;
                    int y=(getHeight()-h)/2;
                    g.drawImage(logo,x,y,w,h,this);
                }
            }
        }
    }

    private JLabel whiteLabel(String text){
        JLabel l=new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton goldBtn(String text){
        JButton b=new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei",Font.BOLD,14));
        return b;
    }

    private void choosePhoto() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            photoPath = f.getAbsolutePath();
            refreshPhotoPreview();
        }
    }

    private void deletePhotoPreview() {
        photoPath = null;
        refreshPhotoPreview();
    }

    private void refreshPhotoPreview() {
        ImageIcon icon = ImageUtil.scaledIcon(photoPath, 240, 240);
        if (icon == null) {
            lblPhoto.setIcon(null);
            lblPhoto.setText("預設人像");
        } else {
            lblPhoto.setText("");
            lblPhoto.setIcon(icon);
        }
    }

    private void doSave() {
        try {
            Employee e = new Employee();
            e.setEmployeeName(txtName.getText());
            e.setPhone(txtPhone.getText());
            e.setEmail(txtEmail.getText());
            e.setBirthday(dcBirthday.getDate());
            e.setAddressCity(String.valueOf(cmbCity.getSelectedItem()));
            e.setAddressDetail(txtAddressDetail.getText());
            e.setUsername(txtUsername.getText());
            e.setPassword(new String(txtPassword.getPassword()));
            e.setRole(String.valueOf(cmbRole.getSelectedItem()));
            e.setPhotoPath(photoPath);

            Employee saved = service.register(e);
            AppSession.setEmployee(saved);
            Message.info(this, "註冊成功，已自動登入");
            go(new EmployeeUI());

        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }
}