package controller.order;

import controller.AppSession;
import controller.common.BaseFrame;
import controller.common.Message;
import model.OrderItem;
import model.Orders;
import service.OrdersService;
import service.impl.OrdersServiceImpl;
import util.IdUtil;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ConfirmUI extends BaseFrame {

    private final OrdersService ordersService = new OrdersServiceImpl();

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public ConfirmUI() {

        setTitle("確認訂單");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== Banner =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(NAVY);

        BannerPanel banner = new BannerPanel("/images/hero_order.png", "/images/logo.png");
        banner.setPreferredSize(new Dimension(1200, 180));
        banner.setBorder(BorderFactory.createLineBorder(GOLD,2));

        top.add(banner, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== Table =====
        model = new DefaultTableModel(
                new Object[]{"product_name","unit_price","qty","subtotal","depart_date","return_date","product_img"}, 0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(model);
        table.setRowHeight(85);
        table.setSelectionBackground(GOLD);
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(NAVY);
        header.setForeground(GOLD);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        table.getColumnModel().getColumn(6).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JLabel lbl = new JLabel("", SwingConstants.CENTER);
            lbl.setOpaque(true);
            if (isSelected) lbl.setBackground(tbl.getSelectionBackground());
            ImageIcon icon = ImageUtil.scaledIcon(value == null? null : String.valueOf(value), 120, 70);
            if (icon != null) lbl.setIcon(icon);
            return lbl;
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(GOLD,2));

        add(sp, BorderLayout.CENTER);

        // ===== Bottom =====
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(NAVY);
        bottom.setBorder(new EmptyBorder(10,15,10,15));

        lblTotal = new JLabel("總金額: NT$ 0 元");
        lblTotal.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
        lblTotal.setForeground(GOLD);
        bottom.add(lblTotal, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btns.setOpaque(false);

        JButton btnBack = cisBlueBtn("返回重新下單");
        JButton btnConfirm = cisGoldBtn("確認(資料進資料庫)");

        btns.add(btnBack);
        btns.add(btnConfirm);

        bottom.add(btns, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        // ===== Events =====
        btnBack.addActionListener(e -> go(new OrderUI()));
        btnConfirm.addActionListener(e -> doConfirm());

        setLocationRelativeTo(null);
        loadCart();
    }

    // ===== CIS 按鈕 =====
    private JButton cisGoldBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei", Font.BOLD, 15));
        return b;
    }

    private JButton cisBlueBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(new Color(20,40,90));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei", Font.BOLD, 15));
        return b;
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

    // ===== 載入購物車 =====
    private void loadCart() {
        model.setRowCount(0);
        int sum = 0;

        for (OrderItem it : AppSession.getCart()) {
            sum += it.getSubtotal();
            model.addRow(new Object[]{
                    it.getProductName(),
                    it.getUnitPrice(),
                    it.getQty(),
                    it.getSubtotal(),
                    it.getDepartDate(),
                    it.getReturnDate(),
                    it.getProductImgPath()
            });
        }

        OrdersService.PricingResult pr =
                ordersService.calculate(AppSession.getCustomer(), sum, AppSession.getSelectedCouponCode());

        lblTotal.setText("總金額: NT$ " + pr.finalAmount + " 元");
    }

    private void doConfirm() {
        try {
            String orderNo = IdUtil.orderNo();
            Integer empId = AppSession.getSelectedEmployeeId();

            Orders o = ordersService.placeOrder(
                    orderNo,
                    AppSession.getCustomer().getId(),
                    empId,
                    AppSession.getSelectedCouponCode(),
                    List.copyOf(AppSession.getCart())
            );

            AppSession.clearCart();
            AppSession.setSelectedCouponCode(null);

            go(new FinishedUI(o.getOrderNo()));

        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }
}