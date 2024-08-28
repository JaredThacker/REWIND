import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './comments.reducer';

export const Comments = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const commentsList = useAppSelector(state => state.comments.entities);
  const loading = useAppSelector(state => state.comments.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="comments-heading" data-cy="CommentsHeading">
        <Translate contentKey="jhipsterApp.comments.home.title">Comments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterApp.comments.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/comments/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterApp.comments.home.createLabel">Create new Comments</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {commentsList && commentsList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="jhipsterApp.comments.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('commentBody')}>
                  <Translate contentKey="jhipsterApp.comments.commentBody">Comment Body</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('commentBody')} />
                </th>
                <th className="hand" onClick={sort('authorUserName')}>
                  <Translate contentKey="jhipsterApp.comments.authorUserName">Author User Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('authorUserName')} />
                </th>
                <th className="hand" onClick={sort('likes')}>
                  <Translate contentKey="jhipsterApp.comments.likes">Likes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('likes')} />
                </th>
                <th className="hand" onClick={sort('dislikes')}>
                  <Translate contentKey="jhipsterApp.comments.dislikes">Dislikes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dislikes')} />
                </th>
                <th>
                  <Translate contentKey="jhipsterApp.comments.video">Video</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {commentsList.map((comments, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/comments/${comments.id}`} color="link" size="sm">
                      {comments.id}
                    </Button>
                  </td>
                  <td>{comments.commentBody}</td>
                  <td>{comments.authorUserName}</td>
                  <td>{comments.likes}</td>
                  <td>{comments.dislikes}</td>
                  <td>{comments.video ? <Link to={`/video/${comments.video.id}`}>{comments.video.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/comments/${comments.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/comments/${comments.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/comments/${comments.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="jhipsterApp.comments.home.notFound">No Comments found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Comments;
