import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
    bootstrapCaretDownFill,
    bootstrapCaretUpFill,
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash }
    from '@ng-icons/bootstrap-icons';
import { RideInfo } from './driver-info.model';




@Component({
standalone: true,
  imports: [CommonModule, NgIcon],
  selector: 'app-ride-history',
  templateUrl: './driver-ride-history.html',
  styleUrls: ['./driver-ride-history.css'],
  viewProviders: [provideIcons({ bootstrapCaretDownFill,
    bootstrapCaretUpFill,
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash
    })]
})
export class RideHistoryComponent {
  sort: 'date' | 'price' | 'route' = 'date';

  rides: RideInfo[] = [
  ...Array.from({ length: 4 }).map(() => ({
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

  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }

  goToRide(){
    window.location.href += '/ride'
  }
}
