import { Component, Type, ViewChild, ViewContainerRef } from '@angular/core';
import { HomeComponent } from '../components/home/home';
import { DuringRide } from '../components/during-ride/during-ride.component';
import { HttpClient } from '@angular/common/http';

interface UserStateDTO {
  rideId: string,
  state: string,
}

@Component({
  selector: 'app-home-container',
  imports: [],
  templateUrl: './home-container.component.html',
  styleUrl: './home-container.component.css',
})
export class HomeContainer {
  @ViewChild('host', { read: ViewContainerRef }) host!: ViewContainerRef;

  private rideInProgress: boolean = false
  private rideId?: string;

  private baseUrl = 'http://localhost:8081/api';


  constructor(private http: HttpClient) {
    
  }


  ngAfterViewInit() {
    this.http.get<UserStateDTO>(`${this.baseUrl}/users/state`).subscribe(
      {
        next: (data) => {
          if (data.state === 'LOOKING'){
            this.rideInProgress = false;
          } else if (data.state === 'RIDING') {
            this.rideId = data.rideId;
            this.rideInProgress = true
          }
          let component: Type<any> = this.rideInProgress ?  DuringRide : HomeComponent
          this.host.clear();
          const ref = this.host.createComponent(component)
          ref.setInput('rideId', this.rideId);
        },

        error: (data) => {
          this.rideInProgress = false
          let component: Type<any> = this.rideInProgress ?  DuringRide : HomeComponent
          this.host.clear();
          const ref = this.host.createComponent(component)
          ref.setInput('rideId', this.rideId);
        }
      }
    )
    
   
  }
}
