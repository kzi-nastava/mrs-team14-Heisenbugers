import { Component, ChangeDetectorRef, EventEmitter, Input, Output, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRidesService } from '../admin-rides.service';
import { AdminRideDetailsDTO, LocationDTO } from '../admin-rides.model';
import { MapComponent, MapPin } from '../../map/map.component';
import { LatLng } from 'leaflet';

@Component({
  selector: 'app-admin-ride-details-modal',
  standalone: true,
  imports: [CommonModule,MapComponent],
  templateUrl: './admin-ride-details-modal.component.html',
})
export class AdminRideDetailsModalComponent {
  @Input() dto!: any;
  @ViewChild(MapComponent) mapCmp!: MapComponent;
  private ridesApi = inject(AdminRidesService);

  @Input() open = false;
  @Input() rideId: string | null = null;
  @Output() close = new EventEmitter<void>();

  loading = false;
  error: string | null = null;
  //dto: AdminRideDetailsDTO | null = null;

  pins: MapPin[] = [];
  routePoints: LocationDTO[] = [];
  constructor(private cdr: ChangeDetectorRef) {}

  ngOnChanges() {
    if (this.open && this.rideId) {
      this.load();
    }
  }
  renderMapFromDto(): void {
    if (!this.dto) return;


    const start = this.dto.start;
    const end = this.dto.destination;

    const pins: MapPin[] = [];
    if (start?.latitude != null && start?.longitude != null) {
      pins.push({
        lat: start.latitude,
        lng: start.longitude,
        popup: 'Start',
        snapToRoad: true,
      });
    }
    if (end?.latitude != null && end?.longitude != null) {
      pins.push({
        lat: end.latitude,
        lng: end.longitude,
        popup: 'End',
        snapToRoad: true,
      });
    }
    this.pins = pins;

    const route = (this.dto.polyline ?? []) as any[];
    const stops = route
      .filter(p => p?.latitude != null && p?.longitude != null)
      .map(p => new LatLng(p.latitude, p.longitude));

    this.cdr.markForCheck();


    setTimeout(() => {
      if (!this.mapCmp || stops.length < 2) return;

      this.mapCmp
        .showRoute(stops[0], stops[stops.length - 1], stops.slice(1, -1))
        .then(() => this.cdr.markForCheck())
        .catch(() => {});
    });
  }

  private buildMap(dto: AdminRideDetailsDTO) {
    const pins: MapPin[] = [];

    if (dto.start?.latitude != null && dto.start?.longitude != null) {
      pins.push({
        lat: dto.start.latitude,
        lng: dto.start.longitude,
        popup: 'Start',
        snapToRoad: true
      });
    }

    if (dto.destination?.latitude != null && dto.destination?.longitude != null) {
      pins.push({
        lat: dto.destination.latitude,
        lng: dto.destination.longitude,
        popup: 'End',
        snapToRoad: true
      });
    }

    this.pins = pins;


    const pts = (dto.polyline && dto.polyline.length)
      ? dto.polyline
      : (dto.stops && dto.stops.length ? dto.stops : []);

    this.routePoints = pts;
  }

  onBackdrop() {
    this.close.emit();
    this.dto = null;
    this.error = null;
    this.loading = false;
  }

  stop(e: MouseEvent) {
    e.stopPropagation();
  }

  private load() {
    if (!this.rideId) return;

    this.loading = true;
    this.error = null;

    this.ridesApi.getDetails(this.rideId).subscribe({
      next: (d) => {
        this.dto = d;
        this.loading = false;
        this.renderMapFromDto();
      },
      error: (e) => {
        this.loading = false;
        this.error = e?.error?.message ?? 'Failed to load ride details';
      }
    });
  }

  fmt(dt?: string | null) {
    if (!dt) return '-';
    return dt.replace('T', ' ');
  }
}
