import { ChangeDetectorRef, Component, Input, ViewChild } from '@angular/core';

import { RideInfo } from '../../../models/driver-info.model';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapStarFill, bootstrapStarHalf, bootstrapStar, bootstrapPersonCircle, bootstrapClock, bootstrapCash, bootstrapArrowRight, bootstrapArrowLeft } from '@ng-icons/bootstrap-icons';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MapComponent } from '../../map/map.component';
import { LatLng } from 'leaflet';


@Component({
  selector: 'app-driver-card',
  templateUrl: './ride-card.component.html',
  styleUrls: ['./ride-card.component.css'],
  standalone: true,
  imports: [NgIcon, CommonModule, MapComponent],
  viewProviders: [provideIcons({bootstrapStarFill, bootstrapStarHalf, bootstrapStar, bootstrapPersonCircle, bootstrapClock, bootstrapCash, bootstrapArrowRight, bootstrapArrowLeft})]
})
export class RideCardComponent {
  ride?: RideInfo;
  @ViewChild(MapComponent) mapCmp!: MapComponent;


  constructor(private router: Router, private cdr: ChangeDetectorRef) {
    setTimeout(() => {
      this.ride = history.state.ride;
      console.log(this.ride)
      let stops = this.ride!.route!.map(l => {
        return new LatLng(l.latitude, l.longitude)
      })
      this.cdr.markForCheck();
      this.mapCmp.showRoute(stops[0], stops[stops.length - 1], stops.slice(1, -1)).then(
        () => {
          this.cdr.markForCheck();
        }
      )
      
    })
    
  }


  getFormattedTime(): string {
    const startTime = this.ride?.startedAt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const endTime = this.ride?.endedAt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    return `${startTime} - ${endTime}`;
  }

  getStars(): { full: number[], half: boolean, empty: number[] } {
    let rating = this.ride?.rating? this.ride?.rating : 0
    let maxRating = this.ride?.maxRating? this.ride?.maxRating : 5

    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    const emptyStars = maxRating - fullStars - (hasHalfStar ? 1 : 0);
    
    return {
      full: Array(fullStars).fill(0),
      half: hasHalfStar,
      empty: Array(emptyStars).fill(0)
    };
  }

  get stars(): number[]{
    let rating = this.ride?.rating? this.ride?.rating : 0
    return Array(rating).fill(0);
  }

  goBack(){
    this.router.navigate(['/driver-ride-history'])
  }

}