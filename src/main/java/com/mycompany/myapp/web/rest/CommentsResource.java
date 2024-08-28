package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Comments;
import com.mycompany.myapp.repository.CommentsRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Comments}.
 */
@RestController
@RequestMapping("/api/comments")
@Transactional
public class CommentsResource {

    private static final Logger log = LoggerFactory.getLogger(CommentsResource.class);

    private static final String ENTITY_NAME = "comments";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommentsRepository commentsRepository;

    public CommentsResource(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    /**
     * {@code POST  /comments} : Create a new comments.
     *
     * @param comments the comments to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new comments, or with status {@code 400 (Bad Request)} if the comments has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Comments> createComments(@RequestBody Comments comments) throws URISyntaxException {
        log.debug("REST request to save Comments : {}", comments);
        if (comments.getId() != null) {
            throw new BadRequestAlertException("A new comments cannot already have an ID", ENTITY_NAME, "idexists");
        }
        comments = commentsRepository.save(comments);
        return ResponseEntity.created(new URI("/api/comments/" + comments.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, comments.getId().toString()))
            .body(comments);
    }

    /**
     * {@code PUT  /comments/:id} : Updates an existing comments.
     *
     * @param id the id of the comments to save.
     * @param comments the comments to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated comments,
     * or with status {@code 400 (Bad Request)} if the comments is not valid,
     * or with status {@code 500 (Internal Server Error)} if the comments couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comments> updateComments(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Comments comments
    ) throws URISyntaxException {
        log.debug("REST request to update Comments : {}, {}", id, comments);
        if (comments.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, comments.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        comments = commentsRepository.save(comments);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, comments.getId().toString()))
            .body(comments);
    }

    /**
     * {@code PATCH  /comments/:id} : Partial updates given fields of an existing comments, field will ignore if it is null
     *
     * @param id the id of the comments to save.
     * @param comments the comments to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated comments,
     * or with status {@code 400 (Bad Request)} if the comments is not valid,
     * or with status {@code 404 (Not Found)} if the comments is not found,
     * or with status {@code 500 (Internal Server Error)} if the comments couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Comments> partialUpdateComments(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Comments comments
    ) throws URISyntaxException {
        log.debug("REST request to partial update Comments partially : {}, {}", id, comments);
        if (comments.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, comments.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Comments> result = commentsRepository
            .findById(comments.getId())
            .map(existingComments -> {
                if (comments.getCommentBody() != null) {
                    existingComments.setCommentBody(comments.getCommentBody());
                }
                if (comments.getAuthorUserName() != null) {
                    existingComments.setAuthorUserName(comments.getAuthorUserName());
                }
                if (comments.getLikes() != null) {
                    existingComments.setLikes(comments.getLikes());
                }
                if (comments.getDislikes() != null) {
                    existingComments.setDislikes(comments.getDislikes());
                }

                return existingComments;
            })
            .map(commentsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, comments.getId().toString())
        );
    }

    /**
     * {@code GET  /comments} : get all the comments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of comments in body.
     */
    @GetMapping("")
    public List<Comments> getAllComments() {
        log.debug("REST request to get all Comments");
        return commentsRepository.findAll();
    }

    /**
     * {@code GET  /comments/:id} : get the "id" comments.
     *
     * @param id the id of the comments to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the comments, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comments> getComments(@PathVariable("id") Long id) {
        log.debug("REST request to get Comments : {}", id);
        Optional<Comments> comments = commentsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(comments);
    }

    /**
     * {@code DELETE  /comments/:id} : delete the "id" comments.
     *
     * @param id the id of the comments to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable("id") Long id) {
        log.debug("REST request to delete Comments : {}", id);
        commentsRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
