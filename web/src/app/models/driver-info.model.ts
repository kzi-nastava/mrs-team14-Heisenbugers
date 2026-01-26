import { LatLng } from "leaflet";
import { LocationDTO } from "./ride-estimate.model";

export interface Passenger {
  firstName: string;
  lastName: string,
  passengerId?: string;
  profileImageUrl?: string;
}

export interface TrafficViolation {
  type: string;
  description?: string;
}

export interface RideInfo {
  rideId: string;
  driverName?: string;
  startAddress: string;
  endAddress: string;
  startedAt: Date;
  endedAt: Date;
  startTime: Date;
  endTime: Date;
  price: number;
  rating: number;
  maxRating: number;
  canceled: boolean;
  canceledBy?: string;
  passengers: Passenger[];
  trafficViolations?: TrafficViolation[];
  panicTriggered: boolean;
  rated?: boolean;
  route?: LocationDTO[]
}
