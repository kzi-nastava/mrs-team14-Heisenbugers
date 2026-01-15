export interface RegisterPassengerRequestDTO {
  email: string;
  password: string;
  confirmPassword: string;

  firstName: string;
  lastName: string;

  phone: string;
  address: string;

  profileImageUrl?: string | null;
}

export interface RegisterResponseDTO {
  userId: string;
  message: string;
}

export interface MessageResponse {
  message: string;
}

export interface LoginRequestDTO {
  email: string;
  password: string;
}

export interface LoginResponseDTO {
  token: string;
  tokenType: string; // "Bearer"
  userId: string;
  role: string;
}
