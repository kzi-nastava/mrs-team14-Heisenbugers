import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CancelRideRequestDTO, MessageResponse } from '../models/cancel-ride.model';

@Injectable({ providedIn: 'root' })
export class RideActionsService {
  private http = inject(HttpClient);

  cancelRide(rideId: string, body: CancelRideRequestDTO): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`http://localhost:8081/api/rides/${rideId}/cancel`, body);
  }
}
