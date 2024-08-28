package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CommentsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Comments getCommentsSample1() {
        return new Comments().id(1L).commentBody("commentBody1").authorUserName("authorUserName1").likes(1).dislikes(1);
    }

    public static Comments getCommentsSample2() {
        return new Comments().id(2L).commentBody("commentBody2").authorUserName("authorUserName2").likes(2).dislikes(2);
    }

    public static Comments getCommentsRandomSampleGenerator() {
        return new Comments()
            .id(longCount.incrementAndGet())
            .commentBody(UUID.randomUUID().toString())
            .authorUserName(UUID.randomUUID().toString())
            .likes(intCount.incrementAndGet())
            .dislikes(intCount.incrementAndGet());
    }
}
