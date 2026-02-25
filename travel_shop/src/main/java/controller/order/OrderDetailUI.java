package controller.order;

import controller.common.BaseFrame;
import controller.common.Message;
import model.OrderItem;
import model.Orders;
import service.OrderItemService;
import service.OrdersService;
import service.impl.OrderItemServiceImpl;
import service.impl.OrdersServiceImpl;
import util.CsvUtil;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class OrderDetailUI extends BaseFrame {

    private final OrdersService ordersService = new OrdersServiceImpl();
    private final OrderItemService itemService = new OrderItemServiceImpl();

    private JTable table;
    private DefaultTableModel model;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public OrderDetailUI(String orderNo, boolean backToHistory) {

        setTitle("訂單明細");
        setSize(1200, 720);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== 表格模型 =====
        model = new DefaultTableModel(
                new Object[]{"product_name","unit_price","qty","subtotal","depart_date","return_date","product_img"}, 0
        ){
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(model);
        table.setRowHeight(90);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));

        // 表頭深藍白字
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));

        // 圖片 renderer（保持原本邏輯）
        table.getColumnModel().getColumn(6).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JLabel lbl = new JLabel("", SwingConstants.CENTER);
            lbl.setOpaque(true);
            if (isSelected) lbl.setBackground(tbl.getSelectionBackground());
            ImageIcon icon = ImageUtil.scaledIcon(value == null ? null : String.valueOf(value), 140, 80);
            if (icon != null) lbl.setIcon(icon);
            return lbl;
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new CompoundBorder(
                new LineBorder(GOLD,2),
                new EmptyBorder(10,10,10,10)
        ));

        add(sp, BorderLayout.CENTER);

        // ===== 底部區 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,15));
        bottom.setBackground(NAVY);

        JButton btnPrint = goldBtn("列印 CSV");
        JButton btnBack = goldBtn("返回");

        bottom.add(btnPrint);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        // ===== 事件 =====
        btnPrint.addActionListener(e -> printCsv());
        btnBack.addActionListener(e -> {
            if (backToHistory) 
                go(new controller.customer.HistoryOrderUI());
            else 
                go(new controller.employee.EmployeeUI());
        });

        setLocationRelativeTo(null);
        load(orderNo);
    }

    private JButton goldBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        return b;
    }

    private void load(String orderNo) {

        Orders o = ordersService.findByOrderNo(orderNo);
        if (o == null) {
            Message.error(this, "找不到訂單");
            return;
        }

        List<OrderItem> items = itemService.listByOrderId(o.getId());
        model.setRowCount(0);

        for (OrderItem it : items) {
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
    }

    private void printCsv() {

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("order_detail.csv"));

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