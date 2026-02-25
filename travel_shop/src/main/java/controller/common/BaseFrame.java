package controller.common;

import javax.swing.JFrame;

public abstract class BaseFrame extends JFrame {
    protected void go(JFrame next) {
        next.setLocationRelativeTo(null);
        next.setVisible(true);
        this.dispose(); // 每次跳轉新頁面後，前一頁自動關掉
    }
}
