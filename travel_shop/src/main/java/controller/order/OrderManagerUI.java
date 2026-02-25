package controller.order;

import controller.common.BaseFrame;
import controller.common.Message;
import model.OrderStatus;
import model.Orders;
import service.OrdersService;
import service.impl.OrdersServiceImpl;
import util.CsvUtil;
import util.DateUtil;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrderManagerUI extends BaseFrame {

    private final OrdersService ordersService = new OrdersServiceImpl();

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtOrderNo;
    private JDateChooser dcFrom, dcTo;
    private JComboBox<String> cmbMonth;
    private JComboBox<String> cmbStatus;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public OrderManagerUI() {

        setTitle("訂單管理(後台)");
        setSize(1400, 800);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== Banner =====
        BannerPanel banner = new BannerPanel("/images/hero_admin.png", "/images/logo.png");
        banner.setPreferredSize(new Dimension(1400,180));
        banner.setBorder(BorderFactory.createLineBorder(GOLD,2));
        add(banner, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(LIGHT_BG);
        center.setBorder(new EmptyBorder(20,20,20,20));
        add(center, BorderLayout.CENTER);

        // ===== 查詢區 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        top.setBackground(NAVY);
        top.setBorder(new CompoundBorder(
                new LineBorder(GOLD,2),
                new EmptyBorder(10,15,10,15)
        ));

        top.add(whiteLabel("order_no:"));
        txtOrderNo = new JTextField(12);
        top.add(txtOrderNo);

        JButton btnFindNo = goldBtn("查詢訂單");

        top.add(whiteLabel("日期區間:"));
        dcFrom = new JDateChooser();
        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo = new JDateChooser();
        dcTo.setDateFormatString("yyyy-MM-dd");

        top.add(dcFrom);
        top.add(new JLabel("~") {{ setForeground(Color.WHITE); }});
        top.add(dcTo);

        JButton btnFindDate = goldBtn("日期查詢");

        top.add(whiteLabel("月訂單:"));
        cmbMonth = new JComboBox<>();
        fillMonthOptions();
        top.add(cmbMonth);

        JButton btnMonth = goldBtn("查詢月訂單");

        top.add(whiteLabel("status:"));
        cmbStatus = new JComboBox<>(new String[]{
                OrderStatus.PENDING.name(),
                OrderStatus.CONFIRMING.name(),
                OrderStatus.COMPLETED.name()
        });
        top.add(cmbStatus);

        JButton btnUpdateStatus = goldBtn("更新狀態");
        JButton btnRefresh = goldBtn("顯示全部");

        top.add(btnFindNo);
        top.add(btnFindDate);
        top.add(btnMonth);
        top.add(btnUpdateStatus);
        top.add(btnRefresh);

        center.add(top, BorderLayout.NORTH);

        // ===== 表格 =====
        model = new DefaultTableModel(
                new Object[]{"id","order_no","customer_id","employee_id","status","amount","final_amount","order_date"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(GOLD,2));

        center.add(sp, BorderLayout.CENTER);

        // ===== 底部按鈕 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,15));
        bottom.setBackground(NAVY);

        JButton btnOpen = goldBtn("查看明細");
        JButton btnPrint = goldBtn("列印 CSV");
        JButton btnDelete = goldBtn("刪除訂單");
        JButton btnBack = goldBtn("返回");

        bottom.add(btnOpen);
        bottom.add(btnPrint);
        bottom.add(btnDelete);
        bottom.add(btnBack);

        center.add(bottom, BorderLayout.SOUTH);

        // ===== 事件 =====
        btnFindNo.addActionListener(e -> searchByNo());
        btnFindDate.addActionListener(e -> searchByDate());
        btnMonth.addActionListener(e -> searchByMonth());
        btnUpdateStatus.addActionListener(e -> updateStatus());
        btnRefresh.addActionListener(e -> loadAll());

        btnOpen.addActionListener(e -> openDetail());
        btnPrint.addActionListener(e -> printCsv());
        btnDelete.addActionListener(e -> deleteOrder());
        btnBack.addActionListener(e -> go(new controller.employee.EmployeeUI()));

        setLocationRelativeTo(null);
        loadAll();
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

    static class BannerPanel extends JPanel {

        private final Image bg;
        private final Image logo;

        BannerPanel(String bgPath, String logoPath) {
            Image _bg = null;
            Image _logo = null;
            try {
                _bg = new ImageIcon(Objects.requireNonNull(getClass().getResource(bgPath))).getImage();
                _logo = new ImageIcon(Objects.requireNonNull(getClass().getResource(logoPath))).getImage();
            } catch (Exception e) { e.printStackTrace(); }
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

    private void fillMonthOptions() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cmbMonth.removeAllItems();
        for (int m=1;m<=12;m++){
            cmbMonth.addItem(year + "年" + m + "月訂單");
        }
    }

    private void loadAll() {
        fill(ordersService.listByDateRange(new Date(0), new Date()));
    }

    private void searchByNo() {
        String no = txtOrderNo.getText();
        if (no == null || no.trim().isEmpty()) { Message.error(this, "請輸入 order_no"); return; }
        Orders o = ordersService.findByOrderNo(no.trim());
        model.setRowCount(0);
        if (o != null) {
            model.addRow(new Object[]{o.getId(), o.getOrderNo(), o.getCustomerId(), o.getEmployeeId(),
                    o.getStatus(), o.getAmount(), o.getFinalAmount(), DateUtil.fmt(o.getOrderDate())});
        } else Message.info(this, "查無此訂單");
    }

    private void searchByDate() {
        Date from = dcFrom.getDate();
        Date to = dcTo.getDate();
        if (from == null || to == null) { Message.error(this, "請選擇日期區間"); return; }
        fill(ordersService.listByDateRange(from, to));
    }

    private void searchByMonth() {
        String s = String.valueOf(cmbMonth.getSelectedItem());
        if (s == null) return;
        int y = Integer.parseInt(s.substring(0,4));
        int m = Integer.parseInt(s.substring(s.indexOf('年')+1, s.indexOf('月')));
        fill(ordersService.listByMonth(y, m));
    }

    private void fill(List<Orders> list) {
        model.setRowCount(0);
        for (Orders o : list) {
            model.addRow(new Object[]{o.getId(), o.getOrderNo(), o.getCustomerId(), o.getEmployeeId(),
                    o.getStatus(), o.getAmount(), o.getFinalAmount(), DateUtil.fmt(o.getOrderDate())});
        }
    }

    private Integer selectedOrderId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
    }

    private void updateStatus() {
        Integer id = selectedOrderId();
        if (id == null) { Message.error(this, "請選擇一筆訂單"); return; }
        ordersService.updateStatus(id, String.valueOf(cmbStatus.getSelectedItem()));
        Message.info(this, "已更新狀態");
        loadAll();
    }

    private void deleteOrder() {
        Integer id = selectedOrderId();
        if (id == null) { Message.error(this, "請選擇一筆訂單"); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "確定刪除該訂單？(明細會一併刪除)", "確認",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        ordersService.delete(id);
        Message.info(this, "刪除成功");
        loadAll();
    }

    private void openDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { Message.error(this, "請選擇一筆訂單"); return; }
        String orderNo = String.valueOf(model.getValueAt(row, 1));
        go(new OrderDetailUI(orderNo, false));
    }

    private void printCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("orders.csv"));
        int r = fc.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                CsvUtil.exportJTableToCsv(table, fc.getSelectedFile());
                Message.info(this, "已輸出: " + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                Message.error(this, ex.getMessage());
            }
        }
    }
}