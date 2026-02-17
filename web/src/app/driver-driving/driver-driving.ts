import { HttpClient } from '@angular/common/http';
import {ChangeDetectorRef, Component, inject, Input} from '@angular/core';
import { LocationDTO } from '../models/ride-estimate.model';
import { MapComponent,MapPin,RouteSummary } from '../components/map/map.component';
import { RideDTO } from '../components/during-ride/during-ride.component';
import { Router } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {PanicService} from '../services/panic.service';
import {
  bootstrapChatDots,
  bootstrapExclamationCircleFill,
  bootstrapFeather,
  bootstrapStar, bootstrapStarFill, bootstrapX
} from '@ng-icons/bootstrap-icons';
import {DatePipe, DecimalPipe} from '@angular/common';


interface StopRideResponse {
  message: string;
  rideId: string;
  endedAt: string;
  newDestination: {
    latitude: number;
    longitude: number;
    address: string;
  };
  price: number;
}

@Component({
  selector: 'app-driver-driving',
  imports: [
    MapComponent,
    NgIcon,
    DecimalPipe,
    DatePipe
  ],
  templateUrl: './driver-driving.html',
  styleUrl: './driver-driving.css',
  viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX })]

})



class DriverDriving {
  @Input() rideId!: string;
  private baseUrl = 'http://localhost:8081/api';
  startLocation?: LocationDTO
  endLocation?: LocationDTO
  locations: MapPin[] = [];
  estimateMinutes: number | null = null;

  stopConfirmOpen = false;
  stopSubmitting = false;
  stopError: string | null = null;
  stopSummaryOpen = false;
  stopResult?: StopRideResponse;

  private panicApi = inject(PanicService);

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  // rost for messages
  toastVisible = false;
  toastMessage = '';

  ngOnInit(): void {
    if (!this.rideId) {
      console.error('DriverDriving: rideId is missing');
    }
    this.http.get<RideDTO>(`${this.baseUrl}/rides/${this.rideId}`).subscribe({
      next: (data) => {

        this.startLocation = data.startLocation
        this.endLocation = data.endLocation
        this.buildPins();
      this.cdr.markForCheck();
      },
    })
  }
  private buildPins(): void {
    const pins: MapPin[]=[];
    if (this.startLocation) {
      pins.push({
        lat: this.startLocation.latitude,
        lng: this.startLocation.longitude,
        popup: 'Start',
        snapToRoad: true,
      });
    }

    if (this.endLocation) {
      pins.push({
        lat: this.endLocation.latitude,
        lng: this.endLocation.longitude,
        popup: 'End',
        snapToRoad: true,
      });
    }

    this.locations = pins;
  }
  onRouteSummary(summary: RouteSummary): void {
    this.estimateMinutes = summary.timeMin;
  }
  openStopConfirm(): void {
    this.stopError = null;
    this.stopConfirmOpen = true;
  }
  closeStopConfirm(): void {
    if (this.stopSubmitting) return;
    this.stopConfirmOpen = false;
  }

  stopRideNow(): void {

    if (!this.rideId || !this.endLocation) {
      console.error('rideId or endLocation is missing');
      return;
    }
    this.stopSubmitting = true;
    this.stopError = null;

    const body = {
      latitude: this.endLocation.latitude,
      longitude: this.endLocation.longitude,
      address: this.endLocation.address,
      note: 'Stopped by driver'
    };

    this.http.post<StopRideResponse>(`${this.baseUrl}/rides/${this.rideId}/stop`,
      body).subscribe({
      next: (res) => {
        this.stopSubmitting = false;
        this.stopConfirmOpen = false;
        this.stopResult = res;

        this.stopSummaryOpen = true;
        this.cdr.markForCheck();
        //this.router.navigate(['/driver-ride-history']);
        //window.location.reload()
        this.cdr.markForCheck();
      },
      error: err => {
        console.error('Failed to stop ride', err);
        const backendMsg =
          err?.error?.message ||
          err?.error?.error ||
          err?.error ||
          'Failed to stop ride.';
        this.stopError = backendMsg;
      },

    });

  }
  goBackAfterStop() {
    this.stopSummaryOpen = false;
    // выбери, куда везём водителя:
    this.router.navigate(['/driver-ride-history'])
      .catch(() => window.location.reload());
  }

  async panicClick() {
    //const rideId = this.ride?.rideId;
    if (!this.rideId) return;

    const res = prompt('Describe the problem (optional):');

    if (res === null) return;

    const msg = res.trim();
    this.panicApi.panic(String(this.rideId), msg).subscribe({
      next: () => alert('Panic sent to administrators.'),
      error: (e) => alert(e?.error?.message ?? 'Panic failed')
    });
  }

  chatClick(): void {}


}

export default DriverDriving
