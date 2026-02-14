
import { TrafficViolation } from '../../models/driver-info.model';

export type RideStatus =
  | 'REQUESTED'
  | 'ASSIGNED'
  | 'ONGOING'
  | 'FINISHED'
  | 'CANCELED';

export interface AdminRideListItemDTO {
  rideId: string;
  status: RideStatus;

  startedAt: string | null;
  endedAt: string | null;

  startAddress: string | null;
  destinationAddress: string | null;

  canceled: boolean;
  canceledBy: string | null;
  price: number;
  panicTriggered: boolean;
}

export interface PassengerInfoDTO {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  profileImageUrl: string;
}

export interface RatingResponseDTO {
  id: string;
  rideId: string;
  driverScore: number;
  vehicleScore: number;
  comment: string;
  createdAt: string;
}

export interface AdminRideDetailsDTO {
  rideId: string;
  status: RideStatus;

  scheduledAt: string | null;
  startedAt: string | null;
  endedAt: string | null;

  price: number;

  start: LocationDTO | null;
  destination: LocationDTO | null;
  stops: LocationDTO[];

  driverId: string | null;
  driverName: string | null;

  passengers: PassengerInfoDTO[];
  trafficViolations: TrafficViolation[];
  rating: RatingResponseDTO | null;


  polyline: LocationDTO[];

  panicTriggered: boolean;
  canceled: boolean;
  canceledByName: string | null;
  cancelReason: string | null;
  canceledAt: string | null;
}

export interface LocationDTO {
  latitude: number;
  longitude: number;
  address: string;
}


export interface TrafficViolationDTO {
  title: string;
  description?: string | null;
}

export interface RatingResponseDTO {
  id: string;
  rideId: string;
  driverScore: number;
  vehicleScore: number;
  comment: string;
  createdAt: string; // ISO
}

export interface AdminRideListItemDTO {
  rideId: string;
  status: RideStatus;
  startedAt: string | null;
  endedAt: string | null;
  startAddress: string | null;
  destinationAddress: string | null;
  canceled: boolean;
  canceledBy: string | null;
  price: number; // BigDecimal -> number
  panicTriggered: boolean;
}
