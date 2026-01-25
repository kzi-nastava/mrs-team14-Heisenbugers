export interface CreateDriverDTO {
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  address: string;
  profileImageUrl: string | null;
  vehicle: CreateVehicleDTO;
}

export interface CreateVehicleDTO {
  vehicleModel: string;
  vehicleType: string;
  licensePlate: string;
  seatCount: number;
  babyTransport: boolean;
  petTransport: boolean;
}
