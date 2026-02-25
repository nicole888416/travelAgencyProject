package controller.product;

import controller.BaseFrame;
import controller.common.Message;
import controller.order.OrderUI;
import model.Product;
import model.ProductSchedule;
import service.ProductImageService;
import service.ProductScheduleService;
import service.ProductService;
import service.impl.ProductImageServiceImpl;
import service.impl.ProductScheduleServiceImpl;
import service.impl.ProductServiceImpl;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductDetailUI extends BaseFrame {

    private final ProductService productService = new ProductServiceImpl();
    private final ProductScheduleService scheduleService = new ProductScheduleServiceImpl();
    private final ProductImageService imageService = new ProductImageServiceImpl();

    public ProductDetailUI(int productId) {
        setTitle("商品詳情");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 680);
        setLayout(new BorderLayout());

        Product p = productService.findById(productId);
        if (p == null) { Message.error(this, "商品不存在"); go(new OrderUI()); return; }

        JLabel title = new JLabel(p.getProductName(), SwingConstants.CENTER);
        title.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());

        // images
        JPanel imgs = new JPanel(new GridLayout(1,5,8,8));
        List<String> paths = imageService.listPaths(productId);
        for (int i=0;i<5;i++){
            String path = i < paths.size() ? paths.get(i) : null;
            JLabel lbl = new JLabel("", SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            ImageIcon icon = ImageUtil.scaledIcon(path, 160, 160);
            if (icon == null) lbl.setText("No Image");
            else lbl.setIcon(icon);
            imgs.add(lbl);
        }
        center.add(imgs, BorderLayout.NORTH);

        // schedule table
        String[] cols = {"depart_date","return_date","seat_stock"};
        Object[][] data = {};
        JTable table = new JTable(data, cols);
        table.setRowHeight(26);

        var model = (javax.swing.table.DefaultTableModel) table.getModel();
        List<ProductSchedule> sch = scheduleService.listByProduct(productId);
        for (ProductSchedule s : sch) {
            model.addRow(new Object[]{s.getDepartDate(), s.getReturnDate(), s.getSeatStock()});
        }
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // description
        JTextArea desc = new JTextArea(p.getDescription());
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        JScrollPane spDesc = new JScrollPane(desc);
        spDesc.setPreferredSize(new Dimension(0, 220));
        center.add(spDesc, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBack = new JButton("返回下單");
        bottom.add(btnBack);
        add(bottom, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> go(new OrderUI()));
        setLocationRelativeTo(null);
    }
}