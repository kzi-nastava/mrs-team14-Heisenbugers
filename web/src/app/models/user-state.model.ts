
export type UserState =
  | 'READY'
  | 'DRIVING'
  | 'STARTING'
  | 'RIDING'
  | 'LOOKING'
  | 'UNKNOWN';

export interface UserStateDTO {
  state: UserState;
  currentRideId?: string | null;
}
