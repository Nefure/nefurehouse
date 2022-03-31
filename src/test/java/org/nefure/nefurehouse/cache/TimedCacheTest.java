package org.nefure.nefurehouse.cache;

import org.junit.Test;

/**
 * @author nefure
 * @date 2022/3/30 18:19
 */
public class TimedCacheTest {

    TimedClearCache<Integer, Integer> cache = new TimedClearCache<>(20000000);
    @Test
    public void autoClearTest() throws InterruptedException {
        cache.put(3,4);
        Thread.sleep(1000);
        System.out.println(cache.get(3));
        Thread.sleep(1500);
        System.out.println(cache.get(4));
    }

    @Test
    public void lockTest() throws InterruptedException {
        for (int i = 0; i < 10000; i++ ){
            int finalI = i;
            new Thread(()->{
                cache.put(finalI,finalI);
            }).start();
        }
        Thread.sleep(2000);
        int cnt = 0;
        for (int i = 0; i < 10000; i++){
            System.out.println(i+","+cache.get(i));
            if(null == cache.get(i) || i != cache.get(i))cnt++;
        }
        System.out.println(cnt);
    }
}
