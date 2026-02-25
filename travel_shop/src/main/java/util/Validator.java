package util;

import java.util.regex.Pattern;

public class Validator {

    // 修正：在 Java 字串中 \d 必須寫成 \\d
    private static final Pattern PHONE = Pattern.compile("^09\\d{8}$");
    // 修正：. 在正規表示式中需轉義為 \\. 才能精準匹配點號
    private static final Pattern EMAIL = Pattern.compile("^[a-z0-9]+@[a-z0-9]+\\.[a-z0-9]+$");

    public static void requireNotBlank(String v, String fieldName) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(fieldName + "不可為空白");
    }

    public static void validatePhone(String phone) {
        requireNotBlank(phone, "手機");
        String p = phone.trim();
        if (!PHONE.matcher(p).matches()) throw new IllegalArgumentException("手機必須為 10 碼、09 開頭的數字");
    }

    public static void validateEmail(String email) {
        requireNotBlank(email, "Email");
        String e = email.trim();
        if (!EMAIL.matcher(e).matches()) throw new IllegalArgumentException("Email 格式錯誤，範例: aaa@bbb.ccc (小寫/數字)");
    }
}
