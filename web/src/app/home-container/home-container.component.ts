import { Component, Type, ViewChild, ViewContainerRef } from '@angular/core';
import { HomeComponent } from '../components/home/home';
import { DuringRide } from '../components/during-ride/during-ride.component';
import { HttpClient } from '@angular/common/http';
import DriverDriving from '../driver-driving/driver-driving';

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

  private rideId?: string;

  private baseUrl = 'http://localhost:8081/api';


  constructor(private http: HttpClient) {

  }


  ngAfterViewInit() {
    this.http.get<UserStateDTO>(`${this.baseUrl}/users/state`).subscribe(
      {
        next: (data) => {
          let component: Type<any>
          if (data.state === 'LOOKING'){
            component = HomeComponent
          } else if (data.state === 'RIDING') {
            this.rideId = data.rideId;
            component = DuringRide
            const ref = this.host.createComponent(component)
            ref.setInput('rideId', this.rideId);
            ref.setInput('external', false);
            return;
          } else if (data.state === 'DRIVING'){
            this.rideId = data.rideId;
            component = DriverDriving
          } else if (data.state === 'READY'){
            component = HomeComponent
          } else if (data.state === 'STARTING'){
            this.rideId = data.rideId;
            component = HomeComponent
          } else {
            component = HomeComponent
          }
          this.host.clear();
          const ref = this.host.createComponent(component)
          ref.setInput('rideId', this.rideId);
        },

        error: (data) => {
          let component: Type<any> = HomeComponent
          this.host.clear();
          const ref = this.host.createComponent(component)
          ref.setInput('rideId', this.rideId);
        }
      }
    )


  }
}
