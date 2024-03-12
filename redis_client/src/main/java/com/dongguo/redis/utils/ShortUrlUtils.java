package com.dongguo.redis.utils;

import cn.hutool.crypto.digest.DigestUtil;

public class ShortUrlUtils {

    public static void main(String[] args) {
        String sLongUrl = "https://www.bilibili.com/video/BV1Be411R7zM?p=64&vd_source=8bae88bb736eed7ead3addb46e4849f3"; // 长链接
        String[] aResult = shortUrl(sLongUrl);
        // 打印出结果
        for (int i = 0; i < aResult.length; i++) {
            //4个短连接，任意一个都可以使用
            System.out.println("[" + i + "]:::" + aResult[i]);
        }
    }

    public static String[] shortUrl(String url) {

        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"
        };

        String sMD5EncryptResult = DigestUtil.md5Hex(url);       // 对传入网址进行 MD5 加密，key是加密字符串
        String hex = sMD5EncryptResult;

        String[] resUrl = new String[4];
        for (int i = 0; i < 4; i++) {
            // 把加密字符按照8位一组16进制与0x3FFFFFFF进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);

            // 这里需要使用 long 型来转换，因为 Inteter.parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            String outChars = "";
            for (int j = 0; j < 6; j++) {
                long index = 0x0000003D & lHexLong;     // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                outChars += chars[(int) index];         // 把取得的字符相加
                lHexLong = lHexLong >> 5;             // 每次循环按位右移 5 位
            }
            resUrl[i] = outChars;                       // 把字符串存入对应索引的输出数组
        }
        return resUrl;
    }
}
