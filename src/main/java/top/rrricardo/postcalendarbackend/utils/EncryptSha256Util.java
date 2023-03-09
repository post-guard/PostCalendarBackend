package top.rrricardo.postcalendarbackend.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptSha256Util {
    /**
     * 对字符串进行sha256操作
     */
    public static String sha256String(String input) {
        try {
            var messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
            return byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException ignored) {
            // 不可到达的代码
            return "";
        }
    }

    /**
     * 将字节数组转换为字符串
     */
    private static String byte2Hex(byte[] input) {
        var builder = new StringBuilder();
        for (var b: input) {
            var temp = Integer.toHexString(b & 0xff);
            if (temp.length() == 1) {
                builder.append('0');
            }
            builder.append(temp);
        }
        return builder.toString();
    }
}
