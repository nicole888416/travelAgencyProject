package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class IdUtil {

    public static String customerNo(int id) {
        return "C" + String.format("%06d", id);
    }

    public static String employeeNo(int id) {
        return "E" + String.format("%06d", id);
    }

    public static String orderNo() {
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int r = ThreadLocalRandom.current().nextInt(100, 999);
        return "O" + ts + r;
    }
}
