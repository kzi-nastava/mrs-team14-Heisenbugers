import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PanicRequestDTO } from '../models/panic.model';

@Injectable({ providedIn: 'root' })
export class PanicService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/api';

  // POST /api/rides/{rideId}/panic
  panic(rideId: string, message?: string): Observable<any> {
    const body: PanicRequestDTO = {};

    const trimmed = (message ?? '').trim();
    if (trimmed.length > 0) {
      body.message = trimmed;
    }

    // можно отправлять {} — бэк принимает required=false
    return this.http.post(`${this.base}/rides/${rideId}/panic`, body);
  }
}
