
package controller.product;

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
import util.CsvUtil;
import util.DateUtil;
import util.ImageUtil;
import util.Theme;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductManagerUI (後台)
 * 重要修正：
 * 1) 右側改成可滾動，避免日期/圖片區被擠到看不到（你遇到的「無法選日期」「找不到上傳圖欄位」通常是版面高度不足）
 * 2) 支援「先輸入行程/先選圖片」再新增商品（暫存，新增成功後自動寫入 DB）
 */
public class ProductManagerUI extends BaseFrame {

    private final ProductService productService = new ProductServiceImpl();
    private final ProductScheduleService scheduleService = new ProductScheduleServiceImpl();
    private final ProductImageService imageService = new ProductImageServiceImpl();

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtNo, txtName, txtPrice, txtStock;
    private JTextArea txtDesc;

    private JLabel[] imgPreview = new JLabel[5];
    private String[] imgPaths = new String[5];

    private JTable scheduleTable;
    private DefaultTableModel scheduleModel;
    private JDateChooser dcDepart, dcReturn;
    private JTextField txtSeat;

    // 暫存（當尚未建立商品、沒有 product_id 的時候）
    private final List<ProductSchedule> tempSchedules = new ArrayList<>();

    public ProductManagerUI() {
        setTitle("商品管理(後台)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 760);
        setLayout(new BorderLayout());
        Theme.styleFrame(this);

        // 左側：商品列表
        model = new DefaultTableModel(new Object[]{"id","product_no","product_name","price","stock"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);

        JScrollPane spLeft = new JScrollPane(table);
        spLeft.setPreferredSize(new Dimension(520, 0));
        add(spLeft, BorderLayout.WEST);

        // 右側：可滾動的表單（修正版）
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel hero = new JLabel();
        hero.setIcon(ImageUtil.scaledResource("/images/hero_admin.png", 760, 150));
        hero.setBorder(BorderFactory.createLineBorder(Theme.GOLD));
        rightWrap.add(hero, BorderLayout.NORTH);

        JPanel formStack = new JPanel();
        formStack.setOpaque(false);
        formStack.setLayout(new BoxLayout(formStack, BoxLayout.Y_AXIS));
        formStack.setBorder(new EmptyBorder(12, 0, 12, 0));

        formStack.add(buildBasicForm());
        formStack.add(Box.createVerticalStrut(10));
        formStack.add(buildImagesSection());
        formStack.add(Box.createVerticalStrut(10));
        formStack.add(buildScheduleSection());

        JScrollPane spRight = new JScrollPane(formStack);
        spRight.setBorder(BorderFactory.createEmptyBorder());
        spRight.getVerticalScrollBar().setUnitIncrement(16);

        rightWrap.add(spRight, BorderLayout.CENTER);

        // 下方按鈕列
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottom.setBackground(Theme.BG);

        JButton btnNew = Theme.goldButton("新增商品(含暫存圖片/行程)");
        JButton btnUpdate = Theme.primaryButton("修改商品");
        JButton btnDelete = Theme.ghostButton("刪除商品");
        JButton btnReload = Theme.ghostButton("重新整理");
        JButton btnExport = Theme.ghostButton("列印 CSV(商品表)");
        JButton btnBack = Theme.primaryButton("back");

        bottom.add(btnNew);
        bottom.add(btnUpdate);
        bottom.add(btnDelete);
        bottom.add(btnReload);
        bottom.add(btnExport);
        bottom.add(btnBack);

        rightWrap.add(bottom, BorderLayout.SOUTH);

        add(rightWrap, BorderLayout.CENTER);

        // events
        btnNew.addActionListener(e -> createProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnReload.addActionListener(e -> loadProducts());
        btnExport.addActionListener(e -> exportCsv());
        btnBack.addActionListener(e -> go(new controller.employee.EmployeeUI()));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelected();
        });

        setLocationRelativeTo(null);
        loadProducts();
    }

    private JPanel buildBasicForm() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout());

        JLabel title = new JLabel("商品基本資料");
        title.setFont(Theme.H2);
        title.setForeground(Theme.NAVY);
        title.setBorder(new EmptyBorder(0,0,8,0));
        card.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int y=0;
        gc.gridx=0; gc.gridy=y; form.add(new JLabel("product_no(不可重複):"), gc);
        gc.gridx=1; txtNo = new JTextField(); txtNo.setFont(Theme.BODY); form.add(txtNo, gc); y++;

