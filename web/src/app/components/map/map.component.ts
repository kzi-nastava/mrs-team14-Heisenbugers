// map.component.ts
import { Component, Input, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

export interface MapPin {
  lat: number;
  lng: number;
  snapToRoad?: boolean;
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

     this.snapPinsToRoad().then((pins) => {
            this.renderPins(pins);
        });
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['pins'] && this.map) {
        this.snapPinsToRoad().then((pins) => {
            this.renderPins(pins);
        });
    }
  }

  private renderPins(pinArgs: MapPin[]) {
    // remove old markers
    this.markers.forEach(m => m.remove());
    this.markers = [];

    // add new markers
    pinArgs.forEach(pin => {
        let icon: L.Icon | L.DivIcon | undefined = undefined;
        let marker: L.Marker;

        if (pin.iconUrl) {
            icon = L.icon({
            iconUrl: pin.iconUrl,
            iconSize: [32, 32],
            iconAnchor: [16, 16]
            });

            marker = L.marker([pin.lat, pin.lng], { icon })
        } else {
            marker = L.marker([pin.lat, pin.lng]);
        }

        marker.addTo(this.map)
            .bindPopup(pin.popup || '');
        this.markers.push(marker);
        
        });
    }
    private snapPinsToRoad(): Promise<MapPin[]> {
        const snapPromises = this.pins.map(async pin => {
            if (pin.snapToRoad) {
                const snappedLatLng = await this.snapToRoad(pin.lat, pin.lng);
                pin.lat = snappedLatLng.lat;
                pin.lng = snappedLatLng.lng;
            }
            return pin;
            
        });

        return Promise.all(snapPromises);
    }

    snapToRoad(lat: number, lng: number): Promise<L.LatLng> {
        return new Promise((resolve, reject) => {
            const url = `https://router.project-osrm.org/nearest/v1/driving/${lng},${lat}?number=1`;

            fetch(url)
            .then(res => res.json())
            .then(data => {
                if (data.code === 'Ok' && data.waypoints.length) {
                const snapped = data.waypoints[0].location; // [lng, lat]
                resolve(L.latLng(snapped[1], snapped[0]));
                } else {
                reject('No road found nearby');
                }
            })
            .catch(err => reject(err));
        });
    }
}


