export interface AdminRide {
  ride: {
    rideId: string;
    status: string;
    startedAt: string;
    endedAt: string | null;
    startAddress: string;
    destinationAddress: string;
    canceled: boolean;
    canceledBy: string | null;
    price: number;
    panicTriggered: boolean;
  };
  driver: {
    firstName: string;
    lastName: string;
  };
}