package controller.customer;

import controller.AppSession;
import controller.BaseFrame;
import controller.common.Message;
import controller.customer.CustomerUI;
import controller.order.OrderDetailUI;
import model.Orders;
import service.OrderService;
import service.impl.OrderServiceImpl;
import util.CsvUtil;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;

public class HistoryOrderUI extends BaseFrame {

    private final OrderService ordersService = new OrderServiceImpl();

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtOrderNo;
    private JDateChooser dcFrom, dcTo;

    private final Color DARK_BLUE = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public HistoryOrderUI() {

        setTitle("歷史訂單");
        setSize(1100, 620);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== 上方搜尋區 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,15,12));
        top.setBackground(DARK_BLUE);
        top.setBorder(new EmptyBorder(10,20,10,20));

        JLabel lblNo = whiteLabel("order_no:");
        txtOrderNo = new JTextField(16);

        JButton btnFindNo = goldBtn("用 order_no 查詢");

        JLabel lblDate = whiteLabel("日期區間:");
        dcFrom = new JDateChooser(); dcFrom.setDateFormatString("yyyy-MM-dd");
        dcTo = new JDateChooser(); dcTo.setDateFormatString("yyyy-MM-dd");

        JButton btnFindDate = goldBtn("用日期區間查詢");
        JButton btnRefresh = goldBtn("顯示全部");

        top.add(lblNo);
        top.add(txtOrderNo);
        top.add(btnFindNo);
        top.add(lblDate);
        top.add(dcFrom);
        top.add(new JLabel("~"));
        top.add(dcTo);
        top.add(btnFindDate);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // ===== 表格 =====
        model = new DefaultTableModel(
                new Object[]{"order_no","status","amount","discount","final_amount","order_date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionBackground(GOLD);
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(DARK_BLUE);
        header.setForeground(GOLD);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(GOLD,2));
        add(sp, BorderLayout.CENTER);

        // ===== 下方按鈕區 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT,20,12));
        bottom.setBackground(DARK_BLUE);

        JButton btnOpen = goldBtn("查看明細");
        JButton btnPrint = goldBtn("列印 CSV");
        JButton btnBack = goldBtn("返回會員中心");

        bottom.add(btnOpen);
        bottom.add(btnPrint);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        // ===== 事件 =====
        btnFindNo.addActionListener(e -> searchByNo());
        btnFindDate.addActionListener(e -> searchByDate());
        btnRefresh.addActionListener(e -> loadAll());
        btnOpen.addActionListener(e -> openDetail());
        btnPrint.addActionListener(e -> printCsv());
        btnBack.addActionListener(e -> go(new CustomerUI()));

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
        return b;
    }

    private void loadAll() {
        int cid = AppSession.getCustomer().getId();
        List<Orders> list = ordersService.listByCustomer(cid);
        fill(list);
    }

    private void searchByNo() {
        String no = txtOrderNo.getText();
        if (no == null || no.trim().isEmpty()) { Message.error(this, "請輸入 order_no"); return; }
        Orders o = ordersService.findByOrderNo(no.trim());
        model.setRowCount(0);
        if (o != null && o.getCustomerId() == AppSession.getCustomer().getId()) {
            model.addRow(new Object[]{o.getOrderNo(), o.getStatus(), o.getAmount(), o.getDiscountAmount(), o.getFinalAmount(), o.getOrderDate()});
        } else {
            Message.info(this, "查無此訂單");
        }
    }

    private void searchByDate() {
        Date from = dcFrom.getDate();
        Date to = dcTo.getDate();
        if (from == null || to == null) { Message.error(this, "請選擇日期區間"); return; }
        List<Orders> list = ordersService.listByDateRange(from, to);
        list.removeIf(o -> o.getCustomerId() != AppSession.getCustomer().getId());
        fill(list);
    }

    private void fill(List<Orders> list) {
        model.setRowCount(0);
        for (Orders o : list) {
            model.addRow(new Object[]{
                    o.getOrderNo(),
                    o.getStatus(),
                    o.getAmount(),
                    o.getDiscountAmount(),
                    o.getFinalAmount(),
                    o.getOrderDate()
            });
        }
    }

    private void openDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { Message.error(this, "請選擇一筆訂單"); return; }
        String orderNo = String.valueOf(model.getValueAt(row, 0));
        go(new OrderDetailUI(orderNo, true));
    }

    private void printCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("history_orders.csv"));
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