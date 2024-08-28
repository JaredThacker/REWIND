package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VideoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Video getVideoSample1() {
        return new Video().id(1L).title("title1").authorUserId(1L).videoComments("videoComments1").likes(1).dislikes(1);
    }

    public static Video getVideoSample2() {
        return new Video().id(2L).title("title2").authorUserId(2L).videoComments("videoComments2").likes(2).dislikes(2);
    }

    public static Video getVideoRandomSampleGenerator() {
        return new Video()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .authorUserId(longCount.incrementAndGet())
            .videoComments(UUID.randomUUID().toString())
            .likes(intCount.incrementAndGet())
            .dislikes(intCount.incrementAndGet());
    }
}