        gc.gridx=0; gc.gridy=y; form.add(new JLabel("product_name:"), gc);
        gc.gridx=1; txtName = new JTextField(); txtName.setFont(Theme.BODY); form.add(txtName, gc); y++;

        gc.gridx=0; gc.gridy=y; form.add(new JLabel("product_price:"), gc);
        gc.gridx=1; txtPrice = new JTextField(); txtPrice.setFont(Theme.BODY); form.add(txtPrice, gc); y++;

        gc.gridx=0; gc.gridy=y; form.add(new JLabel("product_stock(預設):"), gc);
        gc.gridx=1; txtStock = new JTextField("0"); txtStock.setFont(Theme.BODY); form.add(txtStock, gc); y++;

        gc.gridx=0; gc.gridy=y; gc.anchor = GridBagConstraints.NORTH;
        form.add(new JLabel("description(可放很多文字):"), gc);

        gc.gridx=1;
        txtDesc = new JTextArea(8, 30);
        txtDesc.setFont(Theme.BODY);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(txtDesc);
        spDesc.setPreferredSize(new Dimension(0, 160));
        form.add(spDesc, gc);

        card.add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel("提示：若右側區塊看不到日期/圖片，請用滑鼠滾輪往下（本頁已改為可滾動版面）");
        hint.setForeground(new Color(90, 90, 90));
        hint.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(hint, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildImagesSection() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout());
        JLabel title = new JLabel("產品圖(最多 5 張)  ※可先選圖暫存，新增商品後會自動寫入 DB");
        title.setFont(Theme.H2);
        title.setForeground(Theme.NAVY);
        title.setBorder(new EmptyBorder(0,0,8,0));
        card.add(title, BorderLayout.NORTH);

