import { Component, Type, ViewChild, ViewContainerRef } from '@angular/core';
import { HomeComponent } from '../components/home/home';
import { DuringRide } from '../components/during-ride/during-ride.component';

@Component({
  selector: 'app-home-container',
  imports: [],
  templateUrl: './home-container.component.html',
  styleUrl: './home-container.component.css',
})
export class HomeContainer {
  @ViewChild('host', { read: ViewContainerRef }) host!: ViewContainerRef;

  private rideInProgress: boolean = true

  ngAfterViewInit() {
    let component: Type<any> = this.rideInProgress ?  DuringRide : HomeComponent
    this.host.clear();
    this.host.createComponent(component)
   
  }
}
