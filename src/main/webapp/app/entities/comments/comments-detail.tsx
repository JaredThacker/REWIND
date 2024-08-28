import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './comments.reducer';

export const CommentsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const commentsEntity = useAppSelector(state => state.comments.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="commentsDetailsHeading">
          <Translate contentKey="jhipsterApp.comments.detail.title">Comments</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{commentsEntity.id}</dd>
          <dt>
            <span id="commentBody">
              <Translate contentKey="jhipsterApp.comments.commentBody">Comment Body</Translate>
            </span>
          </dt>
          <dd>{commentsEntity.commentBody}</dd>
          <dt>
            <span id="authorUserName">
              <Translate contentKey="jhipsterApp.comments.authorUserName">Author User Name</Translate>
            </span>
          </dt>
          <dd>{commentsEntity.authorUserName}</dd>
          <dt>
            <span id="likes">
              <Translate contentKey="jhipsterApp.comments.likes">Likes</Translate>
            </span>
          </dt>
          <dd>{commentsEntity.likes}</dd>
          <dt>
            <span id="dislikes">
              <Translate contentKey="jhipsterApp.comments.dislikes">Dislikes</Translate>
            </span>
          </dt>
          <dd>{commentsEntity.dislikes}</dd>
          <dt>
            <Translate contentKey="jhipsterApp.comments.video">Video</Translate>
          </dt>
          <dd>{commentsEntity.video ? commentsEntity.video.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/comments" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/comments/${commentsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CommentsDetail;
