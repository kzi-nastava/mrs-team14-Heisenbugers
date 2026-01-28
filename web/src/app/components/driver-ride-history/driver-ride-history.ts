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
import { RideInfo } from '../../models/driver-info.model';
import { Router } from '@angular/router';
import { RateModal } from "../rate-modal/rate-modal.component";
import { HttpClient } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import { LatLng } from 'leaflet';


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
    startTime: new Date('2025-12-19T08:12:00'),
    endTime: new Date('2025-12-19T10:12:00'),
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
    trafficViolations: [{title: 'Red light'}],
    panicTriggered: false
  })),
  {
    rideId: 'ride-panic-' + Math.random().toString(36).substr(2, 9),
    driverName: 'Vozac Vozacovic',
    startAddress: 'ул.Атамана Головатого 2а',
    endAddress: 'ул.Красная 113',
    startedAt: new Date('2025-12-19T08:12:00'),
    endedAt: new Date('2025-12-19T10:12:00'),
    startTime: new Date('2025-12-19T08:12:00'),
    endTime: new Date('2025-12-19T10:12:00'),
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
    trafficViolations: [{title: 'Red light'}],
    panicTriggered: true
  }];
  toastMessage?: string;
  toastVisible?: boolean;


constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) {

}

  ngOnInit(): void {
    this.http.get<RideInfo[]>(`${this.baseUrl}/drivers/history`).subscribe({
      next: (data) => {
        this.rides = data.map(r => ({
          ...r,
          startedAt: new Date(r.startedAt),
          startTime: new Date(r.startedAt),
          endTime: new Date(r.endedAt),
          endedAt: new Date(r.endedAt),
        }));
        this.cdr.markForCheck();
        console.log(this.rides);

      },
      error: (error) => {
        console.warn('Using mock data due to error fetching ride history:', error);
        this.rides = this.mockRides;
        this.cdr.markForCheck();
      }
    });
    
  }


  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }

  goToRide(ride: RideInfo){
    this.router.navigate(['/driver-ride-history/ride'], {state: {ride: ride}})
  }

  openRateModal(ride: RideInfo) {
    this.ratingRide = ride;
    console.log(this.ratingRide);
  }

  closeRateModal() {
    this.ratingRide = null;
  }

  showToast(message: string, duration: number = 2000) {
    console.log(`Trying to show ${message}`)
  this.toastMessage = message;
  this.toastVisible = true;
  this.cdr.markForCheck();
  setTimeout(() => {this.toastVisible = false; this.cdr.markForCheck()}, duration);
  }

  submitRateForm(data: { driverRate: number; vehicleRate: number; comment: string; }) {
    let sendingData = {
        "driverScore": data.driverRate,
        "vehicleScore": data.vehicleRate,
        "comment": data.comment,
      }
      this.http.post(`${this.baseUrl}/rides/${this.ratingRide?.rideId}/rate`, sendingData)
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
}
