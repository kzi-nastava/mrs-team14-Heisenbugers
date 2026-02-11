export interface Notification {
  id: string;         // UUID as string
  message: string;
  read: boolean;
  createdAt: string;  // ISO string from backend
  readAt?: string | null;
  rideId?: string | null;
}