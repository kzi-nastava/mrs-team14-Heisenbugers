import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CancelRideRequestDTO, MessageResponse } from '../models/cancel-ride.model';

@Injectable({ providedIn: 'root' })
export class RideActionsService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/api/rides';

  cancelRide(rideId: string, reason: string) {
    return this.http.post(`${this.base}/${rideId}/cancel`, { reason });
  }

  getMyActiveRide() {
    return this.http.get<any>(`${this.base}/me/active`);
  }
}
