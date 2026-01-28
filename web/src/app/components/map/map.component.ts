
import {
  Component,
  Input,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
  booleanAttribute,
  Output,
  EventEmitter
} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

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
  @Output() routeSummary = new EventEmitter<RouteSummary>();
  private map!: L.Map;
  private markers: L.Marker[] = [];
  dummyMap!: L.Map



  private routingControl: any | null = null;

  private routeLine: L.Polyline | null = null;

  constructor(private http: HttpClient) {}

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
    console.log('Map initialized');
  }

  private async updateRouteFromPins(pins: MapPin[]) {
    if (!this.map) return;

    // Remove route if not enough pins
    if (pins.length < 2) {
      if (this.routingControl) {
        this.map.removeControl(this.routingControl);
        this.routingControl = null;
      }
      return;
    }

    const startPin = pins.find(p=> p.popup == "Start");
    const endPin = pins.find(p=> p.popup == "End");

    const startLatLng: L.LatLng | null = startPin ? L.latLng(startPin.lat, startPin.lng) : null;
    const endLatLng: L.LatLng | null = endPin ? L.latLng(endPin.lat, endPin.lng) : null;

    if (!startLatLng || !endLatLng) {
      console.warn('Start or End pin not found for route rendering.');
      return;
    }

    const stopLatLngs = pins.filter(p => p.popup != "Start" && p.popup != "End").map(p =>  L.latLng(p.lat, p.lng));

    const from = startLatLng;
    const to = endLatLng;
    const stops = stopLatLngs;

    const summary = await this.showRoute(from, to, stops);
    this.routeSummary.emit(summary);
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['pins'] && this.map) {
        this.snapPinsToRoad().then((pins) => {
            this.renderPins(pins);
        });
    }
  }

  initMap(): Promise<void>{
    return new Promise(resolve => {
      this.dummyMap = L.map('dummyMap', { zoomControl: false }).setView([45.2396, 19.8227], 15);
      resolve()
    })
  }

  registerOnClick(): void {
    this.map.on('click', (e: any) => {
      const coord = e.latlng;
      const lat = coord.lat;
      const lng = coord.lng;
      this.reverseSearch(lat, lng).subscribe((res) => {
        console.log(res.display_name);
      });
      console.log(
        'You clicked the map at latitude: ' + lat + ' and longitude: ' + lng
      );
      const mp = new L.Marker([lat, lng]).addTo(this.map);
    });
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get('http://localhost:8081/api/geocode/reverse', {
      params: {
        lat: lat.toString(),
        lon: lon.toString()
      }
    });
  }

  showRoute(from: L.LatLng, to: L.LatLng, stops: L.LatLng[] = [], map = this.map): Promise<RouteSummary> {
    return new Promise((resolve, reject) => {
      if (!map) {
        reject(new Error('Map is not initialized yet'));
        return;
      }

      // delete last route
      if (this.routingControl) {
        map.removeControl(this.routingControl);
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
        fitSelectedRoutes: true,
        show: false,
        createMarker: () => null,
        lineOptions: { styles: [{ weight: 5, opacity: 0.8 }] },
      }).addTo(map);


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

      this.updateRouteFromPins(pinArgs);
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


