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



    @Test
    public void test (){
        //20180
        byte low = 0x4E;
        byte high =(byte)0xD4;
        int  numb = (low & 0xFF) << 8 | high & 0xFF;

        System.out.println(numb);
        System.out.println(0xD44E);

        System.out.println(0x4ED4);

    }

}