package controller;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import controller.common.LoginUI;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ignore) {}
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
