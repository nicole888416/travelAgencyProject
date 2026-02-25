
package util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class ImageUtil {

    public static ImageIcon scaledIcon(String path, int w, int h) {
        if (path == null || path.trim().isEmpty()) return null;
        File f = new File(path);
        if (!f.exists()) return null;

        ImageIcon src = new ImageIcon(path);
        Image img = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static ImageIcon scaledResource(String resourcePath, int w, int h) {
        try {
            URL url = ImageUtil.class.getResource(resourcePath);
            if (url == null) return null;
            ImageIcon src = new ImageIcon(url);
            Image img = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
