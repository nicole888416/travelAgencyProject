package controller.order;

import controller.AppSession;
import controller.common.BaseFrame;
import controller.common.Message;
import model.Coupon;
import model.Customer;
import model.OrderItem;
import model.Product;
import model.ProductSchedule;
import service.*;
import service.impl.*;
import util.DateUtil;
import util.ImageUtil;
import util.Theme;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class OrderUI extends BaseFrame {

    private final ProductService productService = new ProductServiceImpl();
    private final ProductScheduleService scheduleService = new ProductScheduleServiceImpl();
    private final ProductImageService imageService = new ProductImageServiceImpl();
    private final CouponService couponService = new CouponServiceImpl();
    private final EmployeeService employeeService = new EmployeeServiceImpl();
    private final OrdersService ordersService = new OrdersServiceImpl();

    private JPanel productsPanel;
    private DefaultListModel<String> cartModel = new DefaultListModel<>();
    private JList<String> cartList;
    private JLabel lblTotal;
    private JLabel heroTitle;

    private JComboBox<String> cmbCoupon;
    private JComboBox<EmployeeItem> cmbEmployee;

    private final NumberFormat nf = NumberFormat.getNumberInstance(Locale.TAIWAN);

    public OrderUI() {
        setTitle("VIP 旅遊下單中心");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1380, 820);
        setLayout(new BorderLayout());
        Theme.styleFrame(this);

        Customer c = AppSession.getCustomer();
        if (c == null) { Message.error(this, "請先登入會員"); go(new controller.common.LoginUI()); return; }

        // ===== Banner：背景圖 + Logo 疊加置中 =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.NAVY);

        BannerPanel banner = new BannerPanel("/images/hero_order.png", "/images/logo.png");
        banner.setPreferredSize(new Dimension(1380, 190));
        banner.setBorder(BorderFactory.createLineBorder(Theme.GOLD));
        top.add(banner, BorderLayout.CENTER);

        heroTitle = new JLabel("貴賓專屬下單", SwingConstants.LEFT);
        heroTitle.setFont(Theme.H1);
        heroTitle.setForeground(Theme.GOLD_SOFT);
        heroTitle.setBorder(new EmptyBorder(10, 18, 10, 18));
        top.add(heroTitle, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        // ===== 商品卡片三個一排（固定卡片寬度 + 自動換行）=====
        productsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        productsPanel.setBorder(new EmptyBorder(14,14,14,14));
        productsPanel.setBackground(Theme.BG);

        JScrollPane sp = new JScrollPane(productsPanel);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBorder(BorderFactory.createEmptyBorder());
        add(sp, BorderLayout.CENTER);

        // ===== 右側：購物車 + 結帳（CIS：深藍+金）=====
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(420, 0));
        right.setBackground(Theme.NAVY);
        right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel cartTitle = new JLabel("購物車", SwingConstants.CENTER);
        cartTitle.setFont(Theme.H2);
        cartTitle.setForeground(Theme.GOLD_SOFT);
        cartTitle.setBorder(new EmptyBorder(10,10,10,10));
        right.add(cartTitle, BorderLayout.NORTH);

        cartList = new JList<>(cartModel);
        cartList.setFont(Theme.BODY);
        cartList.setBackground(new Color(240,245,250));
        cartList.setSelectionBackground(Theme.GOLD);
        cartList.setSelectionForeground(Color.BLACK);
        right.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel pay = new JPanel();
        pay.setOpaque(false);
        pay.setLayout(new BoxLayout(pay, BoxLayout.Y_AXIS));
        pay.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblTotal = new JLabel("總金額: NT$ 0 元");
        lblTotal.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        lblTotal.setForeground(Theme.GOLD_SOFT);

        pay.add(lblTotal);
        pay.add(Box.createVerticalStrut(10));

        // Coupon
        JPanel pCoupon = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pCoupon.setOpaque(false);
        pCoupon.add(goldLabel("Coupon:"));
        cmbCoupon = new JComboBox<>();
        cmbCoupon.addItem("請選擇");
        cmbCoupon.addItem("(不使用)");
        for (Coupon cp : couponService.listActive()) {
            cmbCoupon.addItem(cp.getCouponCode());
        }
        pCoupon.add(cmbCoupon);
        pay.add(pCoupon);

        // Employee (必選)
        JPanel pEmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pEmp.setOpaque(false);
        pEmp.add(goldLabel("營業員:"));
        cmbEmployee = new JComboBox<>();
        cmbEmployee.addItem(new EmployeeItem(-1, "請選擇"));
        for (var e : employeeService.listAll()) {
            cmbEmployee.addItem(new EmployeeItem(e.getId(), e.getEmployeeName()));
        }
        pEmp.add(cmbEmployee);
        pay.add(pEmp);

        JButton btnClear = Theme.ghostButton("清空購物車");
        JButton btnConfirm = Theme.primaryButton("confirm(進入確認頁)");
        JButton btnBack = Theme.goldButton("回會員中心");
        JButton btnHistory = Theme.ghostButton("查詢歷史訂單");

        styleCisButton(btnClear, false);
        styleCisButton(btnHistory, false);
        styleCisButton(btnConfirm, true);
        styleCisButton(btnBack, true);

        pay.add(Box.createVerticalStrut(8));
        pay.add(btnClear);
        pay.add(Box.createVerticalStrut(8));
        pay.add(btnConfirm);
        pay.add(Box.createVerticalStrut(8));
        pay.add(btnBack);
        pay.add(Box.createVerticalStrut(8));
        pay.add(btnHistory);

        right.add(pay, BorderLayout.SOUTH);
        add(right, BorderLayout.EAST);

        // events
        btnClear.addActionListener(e -> clearCart());
        btnConfirm.addActionListener(e -> goConfirm());
        btnBack.addActionListener(e -> go(new controller.customer.CustomerUI()));
        btnHistory.addActionListener(e -> go(new controller.customer.HistoryOrderUI()));

        cmbCoupon.addActionListener(e -> {
            String code = String.valueOf(cmbCoupon.getSelectedItem());
            if ("請選擇".equals(code) || "(不使用)".equals(code)) code = null;
            AppSession.setSelectedCouponCode(code);
            refreshTotal();
        });

        cmbEmployee.addActionListener(e -> {
            EmployeeItem it = (EmployeeItem) cmbEmployee.getSelectedItem();
            if (it == null || it.id <= 0) AppSession.setSelectedEmployeeId(null);
            else AppSession.setSelectedEmployeeId(it.id);
        });

        setLocationRelativeTo(null);
        loadProducts();
        refreshCartView();
    }

    private JLabel goldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.GOLD_SOFT);
        l.setFont(Theme.BODY);
        return l;
    }

    private void styleCisButton(JButton btn, boolean gold) {
        btn.setFocusPainted(false);
        btn.setFont(Theme.BODY);
        if (gold) {
            btn.setBackground(Theme.GOLD);
            btn.setForeground(Color.BLACK);
        } else {
            btn.setBackground(new Color(20,40,90));
            btn.setForeground(Color.WHITE);
        }
    }

    // ===== BannerPanel：背景圖滿版 + Logo 置中疊加 =====
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
            setOpaque(true);
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

    private void loadProducts() {
        productsPanel.removeAll();
        List<Product> products = productService.listAll();
        for (Product p : products) {
            productsPanel.add(new ProductCard(p));
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void refreshCartView() {
        cartModel.clear();
        for (OrderItem it : AppSession.getCart()) {
            cartModel.addElement(it.getProductName() + " x " + it.getQty() + "位 = NT$ " + nf.format(it.getSubtotal()) + " 元");
        }
        refreshTotal();
    }

    private void refreshTotal() {
        int sum = 0;
        for (OrderItem it : AppSession.getCart()) sum += it.getSubtotal();

        String code = AppSession.getSelectedCouponCode();
        OrdersService.PricingResult pr = ordersService.calculate(AppSession.getCustomer(), sum, code);

        lblTotal.setText("總金額: NT$ " + nf.format(pr.finalAmount) + " 元");
    }

    private void clearCart() {
        AppSession.clearCart();
        refreshCartView();
    }

    private void goConfirm() {
        if (AppSession.getCart().isEmpty()) { Message.error(this, "購物車不可為空"); return; }
        Integer empId = AppSession.getSelectedEmployeeId();
        if (empId == null) {
            Message.error(this, "請選擇為您服務的營業員再下單，謝謝您");
            return;
        }
        go(new ConfirmUI());
    }

    class ProductCard extends JPanel {
        private final Product product;

        private JDateChooser dcDepart;
        private JLabel lblReturn;
        private JLabel lblHintDates;

        private JComboBox<Integer> cmbQty;

        private Map<String, ProductSchedule> scheduleByDepart = new LinkedHashMap<>();
        private String firstImg;

        ProductCard(Product p) {
            this.product = p;
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(320, 500));
            setBackground(Color.WHITE);

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.GOLD, 2),
                    new EmptyBorder(10,10,10,10)
            ));

            firstImg = imageService.firstPath(p.getId());

            JLabel lblImg = new JLabel("", SwingConstants.CENTER);
            lblImg.setPreferredSize(new Dimension(0, 150));
            ImageIcon icon = (firstImg == null) ? null : ImageUtil.scaledIcon(firstImg, 360, 170);
            if (icon != null) lblImg.setIcon(icon);
            else lblImg.setIcon(ImageUtil.scaledResource("/images/hero_product_card.png", 360, 170));
            add(lblImg, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

            JLabel lblName = new JLabel(p.getProductName(), SwingConstants.CENTER);
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblName.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
            lblName.setForeground(Theme.NAVY);
            center.add(lblName);

            center.add(Box.createVerticalStrut(6));

            // ===== 出發日期列（靠左）=====
            JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
            row1.setOpaque(false);

            JLabel lblDepart = new JLabel("出發日期:");
            lblDepart.setHorizontalAlignment(SwingConstants.LEFT);
            row1.add(lblDepart);

            dcDepart = new JDateChooser();
            dcDepart.setDateFormatString("yyyy-MM-dd");
            row1.add(dcDepart);

            lblReturn = Theme.badge("回程日期: -");
            row1.add(lblReturn);
            center.add(row1);

            // ===== 可出發日期提示（超過兩個自動換行）=====
            lblHintDates = new JLabel();
            lblHintDates.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
            lblHintDates.setForeground(new Color(80,80,80));
            lblHintDates.setBorder(new EmptyBorder(0, 6, 0, 6));
            center.add(lblHintDates);

            // 多給可出發日期一點空間
            center.add(Box.createVerticalStrut(8));

            // ===== Qty 往下移一點 =====
            JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
            row2.setOpaque(false);
            row2.setBorder(new EmptyBorder(6, 0, 0, 0));

            row2.add(new JLabel("數量:"));
            cmbQty = new JComboBox<>();
            cmbQty.setPreferredSize(new Dimension(90, 28));
            row2.add(cmbQty);
            center.add(row2);

            // ===== Price 往左移 & 置中 =====
            String priceText = nf.format(p.getProductPrice());
            JLabel lblPrice = new JLabel(
                    "<html><span style='font-size:16px;color:#0A1937;'>TWD </span>" +
                            "<span style='font-size:18px;color:#C9A227;'><b>" + priceText + "</b></span>" +
                            "<span style='font-size:14px;color:#0A1937;'> 起</span></html>",
                    SwingConstants.CENTER
            );
            lblPrice.setBorder(new EmptyBorder(8, 0, 0, 0));
            lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(lblPrice);

            add(center, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
            bottom.setOpaque(false);

            JButton btnAdd = Theme.primaryButton("加入購物車");
            JButton btnDetail = Theme.goldButton("商品詳情");

            styleCisButton(btnAdd, true);
            styleCisButton(btnDetail, true);

            bottom.add(btnAdd);
            bottom.add(btnDetail);
            add(bottom, BorderLayout.SOUTH);

            loadSchedulesAndInit();

            dcDepart.addPropertyChangeListener("date", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    onDepartChanged();
                }
            });

            btnAdd.addActionListener(e -> addToCart());
            btnDetail.addActionListener(e -> go(new ProductDetailUI(product.getId())));
        }

        private void loadSchedulesAndInit() {
            scheduleByDepart.clear();
            List<ProductSchedule> sch = scheduleService.listByProduct(product.getId());

            List<String> dates = new ArrayList<>();
            for (ProductSchedule s : sch) {
                String depart = DateUtil.fmt(s.getDepartDate());
                scheduleByDepart.put(depart, s);
                if (s.getSeatStock() > 0) dates.add(depart);
                else dates.add(depart + "(售完)");
            }

            if (dates.isEmpty()) {
                lblHintDates.setText("可出發日期：尚未設定(請到後台商品管理新增行程)");
            } else {
                lblHintDates.setText(buildDatesHtml(dates, 2));
            }

            for (ProductSchedule s : sch) {
                if (s.getSeatStock() > 0) {
                    dcDepart.setDate(s.getDepartDate());
                    break;
                }
            }
            onDepartChanged();
        }

        // 每 rowCount 個日期換行（你要 >2 就換行，這裡 rowCount=2）
        private String buildDatesHtml(List<String> dates, int rowCount) {
            StringBuilder sb = new StringBuilder("<html><b>可出發日期：</b>");
            for (int i=0;i<dates.size();i++) {
                if (i > 0) sb.append("、");
                sb.append(dates.get(i));
                if ((i+1) % rowCount == 0 && (i+1) < dates.size()) sb.append("<br/>");
            }
            sb.append("</html>");
            return sb.toString();
        }

        private void onDepartChanged() {
            Date d = dcDepart.getDate();
            cmbQty.removeAllItems();

            if (d == null) {
                lblReturn.setText("回程日期: -");
                cmbQty.setEnabled(false);
                return;
            }

            String key = DateUtil.fmt(d);
            ProductSchedule s = scheduleByDepart.get(key);
            if (s == null) {
                lblReturn.setText("回程日期: -");
                cmbQty.setEnabled(false);
                Message.info(OrderUI.this, "你選的日期不在可出發日期內，請依下方清單選擇");
                return;
            }

            lblReturn.setText("回程日期: " + DateUtil.fmt(s.getReturnDate()));

            int stock = s.getSeatStock();
            if (stock <= 0) {
                cmbQty.addItem(0);
                cmbQty.setEnabled(false);
            } else {
                cmbQty.setEnabled(true);
                for (int i=1;i<=stock;i++) cmbQty.addItem(i);
            }
        }

        private void addToCart() {
            Date d = dcDepart.getDate();
            if (d == null) { Message.error(OrderUI.this, "請選擇出發日期"); return; }
            ProductSchedule s = scheduleByDepart.get(DateUtil.fmt(d));
            if (s == null) { Message.error(OrderUI.this, "請依可出發日期清單選擇"); return; }
            if (s.getSeatStock() <= 0) { Message.error(OrderUI.this, "該出發日期已售完"); return; }

            Integer qty = (Integer) cmbQty.getSelectedItem();
            if (qty == null || qty <= 0) { Message.error(OrderUI.this, "請選擇數量"); return; }

            OrderItem it = new OrderItem();
            it.setProductId(product.getId());
            it.setScheduleId(s.getId());
            it.setQty(qty);
            it.setUnitPrice(product.getProductPrice());
            it.setSubtotal(product.getProductPrice() * qty);

            it.setProductName(product.getProductName());
            it.setProductImgPath(firstImg);
            it.setDepartDate(DateUtil.fmt(s.getDepartDate()));
            it.setReturnDate(DateUtil.fmt(s.getReturnDate()));

            AppSession.getCart().add(it);
            refreshCartView();
            Message.info(OrderUI.this, "已加入購物車");
        }
    }

    static class EmployeeItem {
        int id; String name;
        EmployeeItem(int id, String name){ this.id=id; this.name=name; }
        public String toString(){ return name; }
    }
}