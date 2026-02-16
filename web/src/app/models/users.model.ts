export interface BlockableUserDTO {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  profileImageUrl: string;
  blocked: boolean;
  role: string;
}

export interface IsBlockedDTO {
  blocked: boolean;
  blockNote?: string;
}
