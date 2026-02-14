import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AdminRidesService } from '../admin-rides.service';
import { AdminRideDetailsDTO } from '../admin-rides.model';
import { MapComponent, MapPin } from '../../map/map.component';

@Component({
  selector: 'app-admin-ride-details',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, DecimalPipe, MapComponent],
  templateUrl: './admin-ride-details.component.html',
})
export class AdminRideDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private api = inject(AdminRidesService);

  loading = false;
  error: string | null = null;

  ride: AdminRideDetailsDTO | null = null;

  pins: MapPin[] = [];
  onImgError(e: Event) {
    (e.target as HTMLImageElement).src = 'assets/images/default-avatar.png';
  }

  ngOnInit(): void {
    const rideId = this.route.snapshot.paramMap.get('rideId');
    if (!rideId) {
      this.error = 'rideId is missing.';
      return;
    }

    this.loading = true;
    this.api.getDetails(rideId).subscribe({
      next: (dto) => {
        this.ride = dto;
        this.loading = false;
        this.buildPins();
      },
      error: (e) => {
        this.loading = false;
        this.error = e?.error?.message ?? 'Failed to load ride details.';
      }
    });
  }

  private buildPins(): void {
    if (!this.ride) { this.pins = []; return; }

    const pins: MapPin[] = [];

    if (this.ride.start) {
      pins.push({
        lat: this.ride.start.latitude,
        lng: this.ride.start.longitude,
        popup: 'Start',
        snapToRoad: true,
      });
    }


    (this.ride.stops ?? []).forEach((s, idx) => {
      pins.push({
        lat: s.latitude,
        lng: s.longitude,
        popup: `Stop ${idx + 1}`,
        snapToRoad: true,
      });
    });

    if (this.ride.destination) {
      pins.push({
        lat: this.ride.destination.latitude,
        lng: this.ride.destination.longitude,
        popup: 'End',
        snapToRoad: true,
      });
    }

    this.pins = pins;
  }
}
