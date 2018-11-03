package com.ridko.sk4.common;

import com.ridko.sk4.protocol.ReaderProtocol;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * 数值转换工具
 * @author smitea
 * @since 2018-10-30
 */
public class HexTools {
    public static void crc16(ReaderProtocol readerProtocol) {
        int crc = crcSource(readerProtocol);
        readerProtocol.setCrc(crc);
    }

    private static int crcSource(ReaderProtocol readerProtocol) {
        int crc = (readerProtocol.getType() & 0xFF) + (readerProtocol.getLen() & 0xFF);
        if(readerProtocol.getData() != null) {
            for (int i = 0; i < readerProtocol.getData().length; i++) {
                crc += readerProtocol.getData()[i] & 0xFF;
            }
        }
        return crc;
    }

    public static int convertByteToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < b.length; i++) {
            value = (value << 8) | b[i];
        }
        return value;
    }

    public static String byteArrayToHexString(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(byteToHex(array[i]));
        }
        return sb.toString();
    }

    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase(Locale.getDefault());
    }

    public static byte[] hexStr2Byte(String hex) {
        ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            bf.put(b);
        }
        return bf.array();
    }
}
