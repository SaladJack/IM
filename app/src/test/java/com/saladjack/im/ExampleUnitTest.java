package com.saladjack.im;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testInterval(){
        System.out.println("start");
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(aLong -> System.out.println("now: " + aLong));
        System.out.println("end");
    }
}