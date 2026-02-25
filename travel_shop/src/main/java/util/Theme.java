
package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Theme {

    // Luxury palette: Deep Navy + Gold
    public static final Color NAVY = new Color(10, 25, 55);
    public static final Color NAVY_2 = new Color(16, 40, 85);
    public static final Color GOLD = new Color(200, 170, 90);
    public static final Color GOLD_SOFT = new Color(240, 215, 140);
    public static final Color BG = new Color(245, 246, 248);

    public static final Font H1 = new Font("Microsoft JhengHei", Font.BOLD, 24);
    public static final Font H2 = new Font("Microsoft JhengHei", Font.BOLD, 18);
    public static final Font BODY = new Font("Microsoft JhengHei", Font.PLAIN, 14);

    private Theme(){}

    public static void styleFrame(JFrame f) {
        f.getContentPane().setBackground(BG);
    }

    public static JPanel section(String title) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(H2);
        t.setForeground(NAVY);
        t.setBorder(new EmptyBorder(6, 6, 6, 6));
        wrap.add(t, BorderLayout.NORTH);
        return wrap;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(NAVY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(BODY.deriveFont(Font.BOLD));
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return b;
    }

    public static JButton goldButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(NAVY);
        b.setFocusPainted(false);
        b.setFont(BODY.deriveFont(Font.BOLD));
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setForeground(NAVY);
        b.setFocusPainted(false);
        b.setFont(BODY.deriveFont(Font.BOLD));
        b.setBorder(BorderFactory.createLineBorder(GOLD));
        return b;
    }

    public static JLabel badge(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(NAVY);
        l.setForeground(GOLD_SOFT);
        l.setBorder(new EmptyBorder(6,10,6,10));
        l.setFont(BODY.deriveFont(Font.BOLD));
        return l;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(12,12,12,12)
        ));
        return p;
    }
}
