import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RideEstimateRequestDTO, RideEstimateResponseDTO } from '../models/ride-estimate.model';

@Injectable({ providedIn: 'root' })
export class RideEstimateService {
  private http = inject(HttpClient);

  private readonly url = 'http://localhost:8081/api/public/ride-estimates';

  estimate(body: RideEstimateRequestDTO) {
    return this.http.post<RideEstimateResponseDTO>(this.url, body);
  }
}
