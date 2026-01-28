import {ChangeDetectorRef, Component, ViewChild, inject, Input} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {NgIcon, provideIcons} from '@ng-icons/core';
import { bootstrapGeo } from '@ng-icons/bootstrap-icons';
import * as L from 'leaflet';
import type { RouteSummary } from '../map/map.component'

import { MapComponent, MapPin, carAvailableIcon, carOccupiedIcon } from '../map/map.component';
import { RideEstimateService } from '../../services/ride-estimate.service';
import { RideEstimateRequestDTO, RideEstimateResponseDTO, VehicleType } from '../../models/ride-estimate.model';
import { HttpClient } from '@angular/common/http';
import {CurrencyPipe, DecimalPipe} from '@angular/common';
import {AuthService} from '../auth/auth.service';
import {RideBookingComponent} from '../ride-booking/ride-booking.component';
import {StartRideComponent} from '../start-ride/start-ride.component';

type FormKeys = 'startAddress' | 'destinationAddress';

interface VehicleLocationDTO {
  id: string,
  model: string,
  licensePlate: string,
  latitude: number,
  longitude: number,
  occupied: boolean,
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MapComponent, ReactiveFormsModule, NgIcon, CurrencyPipe, DecimalPipe, RideBookingComponent, StartRideComponent],
  templateUrl: './home.html',
  viewProviders: [provideIcons({ bootstrapGeo })]
})


export class HomeComponent {
  @ViewChild(MapComponent) mapCmp!: MapComponent;

  private fb = inject(FormBuilder);
  private estimateApi = inject(RideEstimateService);

  route = history.state.favoriteRoute;

  mockPins: MapPin[] = [
    { lat: 45.2396, lng: 19.8227, popup: 'This car is available', iconUrl: carAvailableIcon, snapToRoad: true },
    { lat: 45.241,  lng: 19.823,  popup: 'This car is occupied',  iconUrl: carOccupiedIcon,  snapToRoad: true },
  ];

  pins: MapPin[] = [];
  vehicles?: VehicleLocationDTO[];

  estimateOpen = false;
  loading = false;
  errorMsg: string | null = null;
  submitAttempted = false;
  @Input() rideId!: string;

  estimateResult: RideEstimateResponseDTO | null = null;
  routeSummary: RouteSummary | null = null;

  form = this.fb.group({
    startAddress: ['', [Validators.required, Validators.minLength(3)]],
    destinationAddress: ['', [Validators.required, Validators.minLength(3)]],
    vehicleType: ['STANDARD' as VehicleType],
    babyTransport: [false],
    petTransport: [false],
  });
  baseUrl = 'http://localhost:8081/api'

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, protected authService: AuthService) {

  }

  get f() { return this.form.controls; }

  onPinsChange(pins: MapPin[]) {
    this.pins = pins;
  }

  onRouteSummary(summary: RouteSummary) {
    // store the route summary so child components (ride-booking) can consume it
    this.routeSummary = summary;
    // update view
    try { this.cdr.markForCheck(); } catch (e) { /* ignore */ }
    console.log('Distance:', summary.distanceKm);
    console.log('Time:', summary.timeMin);
  }

  openEstimate() { this.estimateOpen = true; this.errorMsg = null; }
  closeEstimate() { this.estimateOpen = false; }

  isInvalid(name: FormKeys): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }

  ngOnInit(){
    this.http.get<VehicleLocationDTO[]>(`${this.baseUrl}/public/vehicles`).subscribe({
      next: (data) => {

        this.vehicles = data
        this.pins = data.map((dto) => ({
          lat: dto.latitude,
          lng: dto.longitude,
          iconUrl: dto.occupied? carOccupiedIcon : carAvailableIcon,
          snapToRoad: true,
          popup: `<div id="overlay" class="fixed inset-0 bg-black/40 flex items-end sm:items-center justify-center z-50 w-60">
    <div
      id="modal"
      class="w-full sm:w-96 bg-white rounded-t-2xl sm:rounded-2xl p-5 shadow-xl animate-slide-up"
    >
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-lg font-semibold">Vehicle details</h2>
      </div>

      <!-- Content -->
      <div class="space-y-3">
        <div class="flex justify-between">
          <span class="text-gray-500">Model:</span>
          <span class="font-medium">${dto.model}</span>
        </div>

        <div class="flex justify-between">
          <span class="text-gray-500">License plate:</span>
          <span class="font-mono font-semibold tracking-wide">${dto.licensePlate}</span>
        </div>

        <div class="flex justify-between">
          <span class="text-gray-500">Availability:</span>
          <span class="font-medium">${dto.occupied? 'Occupied' : 'Available'}</span>
        </div>
      </div>
    </div>
  </div>
  <style>
    @keyframes slide-up {
      from {
        transform: translateY(20%);
        opacity: 0;
      }
      to {
        transform: translateY(0);
        opacity: 1;
      }
    }
    .animate-slide-up {
      animation: slide-up 300ms ease-out;
    }
  </style>`,
        }))

        this.cdr.markForCheck();
      },
      error: (error) => {
        console.warn('Using mock data due to error fetching public vehicle info:', error);
        this.pins = this.mockPins;
        this.cdr.markForCheck();
      }
    });

    if(this.route != null){
      this.openEstimate();
    }
  }


  async submitEstimate() {
    this.submitAttempted = true;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMsg = null;
    this.estimateResult = null;

    try {
      const startText = String(this.form.value.startAddress);
      const destText  = String(this.form.value.destinationAddress);


      const start = await this.geocode(startText);
      const destination = await this.geocode(destText);

      const req: RideEstimateRequestDTO = {
        start: { latitude: start.lat, longitude: start.lng, address: start.address },
        destination: { latitude: destination.lat, longitude: destination.lng, address: destination.address },
        stops: [],
        vehicleType: this.form.value.vehicleType ?? 'STANDARD',
        babyTransport: !!this.form.value.babyTransport,
        petTransport: !!this.form.value.petTransport,
      };

      const resp = await this.estimateApi.estimate(req).toPromise();
      if (!resp) throw new Error('Empty response');

      this.estimateResult = resp;


      const pts = (resp.routePoints ?? [])
        .filter(p => p.latitude != null && p.longitude != null)
        .map(p => L.latLng(p.latitude as number, p.longitude as number));

      this.mapCmp.drawRoute(pts);

    } catch (e: any) {
      this.errorMsg = typeof e === 'string' ? e : (e?.message ?? 'Estimate failed');
    } finally {
      this.loading = false;
    }
  }


  private geoCache = new Map<string, { lat: number; lng: number; address: string }>();

  private async geocode(q: string): Promise<{ address: string; lat: number; lng: number }> {

    const key = q.trim().toLowerCase();
    const cached = this.geoCache.get(key);
    if (cached) return cached;

    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(q)}&limit=1`;
    const r = await fetch(url, { headers: { 'Accept': 'application/json' } });
    const data = await r.json();
    if (!data?.length) throw 'Address not found';

    const res = { address: q, lat: Number(data[0].lat), lng: Number(data[0].lon) };
    this.geoCache.set(key, res);
    return res;
  }
}
