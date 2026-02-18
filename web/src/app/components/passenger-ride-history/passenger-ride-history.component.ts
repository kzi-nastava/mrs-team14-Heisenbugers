import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  bootstrapArrowRight,
  bootstrapClock,
  bootstrapCash,
  bootstrapExclamationCircleFill,
  bootstrapFeather,
  bootstrapHeart, bootstrapHeartFill
} from '@ng-icons/bootstrap-icons';

import { RideInfo } from '../../models/driver-info.model';
import { RateModal } from "../rate-modal/rate-modal.component";
import { RateService } from '../../services/rate.service';

@Component({
  standalone: true,
  selector: 'app-passenger-ride-history',
  imports: [CommonModule, NgIcon, RateModal],
  templateUrl: './passenger-ride-history.component.html',
  viewProviders: [provideIcons({
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash,
    bootstrapExclamationCircleFill,
    bootstrapFeather,
    bootstrapHeart,
    bootstrapHeartFill
  })]
})
export class PassengerRideHistoryComponent {

  private baseUrl = 'http://localhost:8081/api';
  private endpoint = `${this.baseUrl}/users/history`;

  rides: RideInfo[] = [];
  loading = false;
  ratingRide: RideInfo | null = null;
  

  toastMessage: string = "";
  toastVisible: boolean = false;
  sort: 'date' | 'price' | 'route' = 'date';

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private rateService: RateService
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
    
  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }

  get sortedRides(): RideInfo[] {
    const list = [...this.rides];

    switch (this.sort) {
      case 'date':
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

  openRateModal(ride: RideInfo) {
    this.ratingRide = ride;
    console.log(this.ratingRide);
  }

  closeRateModal() {
    this.ratingRide = null;
  }
  showToast(message: string, duration: number = 2000) {
    console.log(`Trying to show ${message}`)
    this.toastMessage = message;
    this.toastVisible = true;
    this.cdr.markForCheck();
    setTimeout(() => {this.toastVisible = false; this.cdr.markForCheck()}, duration);
  }

  submitRateForm(data: { driverRate: number; vehicleRate: number; comment: string; }) {
    let sendingData = {
        "driverScore": data.driverRate,
        "vehicleScore": data.vehicleRate,
        "comment": data.comment,
      }
      this.rateService.sendRate(this.ratingRide!.rideId, sendingData)
      .subscribe({
      next: () => this.showToast('Rating recorded successfully!'),
      error: (error) => {
        if (error.status === 409){
          this.showToast('You have already rated this ride')
        } else {
          this.showToast('Failed to record rating')
        }
      }
    })
    this.closeRateModal();
    console.log(sendingData)
  }
  
}
