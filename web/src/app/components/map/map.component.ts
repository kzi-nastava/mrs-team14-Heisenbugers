// map.component.ts
import { Component, Input, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import * as L from 'leaflet';

export interface MapPin {
  lat: number;
  lng: number;
  popup?: string | HTMLElement;
  iconUrl?: string;
  iconColor?: string;
}

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.component.html',
})
export class MapComponent implements AfterViewInit, OnChanges {
  @Input() pins: MapPin[] = [];
  private map!: L.Map;
  private markers: L.Marker[] = [];

  ngAfterViewInit(): void {
    this.map = L.map('map', { zoomControl: false }).setView([45.2396, 19.8227], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    L.control.zoom({ position: 'bottomright' }).addTo(this.map);
    L.control.scale().addTo(this.map);

    this.renderPins();
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['pins'] && this.map) {
      this.renderPins();
    }
  }

  private renderPins() {
    // remove old markers
    this.markers.forEach(m => m.remove());
    this.markers = [];

    // add new markers
    this.pins.forEach(pin => {
        let icon: L.Icon | L.DivIcon | undefined = undefined;
        let marker: L.Marker;

        if (pin.iconUrl) {
            icon = L.icon({
            iconUrl: pin.iconUrl,
            iconSize: [32, 32],
            iconAnchor: [16, 16]
            });

            marker = L.marker([pin.lat, pin.lng], { icon })
        } else{
            marker = L.marker([pin.lat, pin.lng]);
        }

        marker.addTo(this.map)
            .bindPopup(pin.popup || '');
        this.markers.push(marker);
        });
    }
}
