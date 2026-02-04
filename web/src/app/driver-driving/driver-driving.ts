import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { LocationDTO } from '../models/ride-estimate.model';
import { MapComponent } from '../components/map/map.component';
import { RideDTO } from '../components/during-ride/during-ride.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-driver-driving',
  imports: [],
  templateUrl: './driver-driving.html',
  styleUrl: './driver-driving.css',
})
export class DriverDriving {
  @Input() rideId!: string;
  private baseUrl = 'http://localhost:8081/api';
  startLocation?: LocationDTO
  endLocation?: LocationDTO

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, private router: Router) {

  }


  ngOnInit(): void {
    this.http.get<RideDTO>(`${this.baseUrl}/rides/${this.rideId}`).subscribe({
      next: (data) => {

        this.startLocation = data.startLocation
        this.endLocation = data.endLocation
      this.cdr.markForCheck();
      },
    })
  }

  stopRide(): void {

    /*this.http.post<RideDTO>(`${this.baseUrl}/rides/${this.rideId}/stop`, null).subscribe(
      () => {
        window.location.reload()
      }
    )*/

    if (!this.rideId || !this.endLocation) {
      console.error('rideId or endLocation is missing');
      return;
    }

    const body = {
      latitude: this.endLocation.latitude,
      longitude: this.endLocation.longitude,
      address: this.endLocation.address,
      note: 'Stopped by driver'
    };

    this.http.post(
      `${this.baseUrl}/rides/${this.rideId}/stop`,
      body
    ).subscribe({
      next: () => {
        //this.router.navigate(['/driver-ride-history']);
        window.location.reload()
      },
      error: err => {
        console.error('Failed to stop ride', err);
      }
    });

  }



}
