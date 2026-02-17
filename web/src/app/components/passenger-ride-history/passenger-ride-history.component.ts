import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  bootstrapArrowRight,
  bootstrapClock,
  bootstrapCash,
  bootstrapExclamationCircleFill, bootstrapHeart, bootstrapHeartFill
} from '@ng-icons/bootstrap-icons';

import { RideInfo } from '../../models/driver-info.model';

@Component({
  standalone: true,
  selector: 'app-passenger-ride-history',
  imports: [CommonModule,NgIcon],
  templateUrl: './passenger-ride-history.component.html',
  viewProviders: [provideIcons({
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash,
    bootstrapExclamationCircleFill,
    bootstrapHeart,
    bootstrapHeartFill
  })]
})
export class PassengerRideHistoryComponent {
  private baseUrl = 'http://localhost:8081/api';

  rides: RideInfo[] = [];
  loading = false;

  private endpoint = `${this.baseUrl}/users/history`;

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loading = true;

    this.http.get<RideInfo[]>(this.endpoint).subscribe({
      next: (data) => {
        this.rides = (data ?? []).map(r => ({
          ...r,
          startedAt: new Date((r as any).startedAt),
          endedAt: new Date((r as any).endedAt),
          startTime: new Date((r as any).startedAt),
          endTime: new Date((r as any).endedAt),
        }));
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (e) => {
        console.warn('Passenger history error:', e);
        this.rides = [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  toggleFavorite(ride: RideInfo) {
    const currently = ride.favorite;
    this.cdr.markForCheck();

    if (!currently) {
      if (!confirm('Add this route to favorites?')) return;
      this.loading = true;
      this.http.post(`${this.baseUrl}/favorite-routes/${ride.rideId}`, {}).subscribe({
        next: () => {
          this.loading = false;
          ride.favorite = !currently;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to add favorite', err);
          //this.error = 'Failed to add favorite route';
          this.loading = false;
        }
      });
    } else {
      if (!confirm('Remove this route from favorites?')) return;
      this.loading = true;
      this.http.delete(`${this.baseUrl}/favorite-routes/${ride.rideId}/ride`).subscribe({
        next: () => {
          this.loading = false;
          ride.favorite = !currently;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to remove favorite', err);
          //this.error = 'Failed to remove favorite route';
          this.loading = false;
        }
      });
    }
  }

  goToRide(ride: RideInfo) {
    this.router.navigate(['/passenger-ride-history', ride.rideId], { state: { ride:ride } });
  }
}
