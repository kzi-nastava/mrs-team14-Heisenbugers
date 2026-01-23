import { Component, ElementRef, ViewChild } from '@angular/core';
import { carSelectedIcon, MapComponent } from '../map/map.component';
import { NgIcon, provideIcons } from "@ng-icons/core";
import { bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX } from '@ng-icons/bootstrap-icons';
import { MapPin } from '../map/map.component';
import { FormsModule, NgForm } from '@angular/forms';
import { RateModal } from "../rate-modal/rate-modal.component";
import { RideInfo } from '../../models/driver-info.model';
import { LatLng } from 'leaflet';
import { ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';

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
  estimatedTimeRemainingMinutes: number
  route: Location[]
}


@Component({
  selector: 'app-during-ride',
  imports: [MapComponent, NgIcon, FormsModule, RateModal],
  templateUrl: './during-ride.component.html',
  viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX })]

})
export class DuringRide {
  private stops?: L.LatLng[]
  private baseUrl = 'http://localhost:8081/api';
  private rideId: string = "c527273a-ba41-43e2-aa7c-ab78560177ee";

  private mockStops: L.LatLng[] = [
    new LatLng(45.249570, 19.815809),
    new LatLng(45.242299, 19.796333),
    new LatLng(45.241604, 19.842757),
    
  ]

  locations: MapPin[] = [];
  
  
  @ViewChild('noteFocus') noteFocus!: ElementRef<HTMLInputElement>;
  @ViewChild(MapComponent) mapCmp!: MapComponent;
  
  ride: RideInfo = {
    rideId: 'ride-' + Math.random().toString(36).substr(2, 9),
    driverName: 'Vozac Vozacovic',
    startAddress: 'ул.Атамана Головатого 2а',
    endAddress: 'ул.Красная 113',
    startedAt: new Date('2025-12-19T08:12:00'),
    endedAt: new Date('2025-12-19T10:12:00'),
    price: 350,
    rating: 3.5,
    maxRating: 5,
    canceled: false,
    passengers: [
      {firstName: 'Alice', lastName: 'Alisic'},
      {firstName: 'Bob', lastName: 'Bobic'},
      {firstName: 'Carl', lastName: 'Carlic'},
      {firstName: 'Denise', lastName: 'Denisic'}
    ],
    trafficViolations: [{type: 'Red light'}],
    panicTriggered: false
  
  };
  NotesIsOpen: boolean = false;
  rateIsOpen: boolean = false;
  driverRate = 0;
  vehicleRate = 0;

  location: MapPin = { lat: 45.249570, lng: 19.815809, popup: 'You are here', iconUrl: carSelectedIcon };
  passengers = [
    { name: 'Alice Alisic', avatar: 'https://i.pravatar.cc/150?img=1' },
    { name: 'Bob Bobic', avatar: 'https://i.pravatar.cc/150?img=2' },
    { name: 'Carl Carlic', avatar: 'https://i.pravatar.cc/150?img=3' },
    { name: 'Denise Denisic', avatar: 'https://i.pravatar.cc/150?img=4' }
  ];
  constructor(private cdr: ChangeDetectorRef, private http: HttpClient) {
    if (!this.mockStops || this.mockStops.length < 2){
      return;
    }
  }

  ngOnInit(): void {
    this.http.get<TrackingDTO>(`${this.baseUrl}/rides/${this.rideId}/tracking`).subscribe({
      next: (data) => {
        this.stops = data.route.map((l: Location) => {
          return new LatLng(l.latitude, l.longitude);
        })
        let inBetween = this.stops.slice(1, -1)
      this.locations = [{...this.stops[0], popup: "You are here", iconUrl: carSelectedIcon}]
      this.locations.push(...inBetween.map((stop: LatLng) => {
        return {...stop, popup: "Stop"}
      }))
      this.locations.push(this.stops.at(-1)!)
      this.mapCmp.showRoute(this.stops[0], this.stops[this.stops.length - 1], this.stops.slice(1, -1))
      this.cdr.markForCheck();

      },
      error: (error) => {
        console.warn('Using mock data due to error fetching ride history:', error);
        this.stops = this.mockStops;
        this.cdr.markForCheck();
      }
    });
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
    if(form.valid)
      console.log(form.value);
      this.http.post(`${this.baseUrl}/rides/${this.rideId}/report`, form.value)
        .subscribe({
          next: (response) => console.log('Note submitted successfully', response),
          error: (err) => console.error('Error submitting note', err)
        });
    this.closeModal();
  }

  submitRateForm(form: NgForm) {
    if(form.valid && this.driverRate > 0 && this.vehicleRate > 0)
      console.log(form.value, this.driverRate, this.vehicleRate);
    this.closeRateModal();
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

}
