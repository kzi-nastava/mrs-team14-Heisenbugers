import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-driver-profile-request',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-profile-request.component.html',
  styleUrls: ['./driver-profile-request.component.css'],
})
export class DriverProfileRequestComponent {
  @Input() driverRequest: any;
  @Input() oldProfile: any;
  @Input() newProfile: any;

  constructor(private http: HttpClient) {
  }

  approve() {
    this.http.post(
      `http://localhost:8081/api/driver-requests/${this.driverRequest.id}/approve`,
      {}
    ).subscribe();
  }

  reject() {
    this.http.post(
      `http://localhost:8081/api/driver-requests/${this.driverRequest.id}/reject`,
      {}
    ).subscribe();
  }
}
