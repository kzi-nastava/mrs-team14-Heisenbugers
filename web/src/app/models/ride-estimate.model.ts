export type VehicleType = 'STANDARD' | 'LUXURY' | 'VAN';

export interface LocationDTO {
  latitude?: number;
  longitude?: number;
  address?: string;
}

export interface RideEstimateRequestDTO {
  start: LocationDTO;
  destination: LocationDTO;
  stops?: LocationDTO[];
  vehicleType?: VehicleType;
  babyTransport: boolean;
  petTransport: boolean;
}

export interface RideEstimateResponseDTO {
  distanceKm: number;
  estimatedTimeMin: number;
  estimatedPrice: string | number;
  polyline?: string;
  routePoints?: LocationDTO[];
}
