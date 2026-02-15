import { Component, EventEmitter, Input, Output } from "@angular/core";
import { AdminRide } from "../../../../models/admin-ride.model";
import { MapPin, MapComponent } from "../../../map/map.component";
import { DatePipe } from "@angular/common";

@Component({
  selector: 'admin-ride-info',
  templateUrl: './ride-info.component.html',
  imports: [MapComponent, DatePipe],
})
export class AdminRideInfo {
  @Input() dto!: AdminRide
  @Output() close = new EventEmitter<void>();

  ngOnInit() {
    console.log('Ride info DTO:', this.dto);
    this.pins = [
    {
        lat: this.dto.vehicleLatitude,
        lng: this.dto.vehicleLongitude,
        iconUrl: 'icons/car-selected.svg',
    }
  ];
  }


  pins: MapPin[] = [];

  onClose() {
    this.close.emit();
  }

  
}