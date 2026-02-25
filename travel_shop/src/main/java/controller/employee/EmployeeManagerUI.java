package controller.employee;

import controller.common.BaseFrame;
import controller.common.Message;
import model.Employee;
import model.Orders;
import service.EmployeeService;
import service.OrdersService;
import service.impl.EmployeeServiceImpl;
import service.impl.OrdersServiceImpl;
import util.CsvUtil;

import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;

public class EmployeeManagerUI extends BaseFrame {

    private final EmployeeService employeeService = new EmployeeServiceImpl();
    private final OrdersService ordersService = new OrdersServiceImpl();

    private JComboBox<EmployeeItem> cmbEmployees;
    private JDateChooser dcFrom, dcTo;

    private JTable table;
    private DefaultTableModel model;
    private JPanel chartWrap;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public EmployeeManagerUI() {

        setTitle("管理員工(GM)");
        setSize(1200, 720);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== 上方查詢列 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,15,15));
        top.setBackground(NAVY);

        top.add(whiteLabel("員工:"));
        cmbEmployees = new JComboBox<>();
        cmbEmployees.setPreferredSize(new Dimension(180,28));
        top.add(cmbEmployees);

        top.add(whiteLabel("日期區間:"));
        dcFrom = new JDateChooser();
        dcFrom.setDateFormatString("yyyy-MM-dd");
        dcTo = new JDateChooser();
        dcTo.setDateFormatString("yyyy-MM-dd");
        top.add(dcFrom);
        top.add(new JLabel("~"));
        top.add(dcTo);

        JButton btnQuery = goldBtn("查詢");
        top.add(btnQuery);

        add(top, BorderLayout.NORTH);

        // ===== 表格 =====
        model = new DefaultTableModel(
                new Object[]{"order_no","final_amount","order_date"}, 0
        ){
            public boolean isCellEditable(int r, int c){ return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Microsoft JhengHei",Font.PLAIN,14));

        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Microsoft JhengHei",Font.BOLD,14));

        JScrollPane spTable = new JScrollPane(table);
        spTable.setBorder(new LineBorder(GOLD,2));

        // ===== 圖表區 =====
        chartWrap = new JPanel(new BorderLayout());
        chartWrap.setBackground(Color.WHITE);
        chartWrap.setBorder(new LineBorder(GOLD,2));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.55);
        split.setTopComponent(spTable);
        split.setBottomComponent(chartWrap);

        add(split, BorderLayout.CENTER);

        // ===== 底部 =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,15));
        bottom.setBackground(NAVY);

        JButton btnPrint = goldBtn("列印 CSV");
        JButton btnBack = goldBtn("返回員工中心");

        bottom.add(btnPrint);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        // ===== 事件 =====
        btnQuery.addActionListener(e -> query());
        btnPrint.addActionListener(e -> printCsv());
        btnBack.addActionListener(e -> go(new EmployeeUI()));

        setLocationRelativeTo(null);
        loadEmployees();
    }

    private JLabel whiteLabel(String text){
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Microsoft JhengHei",Font.BOLD,14));
        return l;
    }

    private JButton goldBtn(String text){
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Microsoft JhengHei",Font.BOLD,14));
        return b;
    }

    private void loadEmployees() {
        cmbEmployees.removeAllItems();
        List<Employee> list = employeeService.listAll();
        for (Employee e : list) {
            cmbEmployees.addItem(new EmployeeItem(e.getId(), e.getEmployeeName()));
        }
    }

    private void query() {
        EmployeeItem it = (EmployeeItem) cmbEmployees.getSelectedItem();
        if (it == null) { Message.error(this, "請選擇員工"); return; }

        Date from = dcFrom.getDate();
        Date to = dcTo.getDate();
        if (from == null || to == null) { Message.error(this, "請選擇日期區間"); return; }

        List<Orders> list = ordersService.listByDateRange(from, to);
        list.removeIf(o -> o.getEmployeeId() != it.id);

        model.setRowCount(0);
        for (Orders o : list)
            model.addRow(new Object[]{o.getOrderNo(), o.getFinalAmount(), o.getOrderDate()});

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (Orders o : list) {
            String day = String.valueOf(o.getOrderDate()).substring(0,10);
            ds.addValue(o.getFinalAmount(), "業績", day);
        }

        var chart = ChartFactory.createLineChart(
                "員工業績(依日期)",
                "日期",
                "金額",
                ds
        );

        chartWrap.removeAll();
        chartWrap.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartWrap.revalidate();
    }

    private void printCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("employee_performance.csv"));
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

    static class EmployeeItem {
        int id;
        String name;

        EmployeeItem(int id, String name){
            this.id = id;
            this.name = name;
        }

        public String toString(){
            return name;
        }
    }
}