import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './video.reducer';

export const VideoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const videoEntity = useAppSelector(state => state.video.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="videoDetailsHeading">
          <Translate contentKey="jhipsterApp.video.detail.title">Video</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{videoEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="jhipsterApp.video.title">Title</Translate>
            </span>
          </dt>
          <dd>{videoEntity.title}</dd>
          <dt>
            <span id="authorUserId">
              <Translate contentKey="jhipsterApp.video.authorUserId">Author User Id</Translate>
            </span>
          </dt>
          <dd>{videoEntity.authorUserId}</dd>
          <dt>
            <span id="videoComments">
              <Translate contentKey="jhipsterApp.video.videoComments">Video Comments</Translate>
            </span>
          </dt>
          <dd>{videoEntity.videoComments}</dd>
          <dt>
            <span id="likes">
              <Translate contentKey="jhipsterApp.video.likes">Likes</Translate>
            </span>
          </dt>
          <dd>{videoEntity.likes}</dd>
          <dt>
            <span id="dislikes">
              <Translate contentKey="jhipsterApp.video.dislikes">Dislikes</Translate>
            </span>
          </dt>
          <dd>{videoEntity.dislikes}</dd>
          <dt>
            <Translate contentKey="jhipsterApp.video.userProfile">User Profile</Translate>
          </dt>
          <dd>{videoEntity.userProfile ? videoEntity.userProfile.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/video" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/video/${videoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default VideoDetail;
