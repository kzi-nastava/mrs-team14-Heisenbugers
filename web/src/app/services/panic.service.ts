import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PanicRequestDTO } from '../models/panic.model';

@Injectable({ providedIn: 'root' })
export class PanicService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/api';

  //POST /api/rides/{rideId}/panic
  triggerPanic(rideId: string, message: string): Observable<any> {
    return this.http.post(`${this.base}/${rideId}/panic`, { message });
  }

  panic(rideId: string, message?: string): Observable<any> {
    const body: PanicRequestDTO = { rideId, message };
    return this.http.post(`${this.base}/api/rides/{id}/panic`, body);
    }
}
