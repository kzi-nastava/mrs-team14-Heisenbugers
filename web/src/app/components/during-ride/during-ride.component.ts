
import { Component, ElementRef, Input, ViewChild, inject } from '@angular/core';

import { carSelectedIcon, MapComponent } from '../map/map.component';
import { NgIcon, provideIcons } from "@ng-icons/core";
import { bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX } from '@ng-icons/bootstrap-icons';
import { MapPin } from '../map/map.component';
import { FormsModule, NgForm } from '@angular/forms';
import { RateModal } from "../rate-modal/rate-modal.component";

//import { RideInfo } from '../driver-ride-history/driver-info.model';
import { PanicService } from '../../services/panic.service';
import { ActivatedRoute } from '@angular/router';
import { RideInfo } from '../../models/driver-info.model';
import { LatLng } from 'leaflet';
import { ChangeDetectorRef } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RideRateInfo } from '../../models/ride.model';
import {AuthService} from '../auth/auth.service';

interface Location {
  latitude: number,
  longitude: number,
  address?: string
}

interface TrackingDTO {
  rideId: string,
  driver: {firstName: string, lastName: string}
  vehicleLatitude: number,
  vehicleLongitude: number,
  estimatedTimeRemainingMinutes: number,
  route: Location[],
  startLocation: Location,
  endLocation: Location,
}


export interface RideDTO {
  rideId: string,
  driver: {firstName: string, lastName: string}
  route: Location[],
  startLocation: Location,
  endLocation: Location,
  startTime: string,
  endTime: string,
  price: number,
}



@Component({
  selector: 'app-during-ride',
  imports: [MapComponent, NgIcon, FormsModule, RateModal],
  templateUrl: './during-ride.component.html',
  viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX })]

})


export class DuringRide {
  @Input() rideId!: string;
  @Input() external!: boolean;
  private stops?: L.LatLng[]
  private baseUrl = 'http://localhost:8081/api';

  private mockStops: L.LatLng[] = [
    new LatLng(45.249570, 19.815809),
    new LatLng(45.242299, 19.796333),
    new LatLng(45.241604, 19.842757),

  ]

  locations: MapPin[] = [];

  vehicleCoords?: {vehicleLatitude: number, vehicleLongitude: number};
  startLocation?: Location
  endLocation?: Location
  rideRate?: RideRateInfo

  private panicApi = inject(PanicService);
  ride: any;

  @ViewChild('noteFocus') noteFocus!: ElementRef<HTMLInputElement>;
  @ViewChild(MapComponent) mapCmp!: MapComponent;

  NotesIsOpen: boolean = false;
  rateIsOpen: boolean = false;
  driverRate = 0;
  vehicleRate = 0;
  etimateMinutes?: number;

  location: MapPin = { lat: 45.249570, lng: 19.815809, popup: 'You are here', iconUrl: carSelectedIcon };
  passengers = [
    { name: 'Alice Alisic', avatar: 'https://i.pravatar.cc/150?img=1' },
    { name: 'Bob Bobic', avatar: 'https://i.pravatar.cc/150?img=2' },
    { name: 'Carl Carlic', avatar: 'https://i.pravatar.cc/150?img=3' },
    { name: 'Denise Denisic', avatar: 'https://i.pravatar.cc/150?img=4' }
  ];

  toastVisible = false;
  toastMessage = '';


  constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private route: ActivatedRoute, private authService: AuthService) {

    if (!this.mockStops || this.mockStops.length < 2){
      return;
    }
  }

  useMockData(error: any): void {
        console.warn('Using mock data due to error fetching current ride:', error);
        this.stops = this.mockStops;
        this.cdr.markForCheck();
  }

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token') ?? "";
    var id;
    if (this.external) {
      id = this.authService.getRideId(token);
    }

    if(id){
      this.rideId = id;
    }

    if (this.external === undefined) {
      this.route.data.subscribe(data => {
      this.external = data['external'] ?? false;
      });
    }

    const fromRoute = this.route.snapshot.paramMap.get('rideId');
    if (!this.rideId && fromRoute) {
      this.rideId = fromRoute;
    }

    if (!this.rideId) {
      console.error('DuringRide: rideId is missing');
      this.useMockData('Missing rideId');
      return;
    }
    let urls = [`${this.baseUrl}/rides/${this.rideId}/tracking`, `${this.baseUrl}/rides/${this.rideId}` ]
    if (this.external) {
      console.log('External tracking mode');
      urls = [`${this.baseUrl}/rides/link-tracking/tracking`, `${this.baseUrl}/rides/link-tracking/ride`]
    }

    this.subscribeForRide(urls);

  }

  subscribeForRide(urls: string[]): void {

    const token = this.route.snapshot.queryParamMap.get('token');
    const params = new HttpParams().set('token', token ?? '');

    this.http.get<TrackingDTO>(urls[0], { params }).subscribe({
      next: (data) => {
        this.vehicleCoords = {
          vehicleLatitude: data.vehicleLatitude,
          vehicleLongitude: data.vehicleLongitude,
        }
        this.locations = [...this.locations,
          {
          lat: data.vehicleLatitude,
          lng: data.vehicleLongitude,
          popup: "You are here",
          iconUrl: carSelectedIcon,
          snapToRoad: true,
        }]
      },
      error: (error) => this.useMockData(error)
    });

    this.http.get<RideDTO>(urls[1], { params }).subscribe({
      next: (data) => {
        this.stops = data.route.map((l: Location) => {
          return new LatLng(l.latitude, l.longitude);
        })
        this.startLocation = data.startLocation
        this.endLocation = data.endLocation
        this.rideRate = {
          startAddress: data.startLocation.address!,
          endAddress: data.endLocation.address!,
          price: data.price,
          rated: false,
          startTime: new Date(data.startTime),
          endTime: new Date(data.endTime),

        }
        let inBetween = this.stops.slice(1, -1)
        this.locations = [...this.locations, {...this.stops[0], popup: "Start"}]
        this.locations.push(...inBetween.map((stop: LatLng) => {
        return {...stop, popup: "Stop"}
      }))




      this.locations.push({...this.stops.at(-1)!, popup: "Final destination"})
      this.mapCmp.showRoute(this.stops[0], this.stops[this.stops.length - 1], this.stops.slice(1, -1))
      this.cdr.markForCheck();
      this.addEstimateMinutes()
      },
      error: (error) => this.useMockData(error)
    });
  }

  addEstimateMinutes(): void {
    const map = new MapComponent(this.http)
    const stops = this.locations.map(l => {
      return new LatLng(l.lat, l.lng)
    })
    map.initMap().then(() => {
    map.showRoute(stops[0], stops[stops.length - 1], stops.slice(1, -1), map.dummyMap).then(
      (summary) => {
        this.etimateMinutes = summary.timeMin
        this.cdr.markForCheck()
      }
    )
  })
  }

  showToast(message: string, duration: number = 2000) {
    console.log(`Trying to show ${message}`)
  this.toastMessage = message;
  this.toastVisible = true;
  this.cdr.markForCheck();
  setTimeout(() => {this.toastVisible = false; this.cdr.markForCheck()}, duration);
  }

  closeModal() {
    this.NotesIsOpen = false;
  }

  openModal() {
    this.noteFocus.nativeElement.blur();
    this.NotesIsOpen = true;
  }

  closeRateModal() {
  this.rateIsOpen = false;
  }

  openRateModal() {
    this.driverRate = this.vehicleRate = 0;
    this.rateIsOpen = true;
  }

  submitForm(form: NgForm) {
    if(form.valid){
      console.log(form.value);
      this.http.post(`${this.baseUrl}/rides/${this.rideId}/report`, form.value)
        .subscribe({
          next: () => this.showToast('Note recorded successfully!'),
          error: () => this.showToast('Failed to record note')
        });
    this.closeModal();
    }
  }


  //нужно ли эта кнапка
  submitRateForm(data: { driverRate: number; vehicleRate: number; comment: string; }) {
    let sendingData = {
        "driverScore": data.driverRate,
        "vehicleScore": data.vehicleRate,
        "comment": data.comment,
      }
      this.http.post(`${this.baseUrl}/rides/${this.rideId}/rate`, sendingData)
      .subscribe({
      next: () => this.showToast('Rating recorded successfully!'),
      error: (error) => {
        if (error.status === 409){
          this.showToast('You have already rated this ride')
        } else {
          this.showToast('Failed to record rating')
        }
      }
    })
    this.closeRateModal();
    console.log(sendingData)

  }

  getDriverRateArray(): boolean[] {
    return Array(this.driverRate).fill(true).concat(Array(5 - this.driverRate).fill(false));
  }

  setDriverRate(value: number) {
    this.driverRate = value;
  }

  getVehicleRateArray(): boolean[] {
    return Array(this.vehicleRate).fill(true).concat(Array(5 - this.vehicleRate).fill(false));
  }

  setVehicleRate(value: number) {
    this.vehicleRate = value;
  }

  stopConfirmOpen = false;
  stopSubmitting = false;
  stopError: string | null = null;

  openStopConfirm() {
    this.stopError = null;
    this.stopConfirmOpen = true;
  }

  closeStopConfirm() {
    this.stopConfirmOpen = false;
  }

  private async getCurrentStopPoint(): Promise<{ latitude: number; longitude: number }> {
    //caording from tracking
    const lat = this.vehicleCoords?.vehicleLatitude;
    const lng = this.vehicleCoords?.vehicleLongitude;

    if (typeof lat === 'number' && typeof lng === 'number') {
      return { latitude: lat, longitude: lng };
    }


    return await new Promise((resolve, reject) => {
      if (!navigator.geolocation) return reject('Geolocation is not available');

      navigator.geolocation.getCurrentPosition(
        (pos) => resolve({ latitude: pos.coords.latitude, longitude: pos.coords.longitude }),
        () => reject('Cannot get current location')
      );
    });
  }

  async stopRideNow() {
    if (!this.rideId) return;

    this.stopSubmitting = true;
    this.stopError = null;

    try {
      const point = await this.getCurrentStopPoint();

      const body = {
        note: 'Stopped by driver',
        latitude: point.latitude,
        longitude: point.longitude,
        address: null
      };

      // /api/rides/{rideId}/stop
      await this.http.post(`${this.baseUrl}/rides/${this.rideId}/stop`, body).toPromise();

      this.showToast('Ride stopped successfully!');
      this.stopConfirmOpen = false;


      // this.router.navigateByUrl('/driver-ride-history');
    } catch (e: any) {
      this.stopError = e?.error?.message ?? (typeof e === 'string' ? e : 'Failed to stop ride');
    } finally {
      this.stopSubmitting = false;
      this.cdr.markForCheck();
    }
  }


  //ПРОВЕРИТЬ ПАНИК КНОПКУ
  async panicClick() {
    //const rideId = this.ride?.rideId;
    if (!this.rideId) return;

    const msg = prompt('Describe the problem (optional):') ?? '';
    this.panicApi.panic(String(this.rideId), msg).subscribe({
      next: () => alert('Panic sent to administrators.'),
      error: (e) => alert(e?.error?.message ?? 'Panic failed')
    });
  }

}
