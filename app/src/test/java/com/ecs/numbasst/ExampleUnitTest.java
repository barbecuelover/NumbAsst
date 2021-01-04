package com.ecs.numbasst;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }



    public static byte getCrcSum (byte [] lpbyBuf)
    {
        byte  byLPC =0;
        for (byte b : lpbyBuf) {
            byLPC = (byte) (byLPC + b);
        }
        return byLPC;
    }


    public static boolean checkDataWithCrc8SUM(byte[] data) {
        byte ret = data[data.length-1];
        byte[] content = new byte[data.length-1];
        System.arraycopy(data, 0, content, 0, data.length -1);
        byte retCalc = getCrcSum(content);
        return ret == retCalc;
    }

    public static int byte2Int(byte low,byte high){
        return (low & 0xFF) << 8 | high & 0xFF;
    }



    @Test
    public void test (){
        //20180
        int check = byte2Int(0x10,0x20);
        System.out.println(check);
    }

}