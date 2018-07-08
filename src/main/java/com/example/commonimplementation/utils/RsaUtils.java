package com.example.commonimplementation.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * woody
 */
public class RsaUtils {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static byte[] decryptBASE64(String key) {
        return Base64.getDecoder().decode(key);
    }

    public static String encryptBASE64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }




    /**
     * 私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(String data, String key) throws Exception {
        return decryptByPrivateKey(decryptBASE64(data), key);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }




    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(String data, String key) throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] dataByteArray = data.getBytes();
        int inputLen = dataByteArray.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(dataByteArray, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataByteArray, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Key> keyMap) {
        Key key = keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Key> keyMap) {
        Key key = keyMap.get(PUBLIC_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        Map<String, Key> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, keyPair.getPublic());// 公钥
        keyMap.put(PRIVATE_KEY, keyPair.getPrivate());// 私钥
        return keyMap;
    }


    /**
     * 获取参数名称 key
     *
     * @param maps 参数key-value map集合
     * @return
     */
    public static List<String> getParamsName(Map<String, Object> maps) {
        List<String> paramNames = new ArrayList<>();
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            paramNames.add(entry.getKey());
        }
        return paramNames;
    }

    /**
     * 参数名称按字典排序
     *
     * @param paramNames 参数名称List集合
     * @return 排序后的参数名称List集合
     */
    public static List<String> lexicographicOrder(List<String> paramNames) {
        Collections.sort(paramNames);
        return paramNames;
    }

    /**
     * 拼接排序好的参数名称和参数值
     *
     * @param paramNames 排序后的参数名称集合
     * @param maps       参数key-value map集合
     * @return String 拼接后的字符串
     */
    public static String splitParams(List<String> paramNames, Map<String, Object> maps) {
        StringBuilder paramStr = new StringBuilder();
        for (String paramName : paramNames) {
            paramStr.append(paramName);
            for (Map.Entry<String, Object> entry : maps.entrySet()) {
                if (paramName.equals(entry.getKey())) {
                    paramStr.append("=").append(String.valueOf(entry.getValue())).append("&");
                }
            }
        }
        return paramStr.toString().substring(0, paramStr.length() - 1);
    }


    /**
     * 校验是否参数加签正确
     * @param map
     * @param sign
     * @param privateKey
     * @return
     */
    public static boolean verify(Map<String,Object> map,String sign,String privateKey){


        try {
            String text = splitParams(lexicographicOrder(getParamsName(map)), map);
            String  descrpt = new String(decryptByPrivateKey(decryptBASE64(sign),privateKey)) ;

            return text!=null && text.equalsIgnoreCase(descrpt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * 私钥加密实现
     * @param map
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(Map<String,Object> map,String privateKey) throws Exception {
        String text = splitParams(lexicographicOrder(getParamsName(map)), map);

        return encryptBASE64(encryptByPrivateKey(text.getBytes(),privateKey));
    }



    public static void main(String[] args) throws Exception {
        String rsaprivateKeyPkcs8 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALom7tXn6jKsRd41cR9pJ3FsdHmV8Dj9mJtpoYxcsGwMHIL/Q7LXArkKTVHZMQlE/JnTuIX1sX7nZaUMy7+bfro7bKX6Mo6j085KgbftHPG7S+Vb2w0Xr3LOkQVMt3P9fi3PYCFofc7BCrw+RJ33d+pSH9TpCvw1sm0VwACOrZKvAgMBAAECgYAxzQqmZWYNanNCsG90JNCtkoNSp99dj9LM+SSEBaQZM+BI13vIE6eh03S+CeoJfdBKYw8Bn6p9tfBGiLbVqwbq3HnqgG9Z5GYEcJTgHOC0xClxjqdNjGQn4NYHrLh0mAYez/9JkspqKMaZ1C8uuYEJNNCvX2xtUc73+/1CHEXv8QJBAO/kWae3W8vmB7NelURwoGUj03e1qEvrsEsKO/G6X5p5bErZK3joOiTi9as+qsefXvk33+uQrflGKhkDLJxkmokCQQDGptCLSAvggD254hPfIzvYgvNq23c6GC3Ym6xVC0gnsuAtmxKU/CsKrJPw6GJZY6rQkCw9J+LxtIZ1kn+AOJV3AkAWyijCSCcOXdIJdkjT8acrNJYYX0eXvtJGrrg3JK+Ea1igW7VHjThI3M7d79wljrxlDKvXIy2D6uiA685HJT+RAkBjsz1fah1r43EXZfwLNafHlpGMw/Em2xPHbvojBLKGGiShPv8ofxcrGEp30mnp6zfDzVgmqWohXTVOtOQA+mUpAkEAu1uzg9jxpeRhb+kc1CGuF6KI3vnA/1r/J3gQpJdg/jRh+8uPreTLb+lMHz6uy2Z49/3xQfWJ98+OZph4ia1CNw==";

        String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6Ju7V5+oyrEXeNXEfaSdxbHR5lfA4/ZibaaGMXLBsDByC/0Oy1wK5Ck1R2TEJRPyZ07iF9bF+52WlDMu/m366O2yl+jKOo9POSoG37Rzxu0vlW9sNF69yzpEFTLdz/X4tz2AhaH3OwQq8PkSd93fqUh/U6Qr8NbJtFcAAjq2SrwIDAQAB";



        String str=  "加签";

        byte  []enctrStr = encryptByPublicKey(str,publicKey);


        System.out.println(new String(decryptByPrivateKey(enctrStr,rsaprivateKeyPkcs8)));
        Map maps = new LinkedHashMap();

        maps.put("A","a");
        maps.put("B","b");

        String text = splitParams(lexicographicOrder(getParamsName(maps)), maps);


        System.out.println(text);

        String sign = encryptBASE64(encryptByPublicKey(text,publicKey));

        System.out.println("加密后："+sign);

        String  descrpt = new String(decryptByPrivateKey(decryptBASE64(sign),rsaprivateKeyPkcs8)) ;

        System.out.println("解密后："+descrpt);

        sign="aiPXDbXUgnugYDbz8re4gzZgXCvkg1lV2CThCdc1iqbNRaLVO1EZ7esHqgVFUjagfvxpc0kj6Uyg6XjcQVSLSijoYTe286S0CJs4Jj/m0yexUkTjpJVkW6NdkirYEnEpBcCFYI6Eq1s+3Nn/N5YT2egPpSBAnMAvHuiolTFyY2o=";
        System.out.println(verify(maps,sign,rsaprivateKeyPkcs8));


        System.out.println(encryptBASE64(encryptByPrivateKey(text.getBytes(),rsaprivateKeyPkcs8)));

        System.out.println(new String(decryptByPublicKey(decryptBASE64("Hid8gW7ImI94+8EshtxVuvwi2WSku48YxBf1tcQjlltrzxoCoV6Db/jZJgL3EGONoGiv1IAf7YqM6bhUqfgolLMq5nv7nrrt1gDgvPEqFuhOTBVm7B6YXzW3oqnS2BbRT9bU6mLbrF4Z1JCUNWPoawHmy+jaIDtiWfVa10LLeUw="),publicKey)));
        System.out.println(new String(decryptByPublicKey(decryptBASE64("ZQSNodRfqdXZY1ivoc58/1oDmDs5dgIVuj8zu6GLE2G2aVFolSytJWkXSazyCr+xUP5vI0Be9PCGNh0B1JjvzQ4Rq143kdR2ocaObHvnX3Jj2DqNzzQ3ZhsFFUBlTfL6jqogrnaelighheKu3/bvDnB21UA50B9CL93s5juxKj8="),publicKey)));



    }
}
