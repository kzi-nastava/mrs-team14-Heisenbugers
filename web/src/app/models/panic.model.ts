export interface PanicRequestDTO {
  //rideId: string;
  message?: string;
}

export interface PanicEventDTO {
  id: string;
  resolved: boolean;
  createdAt?: string;
  rideId?: string;
  message?: string;
}
