import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminPanicService } from '../../../services/admin-panic.service';
import { MapComponent, MapPin, carSelectedIcon } from '../../map/map.component';



//http://localhost:4200/admin/panic
@Component({
  selector: 'app-admin-panic',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './admin-panic.component.html',
})
export class AdminPanicComponent implements OnInit, OnDestroy {
  private api = inject(AdminPanicService);

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

      if (!initial && latestId && this.lastPanicId && latestId !== this.lastPanicId) {
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
      const tr = await this.api.getRideTracking(rideId).toPromise();
      const lat = tr?.vehicleLatitude;
      const lng = tr?.vehicleLongitude;

      if (typeof lat === 'number' && typeof lng === 'number') {
        this.pins = [{
          lat, lng,
          popup: `PANIC ride: ${rideId}`,
          iconUrl: carSelectedIcon,
          snapToRoad: false,
        }];
      } else {
        this.pins = [];
      }
    } catch {
      this.pins = [];
    }
  }

  async resolvePanic() {
    if (!this.selectedPanicId) return;

    try {
      await this.api.resolvePanic(this.selectedPanicId).toPromise();
      // после resolve обновляем список
      await this.poll(true);
      this.selectedPanicId = null;
      this.selectedRideId = null;
      this.pins = [];
    } catch {
      // можно вывести ошибку в UI, если хочешь
    }
  }
}
