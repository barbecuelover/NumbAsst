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
import java.util.ArrayList;
import java.util.List;

public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(8);

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

    public static String bytesToString(byte[] bytes) {
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
        return (low & 0xFF) << 8 | high & 0xFF;
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


    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
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

            RandomAccessFile file = new RandomAccessFile(path,"r");
            file.read(data,index *1024,1024);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
