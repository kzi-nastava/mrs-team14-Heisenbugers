import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideAnalyticsResponse } from '../models/ride-analytics.model';

@Injectable({ providedIn: 'root' })
export class RideAnalyticsService {
  private baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getDailyAggregates(start: string, end: string, role?: string, userId?: string, aggregate?: boolean): Observable<RideAnalyticsResponse> {
    let params = new HttpParams().set('start', start).set('end', end);

    if (role) params = params.set('role', role);
    if (userId) params = params.set('userId', userId);
    if (aggregate) params = params.set('aggregate', String(aggregate));

    return this.http.get<RideAnalyticsResponse>(`${this.baseUrl}/analytics/rides`, { params });
  }
}

