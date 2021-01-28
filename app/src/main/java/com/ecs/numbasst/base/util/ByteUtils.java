package com.ecs.numbasst.base.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ByteUtils {

    public static byte[] string16ToBytes(String s) {
        byte[] buf = new byte[s.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            try {
                buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }

    /**
     * normal string to 16string
     *
     * @param str
     * @return
     */
    public static String str2Hex16Str(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            //sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 数据固定长度为5字节，依次：个、十、百、千、 万，不足补0；如2345，
     * 发送数据为 0x05 0x04 0x03 0x02 0x00 。
     * @param str
     * @return
     */
    public static byte[] number5ToNumberByte(String str) {
        int number = Integer.parseInt(str);
        byte num1 = (byte) (number % 10);
        byte num2 = (byte) ((number % 100)/10);
        byte num3 = (byte) ((number % 1000)/100);
        byte num4 = (byte) ((number % 10000)/1000);
        byte num5 = (byte) (number/10000);
        byte[] ary = {num1,num2,num3,num4,num5};
        return ary;
    }

    /**
     * 将5位 的byte数组  0x05 0x04 0x03 0x02 0x00 数字转为 字符串 02345
     * @param data
     * @return
     */
    public static String numberByteToStr(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte num :data){
            sb.append(String.valueOf(num));
        }
        return sb.reverse().toString();
    }

    /**
     * 59分 ，表示为 0101 1001
     * 将数字转为 BCD 格式
     * @param b
     * @return
     */
    public static byte convertBCD(int b)
    {
        //高四位
        byte b1 = (byte)(b / 10);
        //低四位
        byte b2 = (byte)(b % 10);
        return (byte)((b1 << 4) | b2);
    }


    //使用1字节就可以表示b
    public static String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进制数
    }

    //需要使用2字节表示b
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }

    //需要使用2字节表示b
    public static String longToHex16(long b) {
        return String.format("%04x", b);
    }

    //需要使用4字节表示b
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }

    //需要使用4字节表示b
    public static String longToHex32(long b) {
        return String.format("%08x", b);
    }

    public static String bytesToString16(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * 将两位 byte 转为 int
     * 低位在前，高位在后
     */
    public static int byte2Int(byte low,byte high){
        return ( high & 0xFF) << 8 | low & 0xFF;
    }

    /**
     * 将byte[] 转为 int
     * 低位在前，高位在后
     */
    public static int byte2Int(byte[] content){
        int temp = 0;
        if (content!=null){
            for (int i=0;i<content.length;i++){
                temp |= content[i] << 8*i;
            }
        }
        return temp;
    }


    /**
     *将int 转为2字节 Byte[]  低位在前，
     */
    public static byte[] intToLow2Byte(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }


    /**
     *将int 转为2字节 Byte[]  低位在前，
     */
    public static byte[] longToLow4Byte(long n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static byte[] longToHigh4Byte(long n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static long bytes4LowToLong(byte[] bytes){
        if (bytes==null || bytes.length!=4){
            return 0;
        }else {
            int v0 = (bytes[3] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
            int v1 = (bytes[2] & 0xff) << 16;
            int v2 = (bytes[1] & 0xff) << 8;
            int v3 = (bytes[0] & 0xff) ;
            return v0 + v1 + v2 + v3;
        }
    }

    /**
     * 大端
     * @param bytes
     * @return
     */
    public static long bytes4HighToLong(byte[] bytes){
        if (bytes==null || bytes.length!=4){
            return 0;
        }else {
            int v0 = (bytes[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
            int v1 = (bytes[1] & 0xff) << 16;
            int v2 = (bytes[2] & 0xff) << 8;
            int v3 = (bytes[3] & 0xff) ;
            return v0 + v1 + v2 + v3;
        }
    }


    public static long bytes2HighToLong(byte[] bytes){
        if (bytes==null || bytes.length!=2){
            return 0;
        }else {
            int v2 = (bytes[0] & 0xff) << 8;
            int v3 = (bytes[1] & 0xff) ;
            return  v2 + v3;
        }
    }


    public static byte[] joinArray(byte[] ary1, byte[] ary2) {
        byte[] data = new byte[ary1.length + ary2.length];
        System.arraycopy(ary1, 0, data, 0, ary1.length);
        System.arraycopy(ary2, 0, data, ary1.length, ary2.length);
        return data;
    }
    //获得指定文件的byte数组
    public static byte[] getFile2Bytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 将 文件 分为多个 byte数字，每个数组长度为15
     * @param filePath
     * @return 返回byte数组集合
     */
    public static List<byte[]> getUpdateDataList(String filePath){
        List<byte[]> list = new ArrayList<>();
        byte[] data = getFile2Bytes(filePath);
        if (data != null) {
            int index = 0;
            do {
                byte[] surplusData = new byte[data.length - index];
                byte[] currentData;
                System.arraycopy(data, index, surplusData, 0, data.length - index);
                if (surplusData.length <= 20) {
                    currentData = new byte[surplusData.length];
                    System.arraycopy(surplusData, 0, currentData, 0, surplusData.length);
                    index += surplusData.length;
                } else {
                    currentData = new byte[20];
                    System.arraycopy(data, index, currentData, 0, 20);
                    index += 20;
                }
                list.add(currentData);
            } while (index < data.length);
        }
        return  list;
    }






    /**
     * 将字节数组转成文件
     * @param filePath
     * @param data
     */
    public static void saveBytesToFile(String filePath, byte[] data) {
        File file = new File(filePath);
        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(new FileOutputStream(file));
            outStream.write(data);
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static byte[] getFileByteFromIndex(String path ,int index){
        byte [] data = new byte[1024];
        try {
            long  offSize = index *1024;
            int length = 1024;
            RandomAccessFile file = new RandomAccessFile(path,"r");
            if( (offSize + length) > file.length() ){
                length = (int)(file.length() - offSize);
            }
            //超出文件长度
            if (length < 0){
                file.close();
                return data;
            }

            file.seek(offSize);

            if( (offSize + length) > file.length() ){
               length = (int)(file.length() - offSize);
            }
            file.read(data,0,length);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
