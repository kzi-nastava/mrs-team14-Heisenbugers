export interface GetProfileDTO {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
  profileImageUrl: string | null;
}

export interface UpdateProfileDTO {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
  email: string;
  profileImageUrl: string | null;
  image: string  | null;
}

export interface ChangePasswordDTO {
  oldPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}
