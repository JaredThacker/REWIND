package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CommentsTestSamples.*;
import static com.mycompany.myapp.domain.VideoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comments.class);
        Comments comments1 = getCommentsSample1();
        Comments comments2 = new Comments();
        assertThat(comments1).isNotEqualTo(comments2);

        comments2.setId(comments1.getId());
        assertThat(comments1).isEqualTo(comments2);

        comments2 = getCommentsSample2();
        assertThat(comments1).isNotEqualTo(comments2);
    }

    @Test
    void videoTest() {
        Comments comments = getCommentsRandomSampleGenerator();
        Video videoBack = getVideoRandomSampleGenerator();

        comments.setVideo(videoBack);
        assertThat(comments.getVideo()).isEqualTo(videoBack);

        comments.video(null);
        assertThat(comments.getVideo()).isNull();
    }
}
