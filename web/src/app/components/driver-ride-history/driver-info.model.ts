export interface Passenger {
  firstName: string;
  lastName: string,
}

export interface TrafficViolation {
  type: string;
  description?: string;
}

export interface RideInfo {
  id: string;
  driverName: string;
  startLocation: string;
  finishLocation: string;
  startTime: Date;
  endTime: Date;
  price: number;
  rating: number;
  maxRating: number;
  cancelled: boolean;
  passengers: Passenger[];
  trafficViolations: TrafficViolation[];
  wasPanic: boolean;
  rated?: boolean;
}