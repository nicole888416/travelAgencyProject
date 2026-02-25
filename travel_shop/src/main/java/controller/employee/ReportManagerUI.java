package controller.employee;

import controller.common.BaseFrame;
import controller.common.Message;
import model.Orders;
import service.OrdersService;
import service.impl.OrdersServiceImpl;
import util.CsvUtil;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.*;

public class ReportManagerUI extends BaseFrame {

    private final OrdersService ordersService = new OrdersServiceImpl();

    private JTable table;
    private DefaultTableModel model;
    private JDateChooser dcFrom, dcTo;
    private JPanel chartWrap;

    private final Color NAVY = new Color(15,35,70);
    private final Color GOLD = new Color(201,162,39);
    private final Color LIGHT_BG = new Color(230,235,245);

    public ReportManagerUI() {

        setTitle("管理營業額(GM)");
        setSize(1200, 720);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // ===== 上方查詢列 =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,15,15));
        top.setBackground(NAVY);

        top.add(whiteLabel("日期區間:"));

        dcFrom = new JDateChooser();
        dcFrom.setDateFormatString("yyyy-MM-dd");
        dcTo = new JDateChooser();
        dcTo.setDateFormatString("yyyy-MM-dd");

        top.add(dcFrom);
        top.add(new JLabel("~"));
        top.add(dcTo);

        JButton btnRange = goldBtn("查詢");
        JButton btnDay = goldBtn("當日");
        JButton btnWeek = goldBtn("當週");
        JButton btnMonth = goldBtn("當月");
        JButton btnQuarter = goldBtn("當季");
        JButton btnYear = goldBtn("當年");

        top.add(btnRange);
        top.add(btnDay);
        top.add(btnWeek);
        top.add(btnMonth);
        top.add(btnQuarter);
        top.add(btnYear);

        add(top, BorderLayout.NORTH);

        // ===== 表格 =====
        model = new DefaultTableModel(
                new Object[]{"日期","總營業額"}, 0
        ){
            public boolean isCellEditable(int r,int c){ return false; }
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
        btnRange.addActionListener(e -> queryByRange(dcFrom.getDate(), dcTo.getDate()));
        btnDay.addActionListener(e -> preset(Preset.DAY));
        btnWeek.addActionListener(e -> preset(Preset.WEEK));
        btnMonth.addActionListener(e -> preset(Preset.MONTH));
        btnQuarter.addActionListener(e -> preset(Preset.QUARTER));
        btnYear.addActionListener(e -> preset(Preset.YEAR));
        btnPrint.addActionListener(e -> printCsv());
        btnBack.addActionListener(e -> go(new EmployeeUI()));

        setLocationRelativeTo(null);
        preset(Preset.MONTH);
    }

    enum Preset { DAY, WEEK, MONTH, QUARTER, YEAR }

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

    private void preset(Preset p) {

        Calendar cal = Calendar.getInstance();
        Date end = cal.getTime();

        switch (p) {
            case DAY:
                cal.set(Calendar.HOUR_OF_DAY,0);
                break;
            case WEEK:
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                break;
            case MONTH:
                cal.set(Calendar.DAY_OF_MONTH,1);
                break;
            case QUARTER:
                int m = cal.get(Calendar.MONTH);
                cal.set(Calendar.MONTH, (m/3)*3);
                cal.set(Calendar.DAY_OF_MONTH,1);
                break;
            case YEAR:
                cal.set(Calendar.MONTH,0);
                cal.set(Calendar.DAY_OF_MONTH,1);
                break;
        }

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Date start = cal.getTime();
        dcFrom.setDate(start);
        dcTo.setDate(end);
        queryByRange(start, end);
    }

    private void queryByRange(Date from, Date to) {

        if (from == null || to == null) {
            Message.error(this, "請選擇日期區間");
            return;
        }

        List<Orders> list = ordersService.listByDateRange(from, to);

        Map<String,Integer> sumByDay = new LinkedHashMap<>();

        for (Orders o : list) {
            String day = String.valueOf(o.getOrderDate()).substring(0,10);
            sumByDay.put(day,
                    sumByDay.getOrDefault(day,0) + o.getFinalAmount());
        }

        model.setRowCount(0);
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        for (String day : sumByDay.keySet()) {
            int sum = sumByDay.get(day);
            model.addRow(new Object[]{day, sum});
            ds.addValue(sum, "營業額", day);
        }

        var chart = ChartFactory.createLineChart(
                "旅行社營業額(依日期)",
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
        fc.setSelectedFile(new File("revenue_report.csv"));

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