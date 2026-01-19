import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
    bootstrapCaretDownFill,
    bootstrapCaretUpFill,
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash,
    bootstrapExclamationCircleFill, 
    bootstrapFeather}
    from '@ng-icons/bootstrap-icons';
import { RideInfo } from './driver-info.model';
import { Router } from '@angular/router';
import { RateModal } from "../rate-modal/rate-modal.component";




@Component({
standalone: true,
  imports: [CommonModule, NgIcon, RateModal],
  selector: 'app-ride-history',
  templateUrl: './driver-ride-history.html',
  styleUrls: ['./driver-ride-history.css'],
  viewProviders: [provideIcons({ bootstrapCaretDownFill,
    bootstrapCaretUpFill,
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash,
    bootstrapExclamationCircleFill,
    bootstrapFeather
    })]
})
export class RideHistoryComponent {
  sort: 'date' | 'price' | 'route' = 'date';
  ratingRide: RideInfo | null = null;

  rides: RideInfo[] = [
  ...Array.from({ length: 4 }).map(() => ({
    id: 'ride-' + Math.random().toString(36).substr(2, 9),
    driverName: 'Vozac Vozacovic',
    startLocation: 'ул.Атамана Головатого 2а',
    finishLocation: 'ул.Красная 113',
    startTime: new Date('2025-12-19T08:12:00'),
    endTime: new Date('2025-12-19T10:12:00'),
    price: 350,
    rating: 3.5,
    maxRating: 5,
    cancelled: false,
    passengers: [
      {firstName: 'Alice', lastName: 'Alisic'},
      {firstName: 'Bob', lastName: 'Bobic'},
      {firstName: 'Carl', lastName: 'Carlic'},
      {firstName: 'Denise', lastName: 'Denisic'}
    ],
    trafficViolations: [{type: 'Red light'}],
    wasPanic: false
  })),
  {
    id: 'ride-panic-' + Math.random().toString(36).substr(2, 9),
    driverName: 'Vozac Vozacovic',
    startLocation: 'ул.Атамана Головатого 2а',
    finishLocation: 'ул.Красная 113',
    startTime: new Date('2025-12-19T08:12:00'),
    endTime: new Date('2025-12-19T10:12:00'),
    price: 350,
    rating: 3.5,
    maxRating: 5,
    cancelled: false,
    passengers: [
      {firstName: 'Alice', lastName: 'Alisic'},
      {firstName: 'Bob', lastName: 'Bobic'},
      {firstName: 'Carl', lastName: 'Carlic'},
      {firstName: 'Denise', lastName: 'Denisic'}
    ],
    trafficViolations: [{type: 'Red light'}],
    wasPanic: true
  }
];

constructor(private router: Router) {
  
}

  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }

  goToRide(){
    this.router.navigate(['/driver-ride-history/ride'])
  }

  openRateModal(ride: RideInfo) {
    this.ratingRide = ride;
    console.log(this.ratingRide);
  }

  closeRateModal() {
    this.ratingRide = null;
  }
}
