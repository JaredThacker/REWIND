package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Comments.
 */
@Entity
@Table(name = "comments")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment_body")
    private String commentBody;

    @Column(name = "author_user_name")
    private String authorUserName;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "dislikes")
    private Integer dislikes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "comments", "userProfile" }, allowSetters = true)
    private Video video;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Comments id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommentBody() {
        return this.commentBody;
    }

    public Comments commentBody(String commentBody) {
        this.setCommentBody(commentBody);
        return this;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getAuthorUserName() {
        return this.authorUserName;
    }

    public Comments authorUserName(String authorUserName) {
        this.setAuthorUserName(authorUserName);
        return this;
    }

    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    public Integer getLikes() {
        return this.likes;
    }

    public Comments likes(Integer likes) {
        this.setLikes(likes);
        return this;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return this.dislikes;
    }

    public Comments dislikes(Integer dislikes) {
        this.setDislikes(dislikes);
        return this;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public Video getVideo() {
        return this.video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Comments video(Video video) {
        this.setVideo(video);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comments)) {
            return false;
        }
        return getId() != null && getId().equals(((Comments) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comments{" +
            "id=" + getId() +
            ", commentBody='" + getCommentBody() + "'" +
            ", authorUserName='" + getAuthorUserName() + "'" +
            ", likes=" + getLikes() +
            ", dislikes=" + getDislikes() +
            "}";
    }
}
