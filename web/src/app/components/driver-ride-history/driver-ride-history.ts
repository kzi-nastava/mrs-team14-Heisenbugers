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
import { HttpClient } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';







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
  private baseUrl = 'http://localhost:8081/api';
  sort: 'date' | 'price' | 'route' = 'date';
  ratingRide: RideInfo | null = null;

  rides: RideInfo[] = [];
  mockRides: RideInfo[] = [
    ...Array.from({ length: 4 }).map(() => ({
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
  })),
  {
    rideId: 'ride-panic-' + Math.random().toString(36).substr(2, 9),
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
    panicTriggered: true
  }];


constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) {

}

  ngOnInit(): void {
    this.http.get<RideInfo[]>(`${this.baseUrl}/drivers/history`).subscribe({
      next: (data) => {
        this.rides = data.map(r => ({
          ...r,
          startedAt: new Date(r.startedAt),
          endedAt: new Date(r.endedAt)
    }));
    this.cdr.markForCheck();


      },
      error: (error) => {
        console.warn('Using mock data due to error fetching ride history:', error);
        this.rides = this.mockRides;
      }
    });
    
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
