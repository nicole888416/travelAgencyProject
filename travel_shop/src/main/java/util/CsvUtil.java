package util;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Excel 中文不亂碼：用 UTF-8 BOM。
 */
public class CsvUtil {

    public static void exportJTableToCsv(JTable table, File file) throws IOException {
        try (OutputStream os = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {

            // UTF-8 BOM for Excel
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);

            TableModel m = table.getModel();

            // header
            for (int c = 0; c < m.getColumnCount(); c++) {
                bw.write(escape(m.getColumnName(c)));
                if (c < m.getColumnCount()-1) bw.write(",");
            }
            bw.newLine();

            // rows
            for (int r = 0; r < m.getRowCount(); r++) {
                for (int c = 0; c < m.getColumnCount(); c++) {
                    Object v = m.getValueAt(r, c);
                    bw.write(escape(v == null ? "" : String.valueOf(v)));
                    if (c < m.getColumnCount()-1) bw.write(",");
                }
                bw.newLine();
            }
        }
    }

    private static String escape(String s) {
        String v = s.replace("", "");
        if (v.contains(",") || v.contains("\n") || v.contains("\r")) {
            return "" + v + "";
        }
        return v;
    }
}
