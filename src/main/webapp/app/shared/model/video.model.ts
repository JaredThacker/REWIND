import { IUserProfile } from 'app/shared/model/user-profile.model';

export interface IVideo {
  id?: number;
  title?: string | null;
  authorUserId?: number | null;
  videoComments?: string | null;
  likes?: number | null;
  dislikes?: number | null;
  userProfile?: IUserProfile | null;
}

export const defaultValue: Readonly<IVideo> = {};
