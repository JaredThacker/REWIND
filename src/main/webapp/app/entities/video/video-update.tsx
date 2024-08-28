import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUserProfile } from 'app/shared/model/user-profile.model';
import { getEntities as getUserProfiles } from 'app/entities/user-profile/user-profile.reducer';
import { IVideo } from 'app/shared/model/video.model';
import { getEntity, updateEntity, createEntity, reset } from './video.reducer';

export const VideoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const userProfiles = useAppSelector(state => state.userProfile.entities);
  const videoEntity = useAppSelector(state => state.video.entity);
  const loading = useAppSelector(state => state.video.loading);
  const updating = useAppSelector(state => state.video.updating);
  const updateSuccess = useAppSelector(state => state.video.updateSuccess);

  const handleClose = () => {
    navigate('/video');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUserProfiles({}));
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
    if (values.authorUserId !== undefined && typeof values.authorUserId !== 'number') {
      values.authorUserId = Number(values.authorUserId);
    }
    if (values.likes !== undefined && typeof values.likes !== 'number') {
      values.likes = Number(values.likes);
    }
    if (values.dislikes !== undefined && typeof values.dislikes !== 'number') {
      values.dislikes = Number(values.dislikes);
    }

    const entity = {
      ...videoEntity,
      ...values,
      userProfile: userProfiles.find(it => it.id.toString() === values.userProfile?.toString()),
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
          ...videoEntity,
          userProfile: videoEntity?.userProfile?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterApp.video.home.createOrEditLabel" data-cy="VideoCreateUpdateHeading">
            <Translate contentKey="jhipsterApp.video.home.createOrEditLabel">Create or edit a Video</Translate>
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
                  id="video-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('jhipsterApp.video.title')} id="video-title" name="title" data-cy="title" type="text" />
              <ValidatedField
                label={translate('jhipsterApp.video.authorUserId')}
                id="video-authorUserId"
                name="authorUserId"
                data-cy="authorUserId"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterApp.video.videoComments')}
                id="video-videoComments"
                name="videoComments"
                data-cy="videoComments"
                type="text"
              />
              <ValidatedField label={translate('jhipsterApp.video.likes')} id="video-likes" name="likes" data-cy="likes" type="text" />
              <ValidatedField
                label={translate('jhipsterApp.video.dislikes')}
                id="video-dislikes"
                name="dislikes"
                data-cy="dislikes"
                type="text"
              />
              <ValidatedField
                id="video-userProfile"
                name="userProfile"
                data-cy="userProfile"
                label={translate('jhipsterApp.video.userProfile')}
                type="select"
              >
                <option value="" key="0" />
                {userProfiles
                  ? userProfiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/video" replace color="info">
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

export default VideoUpdate;
