export interface IUserProfile {
  id?: number;
  userName?: string;
  password?: string;
  email?: string | null;
}

export const defaultValue: Readonly<IUserProfile> = {};
