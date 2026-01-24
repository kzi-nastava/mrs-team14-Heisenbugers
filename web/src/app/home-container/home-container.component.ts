import { Component, Type, ViewChild, ViewContainerRef } from '@angular/core';
import { HomeComponent } from '../components/home/home';
import { DuringRide } from '../components/during-ride/during-ride.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home-container',
  imports: [],
  templateUrl: './home-container.component.html',
  styleUrl: './home-container.component.css',
})
export class HomeContainer {
  @ViewChild('host', { read: ViewContainerRef }) host!: ViewContainerRef;

  private rideInProgress: boolean = false

  private baseUrl = 'http://localhost:8081/api';


  constructor(private http: HttpClient) {
    
  }

  ngAfterViewInit() {
    this.http.get<String>(`${this.baseUrl}/users/state`).subscribe(
      {
        next: (data) => {
          if (data === 'RIDING'){
            this.rideInProgress = true
          }
          let component: Type<any> = this.rideInProgress ?  DuringRide : HomeComponent
    this.host.clear();
    this.host.createComponent(component)
        },

        error: (data) => {
          this.rideInProgress = false
          let component: Type<any> = this.rideInProgress ?  DuringRide : HomeComponent
    this.host.clear();
    this.host.createComponent(component)
        }
      }
    )
    
   
  }
}
