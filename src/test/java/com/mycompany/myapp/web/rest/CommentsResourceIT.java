package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CommentsAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Comments;
import com.mycompany.myapp.repository.CommentsRepository;
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
 * Integration tests for the {@link CommentsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommentsResourceIT {

    private static final String DEFAULT_COMMENT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_USER_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_LIKES = 1;
    private static final Integer UPDATED_LIKES = 2;

    private static final Integer DEFAULT_DISLIKES = 1;
    private static final Integer UPDATED_DISLIKES = 2;

    private static final String ENTITY_API_URL = "/api/comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommentsMockMvc;

    private Comments comments;

    private Comments insertedComments;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comments createEntity(EntityManager em) {
        Comments comments = new Comments()
            .commentBody(DEFAULT_COMMENT_BODY)
            .authorUserName(DEFAULT_AUTHOR_USER_NAME)
            .likes(DEFAULT_LIKES)
            .dislikes(DEFAULT_DISLIKES);
        return comments;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comments createUpdatedEntity(EntityManager em) {
        Comments comments = new Comments()
            .commentBody(UPDATED_COMMENT_BODY)
            .authorUserName(UPDATED_AUTHOR_USER_NAME)
            .likes(UPDATED_LIKES)
            .dislikes(UPDATED_DISLIKES);
        return comments;
    }

    @BeforeEach
    public void initTest() {
        comments = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedComments != null) {
            commentsRepository.delete(insertedComments);
            insertedComments = null;
        }
    }

    @Test
    @Transactional
    void createComments() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Comments
        var returnedComments = om.readValue(
            restCommentsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(comments)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Comments.class
        );

        // Validate the Comments in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCommentsUpdatableFieldsEquals(returnedComments, getPersistedComments(returnedComments));

        insertedComments = returnedComments;
    }

    @Test
    @Transactional
    void createCommentsWithExistingId() throws Exception {
        // Create the Comments with an existing ID
        comments.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(comments)))
            .andExpect(status().isBadRequest());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllComments() throws Exception {
        // Initialize the database
        insertedComments = commentsRepository.saveAndFlush(comments);

        // Get all the commentsList
        restCommentsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comments.getId().intValue())))
            .andExpect(jsonPath("$.[*].commentBody").value(hasItem(DEFAULT_COMMENT_BODY)))
            .andExpect(jsonPath("$.[*].authorUserName").value(hasItem(DEFAULT_AUTHOR_USER_NAME)))
            .andExpect(jsonPath("$.[*].likes").value(hasItem(DEFAULT_LIKES)))
            .andExpect(jsonPath("$.[*].dislikes").value(hasItem(DEFAULT_DISLIKES)));
    }

    @Test
    @Transactional
    void getComments() throws Exception {
        // Initialize the database
        insertedComments = commentsRepository.saveAndFlush(comments);

        // Get the comments
        restCommentsMockMvc
            .perform(get(ENTITY_API_URL_ID, comments.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(comments.getId().intValue()))
            .andExpect(jsonPath("$.commentBody").value(DEFAULT_COMMENT_BODY))
            .andExpect(jsonPath("$.authorUserName").value(DEFAULT_AUTHOR_USER_NAME))
            .andExpect(jsonPath("$.likes").value(DEFAULT_LIKES))
            .andExpect(jsonPath("$.dislikes").value(DEFAULT_DISLIKES));
    }

    @Test
    @Transactional
    void getNonExistingComments() throws Exception {
        // Get the comments
        restCommentsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComments() throws Exception {
        // Initialize the database
        insertedComments = commentsRepository.saveAndFlush(comments);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comments
        Comments updatedComments = commentsRepository.findById(comments.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComments are not directly saved in db
        em.detach(updatedComments);
        updatedComments
            .commentBody(UPDATED_COMMENT_BODY)
            .authorUserName(UPDATED_AUTHOR_USER_NAME)
            .likes(UPDATED_LIKES)
            .dislikes(UPDATED_DISLIKES);

        restCommentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedComments.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedComments))
            )
            .andExpect(status().isOk());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommentsToMatchAllProperties(updatedComments);
    }

    @Test
    @Transactional
    void putNonExistingComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comments.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, comments.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(comments))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comments.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(comments))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comments.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(comments)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommentsWithPatch() throws Exception {
        // Initialize the database
        insertedComments = commentsRepository.saveAndFlush(comments);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comments using partial update
        Comments partialUpdatedComments = new Comments();
        partialUpdatedComments.setId(comments.getId());

        partialUpdatedComments.authorUserName(UPDATED_AUTHOR_USER_NAME).dislikes(UPDATED_DISLIKES);

        restCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComments.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComments))
            )
            .andExpect(status().isOk());

        // Validate the Comments in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommentsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedComments, comments), getPersistedComments(comments));
    }

    @Test
    @Transactional
    void fullUpdateCommentsWithPatch() throws Exception {
        // Initialize the database
        insertedComments = commentsRepository.saveAndFlush(comments);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comments using partial update
        Comments partialUpdatedComments = new Comments();
        partialUpdatedComments.setId(comments.getId());

        partialUpdatedComments
            .commentBody(UPDATED_COMMENT_BODY)
            .authorUserName(UPDATED_AUTHOR_USER_NAME)
            .likes(UPDATED_LIKES)
            .dislikes(UPDATED_DISLIKES);

        restCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComments.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComments))
            )
            .andExpect(status().isOk());

        // Validate the Comments in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommentsUpdatableFieldsEquals(partialUpdatedComments, getPersistedComments(partialUpdatedComments));
    }

    @Test
    @Transactional
    void patchNonExistingComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comments.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, comments.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(comments))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comments.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(comments))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComments() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comments.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(comments)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Comments in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComments() throws Exception {
        // Initialize the database
        insertedComments = commentsRepository.saveAndFlush(comments);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the comments
        restCommentsMockMvc
            .perform(delete(ENTITY_API_URL_ID, comments.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return commentsRepository.count();
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

    protected Comments getPersistedComments(Comments comments) {
        return commentsRepository.findById(comments.getId()).orElseThrow();
    }

    protected void assertPersistedCommentsToMatchAllProperties(Comments expectedComments) {
        assertCommentsAllPropertiesEquals(expectedComments, getPersistedComments(expectedComments));
    }

    protected void assertPersistedCommentsToMatchUpdatableProperties(Comments expectedComments) {
        assertCommentsAllUpdatablePropertiesEquals(expectedComments, getPersistedComments(expectedComments));
    }
}