        JPanel imgGrid = new JPanel(new GridLayout(1,5,10,10));
        imgGrid.setOpaque(false);
        for (int i=0;i<5;i++){
            JLabel lbl = new JLabel("No."+(i+1), SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Theme.GOLD));
            lbl.setPreferredSize(new Dimension(150, 150));
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            imgPreview[i]=lbl;
            imgGrid.add(lbl);
        }
        card.add(imgGrid, BorderLayout.CENTER);

        JPanel imgBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        imgBtns.setOpaque(false);
        for (int i=0;i<5;i++){
            int idx=i;
            JButton bAdd = Theme.primaryButton("新增/修改圖"+(i+1));
            JButton bDel = Theme.ghostButton("刪除圖"+(i+1));
            bAdd.addActionListener(e -> chooseImage(idx));
            bDel.addActionListener(e -> deleteImage(idx));
            imgBtns.add(bAdd); imgBtns.add(bDel);
        }
        card.add(imgBtns, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildScheduleSection() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout());

        JLabel title = new JLabel("出發/回程/機位(可多筆)  ※可先新增行程暫存，新增商品後會自動寫入 DB");
        title.setFont(Theme.H2);
        title.setForeground(Theme.NAVY);
        title.setBorder(new EmptyBorder(0,0,8,0));
        card.add(title, BorderLayout.NORTH);

        scheduleModel = new DefaultTableModel(new Object[]{"id","depart_date","return_date","seat_stock"}, 0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        scheduleTable = new JTable(scheduleModel);
        scheduleTable.setRowHeight(28);
        card.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel schTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        schTop.setOpaque(false);

        schTop.add(new JLabel("depart_date:"));
        dcDepart = new JDateChooser();
        dcDepart.setDateFormatString("yyyy-MM-dd");
        schTop.add(dcDepart);

        schTop.add(new JLabel("return_date:"));
        dcReturn = new JDateChooser();
        dcReturn.setDateFormatString("yyyy-MM-dd");
        schTop.add(dcReturn);

        schTop.add(new JLabel("seat_stock:"));
        txtSeat = new JTextField("0", 6);
        schTop.add(txtSeat);

        JButton btnAddSch = Theme.primaryButton("新增行程");
        JButton btnUpdSch = Theme.goldButton("修改行程");
        JButton btnDelSch = Theme.ghostButton("刪除行程");

        schTop.add(btnAddSch);
        schTop.add(btnUpdSch);
        schTop.add(btnDelSch);

        btnAddSch.addActionListener(e -> addSchedule());
        btnUpdSch.addActionListener(e -> updateSchedule());
        btnDelSch.addActionListener(e -> deleteSchedule());

        card.add(schTop, BorderLayout.SOUTH);

        return card;
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<Product> list = productService.listAll();
        for (Product p : list) {
            model.addRow(new Object[]{p.getId(), p.getProductNo(), p.getProductName(), p.getProductPrice(), p.getProductStock()});
        }
        clearFormToNewMode();
    }

    private Integer selectedProductId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
    }

    private void clearFormToNewMode() {
        txtNo.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("0");
        txtDesc.setText("");

        // 暫存清空
        tempSchedules.clear();

        for(int i=0;i<5;i++){
            imgPaths[i]=null;
            refreshImg(i);
        }
        scheduleModel.setRowCount(0);

        txtNo.setEnabled(true); // 新增模式 product_no 可輸入
        table.clearSelection();
    }

    private void loadSelected() {
        Integer id = selectedProductId();
        if (id == null) return;

        Product p = productService.findById(id);
        if (p == null) return;

        txtNo.setText(p.getProductNo());
        txtName.setText(p.getProductName());
        txtPrice.setText(String.valueOf(p.getProductPrice()));
        txtStock.setText(String.valueOf(p.getProductStock()));
        txtDesc.setText(p.getDescription());

        // images
        List<String> paths = imageService.listPaths(id);
        for(int i=0;i<5;i++){ imgPaths[i]=null; }
        for(int i=0;i<paths.size() && i<5;i++){ imgPaths[i]=paths.get(i); }
        for(int i=0;i<5;i++){ refreshImg(i); }

        // schedules
        List<ProductSchedule> sch = scheduleService.listByProduct(id);
        scheduleModel.setRowCount(0);
        for(ProductSchedule s: sch){
            scheduleModel.addRow(new Object[]{s.getId(), DateUtil.fmt(s.getDepartDate()), DateUtil.fmt(s.getReturnDate()), s.getSeatStock()});
        }

        // 選到既有商品時，不使用暫存
        tempSchedules.clear();

        txtNo.setEnabled(false);
    }

    private void refreshImg(int idx) {
        ImageIcon icon = ImageUtil.scaledIcon(imgPaths[idx], 140, 140);
        if (icon == null) { imgPreview[idx].setIcon(null); imgPreview[idx].setText("No."+(idx+1)); }
        else { imgPreview[idx].setText(""); imgPreview[idx].setIcon(icon); }
    }

    private void chooseImage(int idx) {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();

            Integer pid = selectedProductId();
            if (pid == null) {
                // 新增模式：先暫存，等新增商品後一次寫入 DB
                imgPaths[idx] = path;
                refreshImg(idx);
                Message.info(this, "已暫存圖片 No."+(idx+1)+"，新增商品後會自動寫入資料庫");
            } else {
                // 編輯模式：直接寫入 DB
                imageService.upsert(pid, idx+1, path);
                imgPaths[idx]=path;
                refreshImg(idx);
                Message.info(this, "已更新圖片 No."+(idx+1));
            }
        }
    }

    private void deleteImage(int idx) {
        Integer pid = selectedProductId();
        if (pid == null) {
            imgPaths[idx]=null;
            refreshImg(idx);
            Message.info(this, "已刪除暫存圖片 No."+(idx+1));
        } else {
            imageService.delete(pid, idx+1);
            imgPaths[idx]=null;
            refreshImg(idx);
            Message.info(this, "已刪除圖片 No."+(idx+1));
        }
    }

    private void createProduct() {
        try {
            Product p = new Product();
            p.setProductNo(txtNo.getText());
            p.setProductName(txtName.getText());
            p.setProductPrice(Integer.parseInt(txtPrice.getText().trim()));
            p.setProductStock(Integer.parseInt(txtStock.getText().trim()));
            p.setDescription(txtDesc.getText());

            Product saved = productService.create(p);

            // 1) 把暫存圖片寫入 DB
            for (int i=0;i<5;i++){
                if (imgPaths[i] != null && !imgPaths[i].trim().isEmpty()) {
                    imageService.upsert(saved.getId(), i+1, imgPaths[i]);
                }
            }

            // 2) 把暫存行程寫入 DB
            for (ProductSchedule s : tempSchedules) {
                s.setProductId(saved.getId());
                scheduleService.create(s);
            }

            Message.info(this, "新增成功！已寫入暫存圖片/行程。\n請在左側點選該商品可再修改。");
            loadProducts();

            // 自動選到新增那筆
            selectRowByProductNo(saved.getProductNo());

        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }

    private void selectRowByProductNo(String productNo) {
        for (int i=0;i<model.getRowCount();i++){
            if (String.valueOf(model.getValueAt(i,1)).equals(productNo)) {
                table.setRowSelectionInterval(i,i);
                break;
            }
        }
    }

    private void updateProduct() {
        try {
            Integer pid = selectedProductId();
            if (pid == null) { Message.error(this, "請先在左側選擇商品"); return; }
            Product p = productService.findById(pid);
            if (p == null) return;

            p.setProductName(txtName.getText());
            p.setProductPrice(Integer.parseInt(txtPrice.getText().trim()));
            p.setProductStock(Integer.parseInt(txtStock.getText().trim()));
            p.setDescription(txtDesc.getText());
            productService.update(p);
            Message.info(this, "修改成功");
            loadProducts();
            selectRowByProductNo(p.getProductNo());
        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }

    private void deleteProduct() {
        try {
            Integer pid = selectedProductId();
            if (pid == null) { Message.error(this, "請先選擇商品"); return; }
            int ok = JOptionPane.showConfirmDialog(this, "確定刪除？(含行程/圖片/明細)", "確認", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            productService.delete(pid);
            Message.info(this, "刪除成功");
            loadProducts();
        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }

    private Integer selectedScheduleId() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) return null;
        Object v = scheduleModel.getValueAt(row, 0);
        if (v == null) return null;
        String s = String.valueOf(v);
        if (s.trim().isEmpty() || "TEMP".equalsIgnoreCase(s.trim())) return null;
        return Integer.parseInt(s);
    }

    private void addSchedule() {
        try {
            if (dcDepart.getDate() == null || dcReturn.getDate() == null) {
                Message.error(this, "請選擇 depart_date 與 return_date");
                return;
            }
            int seat = Integer.parseInt(txtSeat.getText().trim());
            if (seat < 0) { Message.error(this, "seat_stock 不可為負數"); return; }

            Integer pid = selectedProductId();
            if (pid == null) {
                // 新增模式：暫存
                ProductSchedule s = new ProductSchedule();
                s.setDepartDate(dcDepart.getDate());
                s.setReturnDate(dcReturn.getDate());
                s.setSeatStock(seat);
                tempSchedules.add(s);
                scheduleModel.addRow(new Object[]{"TEMP", DateUtil.fmt(s.getDepartDate()), DateUtil.fmt(s.getReturnDate()), s.getSeatStock()});
                Message.info(this, "已暫存行程，新增商品後會自動寫入資料庫");
            } else {
                ProductSchedule s = new ProductSchedule();
                s.setProductId(pid);
                s.setDepartDate(dcDepart.getDate());
                s.setReturnDate(dcReturn.getDate());
                s.setSeatStock(seat);
                scheduleService.create(s);
                Message.info(this, "新增行程成功");
                loadSelected();
            }
        } catch (Exception ex) {
            Message.error(this, ex.getMessage());
        }
    }

    private void updateSchedule() {
        try {
            Integer pid = selectedProductId();
            if (pid == null) {
                Message.error(this, "暫存行程不支援修改：請刪除後重新新增(或先新增商品再修改)");
                return;
            }
            Integer sid = selectedScheduleId();
            if (sid == null) { Message.error(this, "請先選擇一筆行程"); return; }
            ProductSchedule s = scheduleService.findById(sid);
            if (s == null) return;

            s.setDepartDate(dcDepart.getDate());
            s.setReturnDate(dcReturn.getDate());
            s.setSeatStock(Integer.parseInt(txtSeat.getText().trim()));
            scheduleService.update(s);
            Message.info(this, "修改行程成功");
            loadSelected();
        } catch (Exception ex) { Message.error(this, ex.getMessage()); }
    }

    private void deleteSchedule() {
        try {
            Integer pid = selectedProductId();
            int row = scheduleTable.getSelectedRow();
            if (row < 0) { Message.error(this, "請先選擇一筆行程"); return; }

            if (pid == null) {
                // delete temp row
                tempSchedules.remove(row);
                scheduleModel.removeRow(row);
                Message.info(this, "已刪除暫存行程");
            } else {
                Integer sid = Integer.parseInt(String.valueOf(scheduleModel.getValueAt(row, 0)));
                scheduleService.delete(sid);
                Message.info(this, "刪除行程成功");
                loadSelected();
            }
        } catch (Exception ex) { Message.error(this, ex.getMessage()); }
    }

    private void exportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("products.csv"));
        int r = fc.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                CsvUtil.exportJTableToCsv(table, fc.getSelectedFile());
                Message.info(this, "已輸出: " + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) { Message.error(this, ex.getMessage()); }
        }
    }
}
