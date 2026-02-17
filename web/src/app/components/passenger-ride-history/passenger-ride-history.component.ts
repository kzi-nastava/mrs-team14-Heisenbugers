import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  bootstrapArrowRight,
  bootstrapClock,
  bootstrapCash,
  bootstrapExclamationCircleFill
} from '@ng-icons/bootstrap-icons';

import { RideInfo } from '../../models/driver-info.model';

@Component({
  standalone: true,
  selector: 'app-passenger-ride-history',
  imports: [CommonModule, NgIcon],
  templateUrl: './passenger-ride-history.component.html',
  viewProviders: [provideIcons({
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash,
    bootstrapExclamationCircleFill
  })]
})
export class PassengerRideHistoryComponent {
  private baseUrl = 'http://localhost:8081/api';
  private endpoint = `${this.baseUrl}/users/history`;

  rides: RideInfo[] = [];
  loading = false;

  sort: 'date' | 'price' | 'route' = 'date';

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
        this.cdr.markForCheck();
      },
      error: (e) => {
        console.warn('Passenger history error:', e);
        this.rides = [];
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }

  get sortedRides(): RideInfo[] {
    const list = [...this.rides];

    switch (this.sort) {
      case 'date':
        // последние сверху
        return list.sort((a: any, b: any) =>
          new Date(b.startedAt).getTime() - new Date(a.startedAt).getTime()
        );

      case 'price':
        return list.sort((a: any, b: any) => (b.price ?? 0) - (a.price ?? 0));

      case 'route':
        return list.sort((a: any, b: any) => {
          const ra = `${a.startAddress ?? ''}→${a.endAddress ?? ''}`.toLowerCase();
          const rb = `${b.startAddress ?? ''}→${b.endAddress ?? ''}`.toLowerCase();
          return ra.localeCompare(rb);
        });

      default:
        return list;
    }
  }

  goToRide(ride: RideInfo) {
    this.router.navigate(['/passenger-ride-history', ride.rideId], { state: { ride } });
  }
}
