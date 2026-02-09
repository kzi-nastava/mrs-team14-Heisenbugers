import {ChangeDetectorRef, Component, Input} from '@angular/core';
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

  disabledDecision = false;
  decisionMessage: string | null = null;

  constructor(private http: HttpClient, private cd: ChangeDetectorRef) {
  }

  approve() {
    this.disabledDecision = true;
    this.http.post(
      `http://localhost:8081/api/driver-requests/${this.driverRequest.id}/approve`,
      {}
    ).subscribe({
      next: () => {
        if (this.driverRequest) {
          this.driverRequest.approved = true;
        }
        this.decisionMessage = 'Request successfully approved';
        this.cd.detectChanges();
      },
      error: () => {
        this.decisionMessage = 'Failed to approve request';
        this.disabledDecision = false;
        this.cd.detectChanges();
      }
    });
  }

  reject() {
    this.disabledDecision = true;
    this.http.post(
      `http://localhost:8081/api/driver-requests/${this.driverRequest.id}/reject`,
      {}
    ).subscribe({
      next: () => {
        if (this.driverRequest) {
          this.driverRequest.approved = false;
        }
        this.decisionMessage = 'Request successfully rejected';
        this.cd.detectChanges();
      },
      error: () =>
      {
        this.decisionMessage = 'Failed to reject request';
        this.disabledDecision = false;
        this.cd.detectChanges();
      }
  });
  }
}
