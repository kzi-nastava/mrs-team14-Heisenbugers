import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AdminUserListItemDTO } from '../admin-users.model';
import { AdminRidesService } from '../admin-rides.service';
import { AdminRideListItemDTO } from '..//admin-rides.model';
import { AdminRideDetailsModalComponent } from '../admin-ride-details/admin-ride-details-modal.component';

@Component({
  selector: 'app-admin-user-rides-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminRideDetailsModalComponent],
  templateUrl: './admin-user-rides-modal.component.html',
})
export class AdminUserRidesModalComponent {
  private ridesApi = inject(AdminRidesService);

  @Input() open = false;
  @Input() tab: 'PASSENGERS' | 'DRIVERS' = 'PASSENGERS';
  @Input() user: AdminUserListItemDTO | null = null;

  @Output() close = new EventEmitter<void>();


  from: string = '';
  to: string = '';
  sortField: 'startedAt' | 'endedAt' | 'price' | 'status' | 'canceled' | 'panicTriggered' | 'createdAt' = 'startedAt';
  sortDir: 'asc' | 'desc' = 'desc';

  loading = false;
  error: string | null = null;
  rides: AdminRideListItemDTO[] = [];

  // details modal
  detailsOpen = false;
  selectedRideId: string | null = null;

  onBackdrop() {
    this.close.emit();
    this.reset();
  }

  stop(e: MouseEvent) {
    e.stopPropagation();
  }

  reset() {
    this.from = '';
    this.to = '';
    this.sortField = 'startedAt';
    this.sortDir = 'desc';
    this.loading = false;
    this.error = null;
    this.rides = [];
    this.detailsOpen = false;
    this.selectedRideId = null;
  }

  load() {
    if (!this.user) return;

    this.loading = true;
    this.error = null;

    const sort = `${this.sortField},${this.sortDir}`;

    this.ridesApi.search({
      driverId: this.tab === 'DRIVERS' ? this.user.id : undefined,
      passengerId: this.tab === 'PASSENGERS' ? this.user.id : undefined,
      from: this.from?.trim() ? this.from.trim() : undefined,
      to: this.to?.trim() ? this.to.trim() : undefined,
      sort
    }).subscribe({
      next: (data) => {
        this.rides = data ?? [];
        this.loading = false;
      },
      error: (e) => {
        this.loading = false;
        this.error = e?.error?.message ?? 'Failed to load rides';
      }
    });
  }

  openDetails(rideId: string) {
    this.selectedRideId = rideId;
    this.detailsOpen = true;
  }

  closeDetails() {
    this.detailsOpen = false;
    this.selectedRideId = null;
  }


  fmt(dt: string | null | undefined) {
    if (!dt) return '-';
    return dt.replace('T', ' ');
  }
}
