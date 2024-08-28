package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.VideoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Video;
import com.mycompany.myapp.repository.VideoRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VideoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VideoResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_AUTHOR_USER_ID = 1L;
    private static final Long UPDATED_AUTHOR_USER_ID = 2L;

    private static final String DEFAULT_VIDEO_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_COMMENTS = "BBBBBBBBBB";

    private static final Integer DEFAULT_LIKES = 1;
    private static final Integer UPDATED_LIKES = 2;

    private static final Integer DEFAULT_DISLIKES = 1;
    private static final Integer UPDATED_DISLIKES = 2;

    private static final String ENTITY_API_URL = "/api/videos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVideoMockMvc;

    private Video video;

    private Video insertedVideo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Video createEntity(EntityManager em) {
        Video video = new Video()
            .title(DEFAULT_TITLE)
            .authorUserId(DEFAULT_AUTHOR_USER_ID)
            .videoComments(DEFAULT_VIDEO_COMMENTS)
            .likes(DEFAULT_LIKES)
            .dislikes(DEFAULT_DISLIKES);
        return video;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Video createUpdatedEntity(EntityManager em) {
        Video video = new Video()
            .title(UPDATED_TITLE)
            .authorUserId(UPDATED_AUTHOR_USER_ID)
            .videoComments(UPDATED_VIDEO_COMMENTS)
            .likes(UPDATED_LIKES)
            .dislikes(UPDATED_DISLIKES);
        return video;
    }

    @BeforeEach
    public void initTest() {
        video = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedVideo != null) {
            videoRepository.delete(insertedVideo);
            insertedVideo = null;
        }
    }

    @Test
    @Transactional
    void createVideo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Video
        var returnedVideo = om.readValue(
            restVideoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(video)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Video.class
        );

        // Validate the Video in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertVideoUpdatableFieldsEquals(returnedVideo, getPersistedVideo(returnedVideo));

        insertedVideo = returnedVideo;
    }

    @Test
    @Transactional
    void createVideoWithExistingId() throws Exception {
        // Create the Video with an existing ID
        video.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVideoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(video)))
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVideos() throws Exception {
        // Initialize the database
        insertedVideo = videoRepository.saveAndFlush(video);

        // Get all the videoList
        restVideoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(video.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].authorUserId").value(hasItem(DEFAULT_AUTHOR_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].videoComments").value(hasItem(DEFAULT_VIDEO_COMMENTS)))
            .andExpect(jsonPath("$.[*].likes").value(hasItem(DEFAULT_LIKES)))
            .andExpect(jsonPath("$.[*].dislikes").value(hasItem(DEFAULT_DISLIKES)));
    }

    @Test
    @Transactional
    void getVideo() throws Exception {
        // Initialize the database
        insertedVideo = videoRepository.saveAndFlush(video);

        // Get the video
        restVideoMockMvc
            .perform(get(ENTITY_API_URL_ID, video.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(video.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.authorUserId").value(DEFAULT_AUTHOR_USER_ID.intValue()))
            .andExpect(jsonPath("$.videoComments").value(DEFAULT_VIDEO_COMMENTS))
            .andExpect(jsonPath("$.likes").value(DEFAULT_LIKES))
            .andExpect(jsonPath("$.dislikes").value(DEFAULT_DISLIKES));
    }

    @Test
    @Transactional
    void getNonExistingVideo() throws Exception {
        // Get the video
        restVideoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVideo() throws Exception {
        // Initialize the database
        insertedVideo = videoRepository.saveAndFlush(video);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the video
        Video updatedVideo = videoRepository.findById(video.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVideo are not directly saved in db
        em.detach(updatedVideo);
        updatedVideo
            .title(UPDATED_TITLE)
            .authorUserId(UPDATED_AUTHOR_USER_ID)
            .videoComments(UPDATED_VIDEO_COMMENTS)
            .likes(UPDATED_LIKES)
            .dislikes(UPDATED_DISLIKES);

        restVideoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVideo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedVideo))
            )
            .andExpect(status().isOk());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVideoToMatchAllProperties(updatedVideo);
    }

    @Test
    @Transactional
    void putNonExistingVideo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        video.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVideoMockMvc
            .perform(put(ENTITY_API_URL_ID, video.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(video)))
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVideo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        video.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(video))
            )
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVideo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        video.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(video)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVideoWithPatch() throws Exception {
        // Initialize the database
        insertedVideo = videoRepository.saveAndFlush(video);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the video using partial update
        Video partialUpdatedVideo = new Video();
        partialUpdatedVideo.setId(video.getId());

        partialUpdatedVideo.authorUserId(UPDATED_AUTHOR_USER_ID).likes(UPDATED_LIKES).dislikes(UPDATED_DISLIKES);

        restVideoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVideo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVideo))
            )
            .andExpect(status().isOk());

        // Validate the Video in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVideoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVideo, video), getPersistedVideo(video));
    }

    @Test
    @Transactional
    void fullUpdateVideoWithPatch() throws Exception {
        // Initialize the database
        insertedVideo = videoRepository.saveAndFlush(video);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the video using partial update
        Video partialUpdatedVideo = new Video();
        partialUpdatedVideo.setId(video.getId());

        partialUpdatedVideo
            .title(UPDATED_TITLE)
            .authorUserId(UPDATED_AUTHOR_USER_ID)
            .videoComments(UPDATED_VIDEO_COMMENTS)
            .likes(UPDATED_LIKES)
            .dislikes(UPDATED_DISLIKES);

        restVideoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVideo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVideo))
            )
            .andExpect(status().isOk());

        // Validate the Video in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVideoUpdatableFieldsEquals(partialUpdatedVideo, getPersistedVideo(partialUpdatedVideo));
    }

    @Test
    @Transactional
    void patchNonExistingVideo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        video.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVideoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, video.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(video))
            )
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVideo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        video.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(video))
            )
            .andExpect(status().isBadRequest());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVideo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        video.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVideoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(video)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Video in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVideo() throws Exception {
        // Initialize the database
        insertedVideo = videoRepository.saveAndFlush(video);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the video
        restVideoMockMvc
            .perform(delete(ENTITY_API_URL_ID, video.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return videoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Video getPersistedVideo(Video video) {
        return videoRepository.findById(video.getId()).orElseThrow();
    }

    protected void assertPersistedVideoToMatchAllProperties(Video expectedVideo) {
        assertVideoAllPropertiesEquals(expectedVideo, getPersistedVideo(expectedVideo));
    }

    protected void assertPersistedVideoToMatchUpdatableProperties(Video expectedVideo) {
        assertVideoAllUpdatablePropertiesEquals(expectedVideo, getPersistedVideo(expectedVideo));
    }
}
