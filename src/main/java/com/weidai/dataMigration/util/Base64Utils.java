package com.weidai.dataMigration.util;

import java.net.URLDecoder;

/**
 * Base64工具类
 *
 * @author gaolongyin
 * @version $Id: Base64Utils.java, v 0.1 2017年3月21日 下午1:42:33 gaolongyin Exp $
 */
public class Base64Utils {

    private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static final byte[] codes    = new byte[256];

    static {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }

    /** 
     * 将原始数据编码为base64编码 
     */
    public static String encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }

        return new String(out);
    }

    /** 
     * 将base64编码的数据解码成原始数据 
     * @throws Exception 
     */
    public static byte[] decode(char[] data) throws Exception {
        int len = ((data.length + 3) / 4) * 3;
        if (data.length > 0 && data[data.length - 1] == '=')
            --len;
        if (data.length > 1 && data[data.length - 2] == '=')
            --len;
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (int ix = 0; ix < data.length; ix++) {
            int value = codes[data[ix] & 0xFF];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        if (index != out.length)
            throw new Exception("miscalculated data length!");
        return out;
    }

    public static void main(String[] a) throws Exception {
        // String input = "test";
        String url = "application%3Ducore%26default.check%3Dfalse%26dubbo%3D2.5.3%26generic%3Dtrue%26interface%3Dcom.weidai.ucore.facade.core.UserServiceFacade%26logger%3Dslf4j%26pid%3D4516%26side%3Dconsumer%26timestamp%3D1499940558533%26version%3D1.0";
        System.out.println(URLDecoder.decode(url, "UTF-8"));
    }
}