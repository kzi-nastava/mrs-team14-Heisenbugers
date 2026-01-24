export interface RideRateInfo {
  startAddress: string;
  endAddress: string;
  price: number;
  rated?: boolean;
  startTime: Date;
  endTime: Date;
}
