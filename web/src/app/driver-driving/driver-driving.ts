import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { LocationDTO } from '../models/ride-estimate.model';
import { MapComponent } from '../components/map/map.component';
import { RideDTO } from '../components/during-ride/during-ride.component';

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

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {
    
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
    
  }

}
