package com.ecs.numbasst;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest2 {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    public static String numberByteToStr(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte num :data){
            sb.append(String.valueOf(num));
        }
        return sb.reverse().toString();
    }


    @Test
    public void test (){


        //55 10 01 00 1D
        byte[] data = {0x01,0x02,0x03,0x04,0x00};
        //55 11 01 00 5B
        byte[] data0 = {0x55,0x11,0x01,0x00,0X5B};

        //55 01 05 01 58 02 58 02 93
        byte[] data1 = {0x55,0x01,0x05,0x01,0x58,0x02,0x58,0x02,(byte)0x93};

        //55 01 08 02 22 1D 48 00 62 4A 00 9D
        byte[] data2 = {0x55,0x01,0x08,0x02,0x22,0x1D,0x48,0x00,0x62,0x4A,0x00,(byte)0x9D};


        System.out.println(numberByteToStr(data));



    }

}