
import { Component, ViewChild, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {NgIcon, provideIcons} from '@ng-icons/core';
import { bootstrapGeo } from '@ng-icons/bootstrap-icons';
import * as L from 'leaflet';
import type { RouteSummary } from '../map/map.component'

import { MapComponent, MapPin, carAvailableIcon, carOccupiedIcon } from '../map/map.component';
import { RideEstimateService } from '../../services/ride-estimate.service';
import { RideEstimateRequestDTO, RideEstimateResponseDTO, VehicleType } from '../../models/ride-estimate.model';
import {CurrencyPipe, DecimalPipe} from '@angular/common';

type FormKeys = 'startAddress' | 'destinationAddress';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MapComponent, ReactiveFormsModule, NgIcon, CurrencyPipe, DecimalPipe],
  templateUrl: './home.html',
  viewProviders: [provideIcons({ bootstrapGeo })]
})


export class HomeComponent {
  @ViewChild(MapComponent) mapCmp!: MapComponent;

  private fb = inject(FormBuilder);
  private estimateApi = inject(RideEstimateService);

  pins: MapPin[] = [
    { lat: 45.2396, lng: 19.8227, popup: 'This car is available', iconUrl: carAvailableIcon, snapToRoad: true },
    { lat: 45.241,  lng: 19.823,  popup: 'This car is occupied',  iconUrl: carOccupiedIcon,  snapToRoad: true },
  ];

  estimateOpen = false;
  loading = false;
  errorMsg: string | null = null;
  submitAttempted = false;

  estimateResult: RideEstimateResponseDTO | null = null;
  routeSummary: RouteSummary | null = null;

  form = this.fb.group({
    startAddress: ['', [Validators.required, Validators.minLength(3)]],
    destinationAddress: ['', [Validators.required, Validators.minLength(3)]],
    vehicleType: ['STANDARD' as VehicleType],
    babyTransport: [false],
    petTransport: [false],
  });

  get f() { return this.form.controls; }

  openEstimate() { this.estimateOpen = true; this.errorMsg = null; }
  closeEstimate() { this.estimateOpen = false; }

  isInvalid(name: FormKeys): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
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
