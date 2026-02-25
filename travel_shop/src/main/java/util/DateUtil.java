
package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat F = new SimpleDateFormat("yyyy-MM-dd");

    private DateUtil(){}

    public static String fmt(Date d) {
        if (d == null) return "";
        return F.format(d);
    }
}
