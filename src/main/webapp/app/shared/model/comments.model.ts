import { IVideo } from 'app/shared/model/video.model';

export interface IComments {
  id?: number;
  commentBody?: string | null;
  authorUserName?: string | null;
  likes?: number | null;
  dislikes?: number | null;
  video?: IVideo | null;
}

export const defaultValue: Readonly<IComments> = {};
