import video from 'app/entities/video/video.reducer';
import comments from 'app/entities/comments/comments.reducer';
import userProfile from 'app/entities/user-profile/user-profile.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  video,
  comments,
  userProfile,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
