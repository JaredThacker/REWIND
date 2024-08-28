package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Video.
 */
@Entity
@Table(name = "video")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author_user_id")
    private Long authorUserId;

    @Column(name = "video_comments")
    private String videoComments;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "dislikes")
    private Integer dislikes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "video")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "video" }, allowSetters = true)
    private Set<Comments> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "videos" }, allowSetters = true)
    private UserProfile userProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Video id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Video title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorUserId() {
        return this.authorUserId;
    }

    public Video authorUserId(Long authorUserId) {
        this.setAuthorUserId(authorUserId);
        return this;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getVideoComments() {
        return this.videoComments;
    }

    public Video videoComments(String videoComments) {
        this.setVideoComments(videoComments);
        return this;
    }

    public void setVideoComments(String videoComments) {
        this.videoComments = videoComments;
    }

    public Integer getLikes() {
        return this.likes;
    }

    public Video likes(Integer likes) {
        this.setLikes(likes);
        return this;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return this.dislikes;
    }

    public Video dislikes(Integer dislikes) {
        this.setDislikes(dislikes);
        return this;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public Set<Comments> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comments> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setVideo(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setVideo(this));
        }
        this.comments = comments;
    }

    public Video comments(Set<Comments> comments) {
        this.setComments(comments);
        return this;
    }

    public Video addComments(Comments comments) {
        this.comments.add(comments);
        comments.setVideo(this);
        return this;
    }

    public Video removeComments(Comments comments) {
        this.comments.remove(comments);
        comments.setVideo(null);
        return this;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Video userProfile(UserProfile userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Video)) {
            return false;
        }
        return getId() != null && getId().equals(((Video) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Video{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", authorUserId=" + getAuthorUserId() +
            ", videoComments='" + getVideoComments() + "'" +
            ", likes=" + getLikes() +
            ", dislikes=" + getDislikes() +
            "}";
    }
}
