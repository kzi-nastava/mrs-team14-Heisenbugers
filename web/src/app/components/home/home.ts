import { Component } from '@angular/core';
import { provideIcons } from "@ng-icons/core";
import { bootstrapGeo } from "@ng-icons/bootstrap-icons";
import { carAvailableIcon, carOccupiedIcon, MapComponent, MapPin } from "../map/map.component";
import {RideBookingComponent} from '../ride-booking/ride-booking.component';

@Component({
  selector: 'app-home',
  imports: [MapComponent, RideBookingComponent],
  templateUrl: './home.html',
  viewProviders: [provideIcons({ bootstrapGeo })]
})
export class HomeComponent {

  pins: MapPin[] = [{ lat: 45.2396, lng: 19.8227, popup: 'This car is available', iconUrl: carAvailableIcon, snapToRoad: true },
  { lat: 45.241, lng: 19.823, popup: 'This car is occupied', iconUrl: carOccupiedIcon, snapToRoad: true }];

}
