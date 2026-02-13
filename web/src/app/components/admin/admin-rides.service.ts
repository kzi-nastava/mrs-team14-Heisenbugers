import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminRideDetailsDTO, AdminRideListItemDTO, RideStatus } from './admin-rides.model';

export interface AdminRideSearchParams {
  driverId?: string;
  passengerId?: string;
  status?: RideStatus;
  from?: string;
  to?: string;
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class AdminRidesService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/api/admin/rides';

  search(params: AdminRideSearchParams): Observable<AdminRideListItemDTO[]> {
    let httpParams = new HttpParams();
    if (params.driverId) httpParams = httpParams.set('driverId', params.driverId);
    if (params.passengerId) httpParams = httpParams.set('passengerId', params.passengerId);
    if (params.status) httpParams = httpParams.set('status', params.status);
    if (params.from) httpParams = httpParams.set('from', params.from);
    if (params.to) httpParams = httpParams.set('to', params.to);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<AdminRideListItemDTO[]>(this.base, { params: httpParams });
  }

  getDetails(rideId: string): Observable<AdminRideDetailsDTO> {
    return this.http.get<AdminRideDetailsDTO>(`${this.base}/${rideId}`);
  }
}
