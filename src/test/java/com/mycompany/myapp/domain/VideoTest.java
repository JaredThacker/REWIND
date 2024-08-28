package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CommentsTestSamples.*;
import static com.mycompany.myapp.domain.UserProfileTestSamples.*;
import static com.mycompany.myapp.domain.VideoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VideoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Video.class);
        Video video1 = getVideoSample1();
        Video video2 = new Video();
        assertThat(video1).isNotEqualTo(video2);

        video2.setId(video1.getId());
        assertThat(video1).isEqualTo(video2);

        video2 = getVideoSample2();
        assertThat(video1).isNotEqualTo(video2);
    }

    @Test
    void commentsTest() {
        Video video = getVideoRandomSampleGenerator();
        Comments commentsBack = getCommentsRandomSampleGenerator();

        video.addComments(commentsBack);
        assertThat(video.getComments()).containsOnly(commentsBack);
        assertThat(commentsBack.getVideo()).isEqualTo(video);

        video.removeComments(commentsBack);
        assertThat(video.getComments()).doesNotContain(commentsBack);
        assertThat(commentsBack.getVideo()).isNull();

        video.comments(new HashSet<>(Set.of(commentsBack)));
        assertThat(video.getComments()).containsOnly(commentsBack);
        assertThat(commentsBack.getVideo()).isEqualTo(video);

        video.setComments(new HashSet<>());
        assertThat(video.getComments()).doesNotContain(commentsBack);
        assertThat(commentsBack.getVideo()).isNull();
    }

    @Test
    void userProfileTest() {
        Video video = getVideoRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        video.setUserProfile(userProfileBack);
        assertThat(video.getUserProfile()).isEqualTo(userProfileBack);

        video.userProfile(null);
        assertThat(video.getUserProfile()).isNull();
    }
}
