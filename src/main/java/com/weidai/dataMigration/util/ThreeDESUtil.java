/**
 *
 * Copyright (C), 2011-2017, 微贷网.
 */
package com.weidai.dataMigration.util;

import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * 3des加密
 * 定义加密算法，有DES、DESede(即3DES)、Blowfish
 * @author gaolongyin
 * @version $Id: ThreeDESUtil.java, v 0.1 2017年3月9日 下午3:34:56 gaolongyin Exp $
 */
public class ThreeDESUtil {
    private static final String Algorithm          = "DESede";
    private static final String PASSWORD_CRYPT_KEY = "weidaiwang20110808";

    /**
     * 加密方法
     * @param src 源数据的字节数组
     * @return 
     */
    public static byte[] encryptMode(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), Algorithm); //生成密钥
            Cipher c1 = Cipher.getInstance(Algorithm); //实例化负责加密/解密的Cipher工具类
            c1.init(Cipher.ENCRYPT_MODE, deskey); //初始化为加密模式
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密函数
     * @param src 密文的字节数组
     * @return
     */
    public static byte[] decryptMode(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey); //初始化为解密模式
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字符串生成密钥字节数组 
     * @param keyStr 密钥字符串
     * @return 
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24]; //声明一个24位的字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes("UTF-8"); //将字符串转成字节数组

        /*
         * 执行数组拷贝
         * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
         */
        if (key.length > temp.length) {
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }

    /**
     * 加密(3DES -> base64)
     * @param value
     * @return
     */
    public static String encodeBASE64(String value) {
        if(!StringUtils.hasText(value)) return value;
        return Base64Utils.encode(encryptMode(value.getBytes()));

    }

    /**
     * 解密(base64 -> 3DES)
     *
     * @param value
     * @return
     */
    public static String decodeBASE64(String value){
        if(!StringUtils.hasText(value)) return value;
        byte[] bt = null;
        try {
            bt = decryptMode(Base64Utils.decode(value.toCharArray()));
            if(null == bt){
            	return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(bt);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String value = "653024197412039566";
        String encryValue = encodeBASE64(value);
        System.out.println("加密前字符串:" + value);
        System.out.println("加密后字符串:" + encryValue);
        System.out.println("解密后字符串:" + decodeBASE64(encryValue));

    }
}
