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

  pins: MapPin[] = [{ lat: 45.2396, lng: 19.8227, popup: 'Car', iconUrl: 'icons/car-front-fill.svg', iconColor: 'blue'},
  { lat: 45.241, lng: 19.823, popup: 'Geo Pin', iconUrl: 'icons/car-front-fill.svg', iconColor: 'blue' }];

  constructor() {

    const popupEl: HTMLDivElement = document.createElement('div');
    popupEl.innerHTML = `<h3>${'Title'}</h3><button class="m-4 bg-red-100">Click me</button>`;

    this.pins[0].popup = popupEl;
    
  }
}
