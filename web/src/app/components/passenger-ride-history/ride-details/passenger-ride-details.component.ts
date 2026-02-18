import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  bootstrapStarFill,
  bootstrapStarHalf,
  bootstrapStar,
  bootstrapPersonCircle,
  bootstrapClock,
  bootstrapCash,
  bootstrapArrowRight,
  bootstrapArrowLeft,
  bootstrapExclamationCircleFill
} from '@ng-icons/bootstrap-icons';

import { MapComponent } from '../../map/map.component';
import { LatLng } from 'leaflet';


type RideHistoryDTO = {
  rideId: string;
  startedAt: string | null;
  endedAt: string | null;
  startAddress: string;
  endAddress: string;
  canceled: boolean;
  canceledBy: string | null;
  price: number;
  panicTriggered: boolean;
};


type PassengerRideDetailsVM = {
  rideId: string;

  startedAt: Date | null;
  endedAt: Date | null;

  startAddress: string;
  endAddress: string;

  price: number;
  canceled: boolean;
  panicTriggered: boolean;


  driverName?: string;
  passengers?: Array<{ firstName: string; lastName: string }>;
  trafficViolations?: Array<{ title: string }>;
  rating?: number;
  maxRating?: number;


  route?: Array<{ latitude: number; longitude: number }>;
};

@Component({
  standalone: true,
  selector: 'app-passenger-ride-details',
  imports: [CommonModule, NgIcon, MapComponent],
  templateUrl: './passenger-ride-details.component.html',
  viewProviders: [provideIcons({
    bootstrapStarFill,
    bootstrapStarHalf,
    bootstrapStar,
    bootstrapPersonCircle,
    bootstrapClock,
    bootstrapCash,
    bootstrapArrowRight,
    bootstrapArrowLeft,
    bootstrapExclamationCircleFill
  })]
})
export class PassengerRideDetailsComponent {
  private baseUrl = 'http://localhost:8081/api';

  ride: PassengerRideDetailsVM | null = null;
  loading = true;

  private rideId: string | null = null;

  @ViewChild(MapComponent) mapCmp!: MapComponent;

  constructor(
    private http: HttpClient,
    private ar: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.rideId = this.ar.snapshot.paramMap.get('rideId');
    if (!this.rideId) {
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }


    const stateRide = (history.state && (history.state as any).ride) as RideHistoryDTO | undefined;
    if (stateRide && stateRide.rideId === this.rideId) {
      this.ride = this.fromHistoryDto(stateRide);
      this.loading = false;
      this.cdr.markForCheck();
      setTimeout(() => this.tryShowRoute(), 0);
    }


    this.http.get<any>(`${this.baseUrl}/rides/${this.rideId}`).subscribe({
      next: (data) => {
        this.ride = this.mergeWithBackendDetails(this.rideId!, this.ride, data);
        this.loading = false;
        this.cdr.markForCheck();
        setTimeout(() => this.tryShowRoute(), 0);
      },
      error: () => {

        if (this.ride) return; // уже есть из state
        this.http.get<RideHistoryDTO[]>(`${this.baseUrl}/users/history`).subscribe({
          next: (list) => {
            const found = (list ?? []).find(x => x.rideId === this.rideId) ?? null;
            this.ride = found ? this.fromHistoryDto(found) : null;
            this.loading = false;
            this.cdr.markForCheck();
            setTimeout(() => this.tryShowRoute(), 0);
          },
          error: () => {
            this.ride = null;
            this.loading = false;
            this.cdr.markForCheck();
          }
        });
      }
    });
  }

  goBack() {
    this.router.navigate(['/profile']);
  }

  // -------- mapping ----------

  private fromHistoryDto(d: RideHistoryDTO): PassengerRideDetailsVM {
    return {
      rideId: d.rideId,
      startedAt: d.startedAt ? new Date(d.startedAt) : null,
      endedAt: d.endedAt ? new Date(d.endedAt) : null,
      startAddress: d.startAddress ?? '',
      endAddress: d.endAddress ?? '',
      price: Number(d.price ?? 0),
      canceled: !!d.canceled,
      panicTriggered: !!d.panicTriggered
    };
  }

  private mergeWithBackendDetails(
    rideId: string,
    current: PassengerRideDetailsVM | null,
    data: any
  ): PassengerRideDetailsVM {

    const startedAtRaw = data?.startedAt ?? current?.startedAt ?? null;
    const endedAtRaw = data?.endedAt ?? current?.endedAt ?? null;

    const startAddress =
      data?.startAddress ??
      data?.start?.address ??
      current?.startAddress ??
      '';

    const endAddress =
      data?.endAddress ??
      data?.end?.address ??
      current?.endAddress ??
      '';

    // route points:
    // - data.route
    // - or data.stops
    const route = (data?.route ?? data?.stops ?? current?.route ?? null) as any;

    return {
      rideId,
      startedAt: startedAtRaw ? new Date(startedAtRaw) : null,
      endedAt: endedAtRaw ? new Date(endedAtRaw) : null,
      startAddress,
      endAddress,
      price: Number(data?.price ?? current?.price ?? 0),
      canceled: !!(data?.canceled ?? current?.canceled ?? false),
      panicTriggered: !!(data?.panicTriggered ?? current?.panicTriggered ?? false),

      driverName:
        data?.driverName ??
        (data?.driver ? `${data.driver.firstName ?? ''} ${data.driver.lastName ?? ''}`.trim() : undefined) ??
        current?.driverName,

      passengers: data?.passengers ?? current?.passengers ?? [],
      trafficViolations: data?.trafficViolations ?? current?.trafficViolations ?? [],
      rating: data?.rating ?? current?.rating ?? 0,
      maxRating: data?.maxRating ?? current?.maxRating ?? 5,

      route: Array.isArray(route) ? route : current?.route
    };
  }

  // -------- UI helpers ----------

  getFormattedTime(): string {
    if (!this.ride?.startedAt || !this.ride?.endedAt) return '-';
    const s = this.ride.startedAt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const e = this.ride.endedAt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    return `${s} - ${e}`;
  }

  getStars(): { full: number[]; half: boolean; empty: number[] } {
    const rating = this.ride?.rating ?? 0;
    const max = this.ride?.maxRating ?? 5;
    const full = Math.floor(rating);
    const half = rating % 1 >= 0.5;
    const empty = Math.max(0, max - full - (half ? 1 : 0));
    return { full: Array(full).fill(0), half, empty: Array(empty).fill(0) };
  }

  get driverName(): string {
    return this.ride?.driverName ?? '-';
  }

  get passengers(): any[] {
    return this.ride?.passengers ?? [];
  }

  get trafficViolations(): any[] {
    return this.ride?.trafficViolations ?? [];
  }

  get panicTriggered(): boolean {
    return !!this.ride?.panicTriggered;
  }

  // -------- map ----------

  private tryShowRoute() {
    if (!this.ride || !this.mapCmp) return;
    const ptsRaw = this.ride.route;
    if (!ptsRaw || ptsRaw.length < 2) return; // нет точек — карту не трогаем

    const pts = ptsRaw.map(p => new LatLng(p.latitude, p.longitude));
    const start = pts[0];
    const end = pts[pts.length - 1];
    const stops = pts.slice(1, -1);

    this.mapCmp.showRoute(start, end, stops).then(() => this.cdr.markForCheck());
  }

  repeatRide() {}
  scheduleRide() {}
}
