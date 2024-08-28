import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IVideo } from 'app/shared/model/video.model';
import { getEntities as getVideos } from 'app/entities/video/video.reducer';
import { IComments } from 'app/shared/model/comments.model';
import { getEntity, updateEntity, createEntity, reset } from './comments.reducer';

export const CommentsUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const videos = useAppSelector(state => state.video.entities);
  const commentsEntity = useAppSelector(state => state.comments.entity);
  const loading = useAppSelector(state => state.comments.loading);
  const updating = useAppSelector(state => state.comments.updating);
  const updateSuccess = useAppSelector(state => state.comments.updateSuccess);

  const handleClose = () => {
    navigate('/comments');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getVideos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.likes !== undefined && typeof values.likes !== 'number') {
      values.likes = Number(values.likes);
    }
    if (values.dislikes !== undefined && typeof values.dislikes !== 'number') {
      values.dislikes = Number(values.dislikes);
    }

    const entity = {
      ...commentsEntity,
      ...values,
      video: videos.find(it => it.id.toString() === values.video?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...commentsEntity,
          video: commentsEntity?.video?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterApp.comments.home.createOrEditLabel" data-cy="CommentsCreateUpdateHeading">
            <Translate contentKey="jhipsterApp.comments.home.createOrEditLabel">Create or edit a Comments</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="comments-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterApp.comments.commentBody')}
                id="comments-commentBody"
                name="commentBody"
                data-cy="commentBody"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterApp.comments.authorUserName')}
                id="comments-authorUserName"
                name="authorUserName"
                data-cy="authorUserName"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterApp.comments.likes')}
                id="comments-likes"
                name="likes"
                data-cy="likes"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterApp.comments.dislikes')}
                id="comments-dislikes"
                name="dislikes"
                data-cy="dislikes"
                type="text"
              />
              <ValidatedField
                id="comments-video"
                name="video"
                data-cy="video"
                label={translate('jhipsterApp.comments.video')}
                type="select"
              >
                <option value="" key="0" />
                {videos
                  ? videos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/comments" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CommentsUpdate;
