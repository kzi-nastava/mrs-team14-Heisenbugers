import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminPanicService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/api/admin';

  getActivePanics(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/panic/active`);
  }

  resolvePanic(panicId: string): Observable<any> {
    return this.http.post(`${this.base}/panic/${panicId}/resolve`, {});
  }

  getUnreadNotifications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/notifications/unread`);
  }

  markNotificationRead(id: string): Observable<void> {
    return this.http.post<void>(`${this.base}/notifications/${id}/read`, {});
  }

  getRideTracking(rideId: string): Observable<any> {
    return this.http.get<any>(`http://localhost:8081/api/rides/${rideId}/tracking`);
  }
}
