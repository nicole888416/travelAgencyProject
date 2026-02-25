
package controller.order;

import controller.common.BaseFrame;
import controller.common.Message;
import model.Product;
import model.ProductSchedule;
import service.ProductImageService;
import service.ProductScheduleService;
import service.ProductService;
import service.impl.ProductImageServiceImpl;
import service.impl.ProductScheduleServiceImpl;
import service.impl.ProductServiceImpl;
import util.DateUtil;
import util.ImageUtil;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 商品詳情頁
 * - 顯示 product_name
 * - 顯示所有產品圖(最多5張) + 圖片輪播
 * - 顯示所有 depart/return/stock + unit_price
 * - description 大欄位 + scroll
 */
public class ProductDetailUI extends BaseFrame {

    private final ProductService productService = new ProductServiceImpl();
    private final ProductScheduleService scheduleService = new ProductScheduleServiceImpl();
    private final ProductImageService imageService = new ProductImageServiceImpl();

    private int idx = 0;
    private Timer timer;

    public ProductDetailUI(int productId) {
        setTitle("商品詳情");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 760);
        setLayout(new BorderLayout());
        Theme.styleFrame(this);

        Product p = productService.findById(productId);
        if (p == null) { Message.error(this, "商品不存在"); go(new OrderUI()); return; }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.TAIWAN);

        // top
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.NAVY);
        JLabel title = new JLabel(p.getProductName(), SwingConstants.LEFT);
        title.setFont(Theme.H1);
        title.setForeground(Theme.GOLD_SOFT);
        title.setBorder(new EmptyBorder(14, 18, 14, 18));
        top.add(title, BorderLayout.CENTER);

        JLabel price = new JLabel("TWD " + nf.format(p.getProductPrice()) + " 起");
        price.setFont(new Font("Microsoft JhengHei", Font.BOLD, 20));
        price.setForeground(Color.WHITE);
        price.setBorder(new EmptyBorder(14, 18, 14, 18));
        top.add(price, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // center split: left images, right schedule/desc
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.48);

        // LEFT: carousel + thumbnails
        JPanel left = Theme.card();
        left.setLayout(new BorderLayout());

        List<String> paths = imageService.listPaths(productId);

        JLabel carousel = new JLabel("", SwingConstants.CENTER);
        carousel.setPreferredSize(new Dimension(0, 360));
        carousel.setBorder(BorderFactory.createLineBorder(Theme.GOLD));

        JButton btnPrev = Theme.ghostButton("◀");
        JButton btnNext = Theme.ghostButton("▶");

        JPanel carTop = new JPanel(new BorderLayout());
        carTop.setOpaque(false);
        carTop.add(btnPrev, BorderLayout.WEST);
        carTop.add(btnNext, BorderLayout.EAST);
        left.add(carTop, BorderLayout.NORTH);
        left.add(carousel, BorderLayout.CENTER);

        JPanel thumbs = new JPanel(new GridLayout(1, Math.max(1, Math.min(5, paths.size())), 8, 8));
        thumbs.setOpaque(false);
        thumbs.setBorder(new EmptyBorder(10, 0, 0, 0));

        if (paths.isEmpty()) {
            JLabel none = new JLabel("尚未上傳產品圖(可到後台新增)", SwingConstants.CENTER);
            none.setForeground(new Color(90,90,90));
            thumbs.setLayout(new BorderLayout());
            thumbs.add(none, BorderLayout.CENTER);
        } else {
            for (int i=0;i<paths.size() && i<5;i++){
                int clickIndex = i;
                JLabel t = new JLabel("", SwingConstants.CENTER);
                t.setBorder(BorderFactory.createLineBorder(new Color(210,210,210)));
                ImageIcon ti = ImageUtil.scaledIcon(paths.get(i), 140, 100);
                if (ti != null) t.setIcon(ti);
                t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                t.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        idx = clickIndex;
                        showImage(paths, carousel);
                    }
                });
                thumbs.add(t);
            }
        }

        left.add(thumbs, BorderLayout.SOUTH);

        // carousel init + timer
        showImage(paths, carousel);
        timer = new Timer(2600, e -> {
            if (paths == null || paths.isEmpty()) return;
            idx = (idx + 1) % Math.min(paths.size(), 5);
            showImage(paths, carousel);
        });
        timer.start();

        btnPrev.addActionListener(e -> {
            if (paths == null || paths.isEmpty()) return;
            idx = (idx - 1 + Math.min(paths.size(), 5)) % Math.min(paths.size(), 5);
            showImage(paths, carousel);
        });
        btnNext.addActionListener(e -> {
            if (paths == null || paths.isEmpty()) return;
            idx = (idx + 1) % Math.min(paths.size(), 5);
            showImage(paths, carousel);
        });

        // RIGHT: schedules + desc
        JPanel right = Theme.card();
        right.setLayout(new BorderLayout());

        JLabel sTitle = new JLabel("可出發日期 / 回程日期 / 機位");
        sTitle.setFont(Theme.H2);
        sTitle.setForeground(Theme.NAVY);
        sTitle.setBorder(new EmptyBorder(0,0,8,0));
        right.add(sTitle, BorderLayout.NORTH);

        DefaultTableModel tm = new DefaultTableModel(new Object[]{"depart_date","return_date","product_stock","unit_price"}, 0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable schTable = new JTable(tm);
        schTable.setRowHeight(30);

        for (ProductSchedule s : scheduleService.listByProduct(productId)) {
            tm.addRow(new Object[]{
                    DateUtil.fmt(s.getDepartDate()),
                    DateUtil.fmt(s.getReturnDate()),
                    s.getSeatStock(),
                    nf.format(p.getProductPrice())
            });
        }

        right.add(new JScrollPane(schTable), BorderLayout.CENTER);

        JLabel dTitle = new JLabel("行程內容 / Description");
        dTitle.setFont(Theme.H2);
        dTitle.setForeground(Theme.NAVY);
        dTitle.setBorder(new EmptyBorder(10,0,6,0));

        JTextArea desc = new JTextArea(p.getDescription());
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setFont(Theme.BODY);

        JScrollPane spDesc = new JScrollPane(desc);
        spDesc.setPreferredSize(new Dimension(0, 240));

        JPanel descWrap = new JPanel(new BorderLayout());
        descWrap.setOpaque(false);
        descWrap.add(dTitle, BorderLayout.NORTH);
        descWrap.add(spDesc, BorderLayout.CENTER);

        right.add(descWrap, BorderLayout.SOUTH);

        split.setLeftComponent(left);
        split.setRightComponent(right);

        add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setBackground(Theme.BG);
        JButton btnBack = Theme.primaryButton("返回下單");
        bottom.add(btnBack);
        add(bottom, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> go(new OrderUI()));
        setLocationRelativeTo(null);
    }

    private void showImage(List<String> paths, JLabel carousel) {
        if (paths == null || paths.isEmpty()) {
            carousel.setIcon(ImageUtil.scaledResource("/images/showImage.png", 520, 360));
            carousel.setText("");
            return;
        }
        int limit = Math.min(paths.size(), 5);
        if (idx >= limit) idx = 0;
        ImageIcon icon = ImageUtil.scaledIcon(paths.get(idx), 520, 360);
        if (icon == null) {
            carousel.setIcon(ImageUtil.scaledResource("/images/showImage.png", 520, 360));
            carousel.setText("");
        } else {
            carousel.setIcon(icon);
            carousel.setText("");
        }
    }

    @Override
    public void dispose() {
        if (timer != null) timer.stop();
        super.dispose();
    }
}
