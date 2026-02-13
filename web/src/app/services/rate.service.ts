import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class RateService {
    private baseUrl = 'http://localhost:8081/api';

    constructor(private http: HttpClient) {
        
    }

    sendRate(rideId: string, data: { driverScore: number; vehicleScore: number; comment: string; }): Observable<any> {
      return this.http.post(`${this.baseUrl}/rides/${rideId}/rate`, data)
  }
}
