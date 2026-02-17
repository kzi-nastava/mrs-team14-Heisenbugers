export interface RideRateInfo {
  startAddress: string;
  endAddress: string;
  price: number;
  rated?: boolean;
  startTime: Date;
  endTime: Date;
}

export interface LocationDTO {
  latitude: number;
  longitude: number;
  address: string;
}

export interface PriceDTO {
  startingPrice: number;
  vehicleType: string;
}
