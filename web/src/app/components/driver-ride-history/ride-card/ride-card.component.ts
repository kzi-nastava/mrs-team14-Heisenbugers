import { Component, Input } from '@angular/core';

import { RideInfo } from '../driver-info.model';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapStarFill, bootstrapStarHalf, bootstrapStar, bootstrapPersonCircle, bootstrapClock, bootstrapCash, bootstrapArrowRight, bootstrapArrowLeft } from '@ng-icons/bootstrap-icons';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';


@Component({
  selector: 'app-driver-card',
  templateUrl: './ride-card.component.html',
  styleUrls: ['./ride-card.component.css'],
  imports: [NgIcon, CommonModule],
  viewProviders: [provideIcons({bootstrapStarFill, bootstrapStarHalf, bootstrapStar, bootstrapPersonCircle, bootstrapClock, bootstrapCash, bootstrapArrowRight, bootstrapArrowLeft})]
})
export class RideCardComponent {
  @Input() ride: RideInfo = {
    id: '',
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
  };

  constructor(private router: Router) {
    
  }
  

  getFormattedTime(): string {
    const startTime = this.ride.startTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const endTime = this.ride.endTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    return `${startTime} - ${endTime}`;
  }

  getStars(): { full: number[], half: boolean, empty: number[] } {
    const fullStars = Math.floor(this.ride.rating);
    const hasHalfStar = this.ride.rating % 1 >= 0.5;
    const emptyStars = this.ride.maxRating - fullStars - (hasHalfStar ? 1 : 0);
    
    return {
      full: Array(fullStars).fill(0),
      half: hasHalfStar,
      empty: Array(emptyStars).fill(0)
    };
  }

  get stars(): number[]{
    return Array(this.ride.rating).fill(0);
  }

  goBack(){
    this.router.navigate(['/driver-ride-history'])
  }

}