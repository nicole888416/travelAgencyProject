package controller.common;

import javax.swing.JOptionPane;
import java.awt.Component;

public class Message {
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "訊息", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "錯誤", JOptionPane.ERROR_MESSAGE);
    }
}
