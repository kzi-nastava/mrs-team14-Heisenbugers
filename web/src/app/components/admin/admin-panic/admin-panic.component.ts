import { Component, OnDestroy, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminPanicService, RideDTO } from '../../../services/admin-panic.service';
import { MapComponent, MapPin, carSelectedIcon } from '../../map/map.component';
import * as L from 'leaflet';
import { firstValueFrom } from 'rxjs';

//http://localhost:4200/admin/panic
@Component({
  selector: 'app-admin-panic',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './admin-panic.component.html',
})
export class AdminPanicComponent implements OnInit, OnDestroy {
  private api = inject(AdminPanicService);
  @ViewChild(MapComponent) mapCmp!: MapComponent;

  panics: any[] = [];
  notifications: any[] = [];

  selectedRideId: string | null = null;
  selectedPanicId: string | null = null;

  pins: MapPin[] = [];

  soundEnabled = false;
  private lastPanicId: string | null = null;
  private intervalId: any;

  ngOnInit(): void {
    this.loadOnce();
    console.log("123512")
    this.intervalId = setInterval(() => this.poll(), 4000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) clearInterval(this.intervalId);
  }

  enableSound() {
    this.soundEnabled = true;
    const a = new Audio('assets/sounds/panic2.mp3');
    a.play().catch(() => {});
  }

  async loadOnce() {
    await this.poll(true);
  }

  async poll(initial = false) {
    await Promise.all([
      this.loadPanics(initial),
      this.loadNotifications(),
    ]);
  }

  async loadPanics(initial = false) {
    try {
      const panics = await this.api.getActivePanics().toPromise();
      this.panics = panics ?? [];

      const latest = this.panics[0];
      const latestId = latest?.id ?? null;

      if (!initial && latestId && latestId !== this.lastPanicId) {
        this.playSound();
      }

      this.lastPanicId = latestId;
    } catch {
      this.panics = [];
    }
  }

  async loadNotifications() {
    try {
      const notifs = await this.api.getUnreadNotifications().toPromise();
      this.notifications = notifs ?? [];
    } catch {
      this.notifications = [];
    }
  }

  private playSound() {
    if (!this.soundEnabled) return;
    new Audio('assets/sounds/panic.mp3').play().catch(() => {});
  }

  async openRide(p: any) {
    const rideId = p?.ride?.id ?? p?.rideId ?? null;
    const panicId = p?.id ?? null;
    if (!rideId) return;

    this.selectedRideId = rideId;
    this.selectedPanicId = panicId;

    try {
      const ride = await this.api.getAdminRide(rideId).toPromise();


      const start = ride?.startLocation ?? ride?.start ?? null;
      const end   = ride?.destination;
      const stops = Array.isArray(ride?.stops) ? ride.stops : [];



      const pins: MapPin[] = [];

      // Start
      if (typeof start?.latitude === 'number' && typeof start?.longitude === 'number') {
        pins.push({
          lat: start.latitude,
          lng: start.longitude,
          popup: 'Start',
          snapToRoad: true,
        });
      }

      // Stops (если есть)
      if (stops.length > 2) {
        for (const s of stops.slice(1, -1)) {
          if (typeof s?.latitude === 'number' && typeof s?.longitude === 'number') {
            pins.push({
              lat: s.latitude,
              lng: s.longitude,
              popup: 'Stop',
              snapToRoad: true,
            });
          }
        }
      }

      // End
      if (typeof end?.latitude === 'number' && typeof end?.longitude === 'number') {
        pins.push({
          lat: end.latitude,
          lng: end.longitude,
          popup: 'End',
          snapToRoad: true,
        });
      }

      // Panic marker
      const panicLat = p?.vehicleLat;
      const panicLng = p?.vehicleLng;
      if (typeof panicLat === 'number' && typeof panicLng === 'number') {
        pins.push({
          lat: panicLat,
          lng: panicLng,
          popup: p?.message ? `PANIC: ${p.message}` : 'PANIC',
          iconUrl: carSelectedIcon,
          snapToRoad: false,
        });
      }

      this.pins = pins;

    } catch {

      // fallback: только panic marker
      const panicLat = p?.vehicleLat;
      const panicLng = p?.vehicleLng;
      if (typeof panicLat === 'number' && typeof panicLng === 'number') {
        this.pins = [{
          lat: panicLat,
          lng: panicLng,
          popup: p?.message ? `PANIC: ${p.message}` : 'PANIC',
          iconUrl: carSelectedIcon,
          snapToRoad: false,
        }];
      } else {
        this.pins = [];
      }
    }
  }


  async resolvePanic() {
    if (!this.selectedPanicId) return;
    try {
      await firstValueFrom(this.api.resolvePanic(this.selectedPanicId));
      await this.poll(true);
      this.selectedPanicId = null;
      this.selectedRideId = null;
      this.pins = [];
    } catch {}
  }
}
