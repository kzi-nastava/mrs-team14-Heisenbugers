
import { Component, Input, AfterViewInit, OnChanges, SimpleChanges, booleanAttribute } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

const carAvailableIcon: string = 'icons/car-available.svg';
const carOccupiedIcon: string = 'icons/car-occupied.svg';
const carSelectedIcon: string = 'icons/car-selected.svg';

export {carAvailableIcon, carOccupiedIcon, carSelectedIcon};

export interface MapPin {
  lat: number;
  lng: number;
  snapToRoad?: boolean;
  popup?: string | HTMLElement;
  iconUrl?: string;
}
export type RouteSummary = {
  distanceKm: number;
  timeMin: number;
};

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.component.html',
})

export class MapComponent implements AfterViewInit, OnChanges {
  @Input() pins: MapPin[] = [];
  @Input({ transform: booleanAttribute }) goBelow = false;
  private map!: L.Map;
  private markers: L.Marker[] = [];



  private routingControl: any | null = null;

  private routeLine: L.Polyline | null = null;

  drawRoute(points: L.LatLng[]) {
    if (!this.map) return;


    if (this.routeLine) {
      this.routeLine.remove();
      this.routeLine = null;
    }

    if (!points.length) return;

    this.routeLine = L.polyline(points, { weight: 5, opacity: 0.8 }).addTo(this.map);
    this.map.fitBounds(this.routeLine.getBounds(), { padding: [30, 30] });
  }
  ngAfterViewInit(): void {
    this.map = L.map('map', { zoomControl: false }).setView([45.2396, 19.8227], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);
    if (this.goBelow) {
        Object.values(this.map.getPanes()).forEach(pane => {
            pane.style.zIndex = '0';
        });
    }

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


  showRoute(from: L.LatLng, to: L.LatLng, stops: L.LatLng[] = []): Promise<RouteSummary> {
    return new Promise((resolve, reject) => {
      if (!this.map) {
        reject(new Error('Map is not initialized yet'));
        return;
      }

      // delete last route
      if (this.routingControl) {
        this.map.removeControl(this.routingControl);
        this.routingControl = null;
      }

      const waypoints = [from, ...stops, to];

      const Routing = (L as any).Routing;
      if (!Routing) {
        reject(new Error('Leaflet Routing Machine is not available (L.Routing is undefined).'));
        return;
      }

      this.routingControl = Routing.control({
        waypoints,
        router: Routing.osrmv1({
          serviceUrl: 'https://router.project-osrm.org/route/v1',
        }),
        addWaypoints: false,
        draggableWaypoints: false,
        routeWhileDragging: false,
        show: false,
        fitSelectedRoutes: true,
        createMarker: () => null,
        lineOptions: { styles: [{ weight: 5, opacity: 0.8 }] },
      }).addTo(this.map);

      this.routingControl.on('routesfound', (e: any) => {
        const route = e?.routes?.[0];
        const distM = Number(route?.summary?.totalDistance ?? 0);
        const timeS = Number(route?.summary?.totalTime ?? 0);

        resolve({
          distanceKm: Math.round((distM / 1000) * 10) / 10,   // 1 знак
          timeMin: Math.max(1, Math.round(timeS / 60)),
        });
      });

      this.routingControl.on('routingerror', (e: any) => {
        reject(e);
      });
    });
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
        if (pin.popup) {
            marker.bindPopup(pin.popup);
        }
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


