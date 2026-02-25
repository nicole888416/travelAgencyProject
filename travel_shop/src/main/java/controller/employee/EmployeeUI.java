package controller.employee;

import controller.AppSession;
import controller.common.BaseFrame;
import controller.common.Message;
import model.Employee;
import model.Role;
import service.EmployeeService;
import service.impl.EmployeeServiceImpl;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Objects;

public class EmployeeUI extends BaseFrame {

    private final EmployeeService service = new EmployeeServiceImpl();

    private JLabel lblInfo;
    private JLabel lblPhoto;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public EmployeeUI() {

        setTitle("員工中心");
        setSize(1200,720);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        Employee emp = AppSession.getEmployee();
        if (emp == null) {
            Message.error(this, "尚未登入");
            go(new controller.common.LoginUI());
            return;
        }

        // ===== Banner =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(NAVY);

        BannerPanel banner = new BannerPanel("/images/hero_admin.png", "/images/logo.png");
        banner.setPreferredSize(new Dimension(1200,180));
        banner.setBorder(BorderFactory.createLineBorder(GOLD,2));

        top.add(banner, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== 中央區 =====
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(LIGHT_BG);
        center.setBorder(new EmptyBorder(20,20,20,20));
        add(center, BorderLayout.CENTER);

        // 左側資訊卡
        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(new CompoundBorder(
                new LineBorder(GOLD,2),
                new EmptyBorder(20,25,20,25)
        ));

        lblInfo = new JLabel();
        lblInfo.setVerticalAlignment(SwingConstants.TOP);
        infoCard.add(lblInfo,BorderLayout.CENTER);

        center.add(infoCard,BorderLayout.CENTER);

        // 右側照片區
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(350,0));
        right.setBackground(NAVY);
        right.setBorder(new LineBorder(GOLD,2));
        center.add(right,BorderLayout.EAST);

        lblPhoto = new JLabel("",SwingConstants.CENTER);
        lblPhoto.setForeground(Color.WHITE);
        right.add(lblPhoto,BorderLayout.CENTER);

        JPanel photoBtns = new JPanel(new GridLayout(1,3,10,10));
        photoBtns.setBorder(new EmptyBorder(10,10,10,10));
        photoBtns.setBackground(NAVY);

        JButton btnAdd = goldBtn("新增");
        JButton btnEdit = goldBtn("修改");
        JButton btnDel = goldBtn("刪除");

        photoBtns.add(btnAdd);
        photoBtns.add(btnEdit);
        photoBtns.add(btnDel);

        right.add(photoBtns,BorderLayout.SOUTH);

        // ===== 底部按鈕 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,15,15));
        bottom.setBackground(NAVY);

        JButton btnEditInfo = goldBtn("修改資料");
        JButton btnPerf = goldBtn("歷史業績");
        JButton btnProduct = goldBtn("管理商品");
        JButton btnOrder = goldBtn("管理訂單");
        JButton btnEmployeeMgr = goldBtn("管理員工(GM)");
        JButton btnReport = goldBtn("管理營業額(GM)");
        JButton btnLogout = goldBtn("登出");

        bottom.add(btnEditInfo);
        bottom.add(btnPerf);
        bottom.add(btnProduct);
        bottom.add(btnOrder);
        bottom.add(btnEmployeeMgr);
        bottom.add(btnReport);
        bottom.add(btnLogout);

        add(bottom, BorderLayout.SOUTH);

        // ===== 事件 =====
        btnEditInfo.addActionListener(e -> go(new EditEmployeeUI()));
        btnPerf.addActionListener(e -> go(new HistoryPerformanceUI()));
        btnProduct.addActionListener(e -> go(new controller.product.ProductManagerUI()));
        btnOrder.addActionListener(e -> go(new controller.order.OrderManagerUI()));

        btnEmployeeMgr.addActionListener(e -> {
            if (AppSession.getEmployee().roleEnum() != Role.GM) {
                Message.error(this, "權限不足：僅總經理可使用");
                return;
            }
            go(new EmployeeManagerUI());
        });

        btnReport.addActionListener(e -> {
            if (AppSession.getEmployee().roleEnum() != Role.GM) {
                Message.error(this, "權限不足：僅總經理可使用");
                return;
            }
            go(new ReportManagerUI());
        });

        btnLogout.addActionListener(e -> {
            AppSession.setEmployee(null);
            go(new controller.common.LoginUI());
        });

        setLocationRelativeTo(null);
        refresh();
    }

    // ===== BannerPanel =====
    static class BannerPanel extends JPanel {

        private final Image bg;
        private final Image logo;

        BannerPanel(String bgPath, String logoPath) {
            Image _bg = null;
            Image _logo = null;
            try {
                _bg = new ImageIcon(Objects.requireNonNull(getClass().getResource(bgPath))).getImage();
                _logo = new ImageIcon(Objects.requireNonNull(getClass().getResource(logoPath))).getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bg = _bg;
            logo = _logo;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }

            if (logo != null) {
                int targetH = (int)(getHeight() * 0.55);
                int ow = logo.getWidth(this);
                int oh = logo.getHeight(this);

                if (ow > 0 && oh > 0) {
                    int targetW = (int)((double)targetH * ow / oh);
                    int x = (getWidth() - targetW) / 2;
                    int y = (getHeight() - targetH) / 2;
                    g.drawImage(logo, x, y, targetW, targetH, this);
                }
            }
        }
    }

    private JButton goldBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        return b;
    }

    private void refresh() {

        Employee e = service.refresh(AppSession.getEmployee().getId());
        AppSession.setEmployee(e);

        String roleName = (e.roleEnum() == Role.GM) ? "總經理" : "一般員工";

        String html =
                "<html><h2 style='color:#0F2346;'>員工中心</h2>"
                        + "<hr style='color:#C9A227;'>"
                        + "<b>員工編號:</b> " + e.getEmployeeNo() + "<br/><br/>"
                        + "<b>姓名:</b> " + e.getEmployeeName() + "<br/><br/>"
                        + "<b>電話:</b> " + e.getPhone() + "<br/><br/>"
                        + "<b>Email:</b> " + e.getEmail() + "<br/><br/>"
                        + "<b>生日:</b> " + e.getBirthday() + "<br/><br/>"
                        + "<b>地址:</b> " + e.getAddressCity() + " " + e.getAddressDetail() + "<br/><br/>"
                        + "<b>帳號:</b> " + e.getUsername() + "<br/><br/>"
                        + "<b>角色:</b> " + roleName + "<br/>"
                        + "</html>";

        lblInfo.setText(html);

        ImageIcon icon = ImageUtil.scaledIcon(e.getPhotoPath(), 280, 280);
        if (icon == null) {
            lblPhoto.setText("預設人像");
        } else {
            lblPhoto.setText("");
            lblPhoto.setIcon(icon);
        }
    }
}