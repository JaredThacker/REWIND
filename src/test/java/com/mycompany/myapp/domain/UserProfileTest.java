package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.UserProfileTestSamples.*;
import static com.mycompany.myapp.domain.VideoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();
        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void videoTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Video videoBack = getVideoRandomSampleGenerator();

        userProfile.addVideo(videoBack);
        assertThat(userProfile.getVideos()).containsOnly(videoBack);
        assertThat(videoBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.removeVideo(videoBack);
        assertThat(userProfile.getVideos()).doesNotContain(videoBack);
        assertThat(videoBack.getUserProfile()).isNull();

        userProfile.videos(new HashSet<>(Set.of(videoBack)));
        assertThat(userProfile.getVideos()).containsOnly(videoBack);
        assertThat(videoBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.setVideos(new HashSet<>());
        assertThat(userProfile.getVideos()).doesNotContain(videoBack);
        assertThat(videoBack.getUserProfile()).isNull();
    }
}
