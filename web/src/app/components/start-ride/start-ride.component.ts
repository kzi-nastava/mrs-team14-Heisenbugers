import { Component, OnInit, inject } from '@angular/core'

import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RideActionsService } from '../../services/ride-actions.service';
import { CancelRideModalComponent } from './cancel-ride-modal.component';
import { HttpClient } from '@angular/common/http';


type CancelReason = 'HEALTH_PROBLEM' | 'CUSTOMER_NOT_HERE' | 'CAR_PROBLEM' | 'OTHER';
interface PassengerVm {
  id: string;
  firstName: string;
  lastName: string;
  profileImageUrl?: string | null;
}

interface LocationVm {
  address?: string | null;
}

interface RideVm {
  id: string;
  start?: LocationVm | null;
  end?: LocationVm | null;
  passengers?: PassengerVm[] | null;
}

@Component({
  selector: 'app-start-ride',
  standalone: true,
  imports: [ FormsModule],
  templateUrl: './start-ride.component.html',
  styleUrl: './start-ride.component.css',
})
export class StartRideComponent {

  // TODO: then you substitute the real data from API (ride tracking / createRide etc.)
  ride: RideVm | null = null;
  private rideActions = inject(RideActionsService);

  distanceKm: number | null = null;
  timeMin: number | null = null;

  // modal state
  cancelOpen = false;
  cancelReason: CancelReason = 'HEALTH_PROBLEM';
  cancelReasonText = '';

  // example: to conveniently turn off buttons when prompted
  loading = false;
  errorMsg: string | null = null;

  ngOnInit(): void {
    // Demo data
    this.ride = {
      id: '00000000-0000-0000-0000-000000000000',
      start: { address: 'Bulevar Jovana Ducica 15' },
      end: { address: 'Bulevar Oslobodjenja 42' },
      passengers: [
        { id: 'p1', firstName: 'Name', lastName: 'Surname', profileImageUrl: null },
        { id: 'p2', firstName: 'Name', lastName: 'Surname', profileImageUrl: null },
      ],
    };

    this.distanceKm = 15;
    this.timeMin = 35;
  }
  openCancel() {
    this.cancelOpen = true;
    this.errorMsg = null;

    if (this.cancelReason !== 'OTHER') {
      this.cancelReasonText = '';
    }
  }


  closeCancel(): void {
    this.cancelOpen = false;
  }

  // Start
  async startRide(): Promise<void> {
    if (!this.ride?.id) return;

    this.loading = true;
    this.errorMsg = null;

    try {
      // TODO: here will be a call to the back: PUT /api/rides/{id}/start or /finish/start
      // await this.api.startRide(this.ride.id).toPromise();

      console.log('Start ride:', this.ride.id);
    } catch (e: any) {
      this.errorMsg = e?.message ?? 'Failed to start ride';
    } finally {
      this.loading = false;
    }
  }
  // Stop in modal (confirm cancel)
  async confirmCancel(): Promise<void> {
    if (!this.ride?.id) return;

    const reason =
      this.cancelReason === 'OTHER'
        ? (this.cancelReasonText || '').trim()
        : this.cancelReason;

    if (!reason) {
      this.errorMsg = 'Please provide a reason.';
      return;
    }

    this.loading = true;
    this.errorMsg = null;

    try {
      // TODO: back!! POST /api/rides/{rideId}/cancel { reason: string }
      // await this.api.cancelRide(this.ride.id, { reason }).toPromise();

      console.log('Cancel ride:', this.ride.id, 'reason:', reason);

      // close modal
      this.closeCancel();
    } catch (e: any) {
      this.errorMsg = e?.message ?? 'Failed to cancel ride';
    } finally {
      this.loading = false;
    }
  }

}
