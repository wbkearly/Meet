package com.wbk.framework.utils;

import java.security.MessageDigest;

/**
 * 哈希计算
 */
public class SHA1 {

    private static final char[] HEXES = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    /**
     * 融云加密算法
     * Java中byte用二进制表示占用8bit
     * 16进制每个字符需要4bit
     * 因此 将每个byte转换成两个相应的16进制字符，
     * 即把byte的高4位和低4位分别转换成相应的16进制字符H和L
     * 并组合起来得到byte转换到16进制字符串的结果同理，
     * 相反的转换也是将两个16进制字符转换成一个byte
     */
    public static String sha1(String data){
        StringBuilder builder = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] bits = md.digest(data.getBytes());
            for (int bit : bits) {
                builder.append(HEXES[(bit >> 4) & 0x0F]);
                builder.append(HEXES[bit & 0x0F]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
