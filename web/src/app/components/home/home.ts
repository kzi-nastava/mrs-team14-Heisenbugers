import { Component } from '@angular/core';
import { provideIcons } from "@ng-icons/core";
import { bootstrapGeo } from "@ng-icons/bootstrap-icons";
import { MapComponent, MapPin } from "../map/map.component";

@Component({
  selector: 'app-home',
  imports: [MapComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
  viewProviders: [provideIcons({ bootstrapGeo })]
})
export class HomeComponent {

  pins: MapPin[] = [{ lat: 45.2396, lng: 19.8227, popup: 'This car is available', iconUrl: 'icons/car-available.svg', iconColor: 'blue'},
  { lat: 45.241, lng: 19.823, popup: 'This car is occupied', iconUrl: 'icons/car-occupied.svg', iconColor: 'blue' }];
  
}
