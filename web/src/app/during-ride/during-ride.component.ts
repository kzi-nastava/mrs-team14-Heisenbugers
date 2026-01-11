import { Component, ElementRef, ViewChild } from '@angular/core';
import { carSelectedIcon, MapComponent } from '../components/map/map.component';
import { NgIcon, provideIcons } from "@ng-icons/core";
import { bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill } from '@ng-icons/bootstrap-icons';
import { MapPin } from '../components/map/map.component';
import { FormsModule, NgForm } from '@angular/forms';


@Component({
  selector: 'app-during-ride',
  imports: [MapComponent, NgIcon, FormsModule],
  templateUrl: './during-ride.component.html',
  viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill })]

})
export class DuringRide {
  
  
  @ViewChild('noteFocus') noteFocus!: ElementRef<HTMLInputElement>;
  
  NotesIsOpen: boolean = false;
  rateIsOpen: boolean = true;
  driverRate = 0;
  vehicleRate = 0;

  location: MapPin = { lat: 45.2396, lng: 19.8227, popup: 'You are here', iconUrl: carSelectedIcon };
  passengers = [
    { name: 'Alice Alisic', avatar: 'https://i.pravatar.cc/150?img=1' },
    { name: 'Bob Bobic', avatar: 'https://i.pravatar.cc/150?img=2' },
    { name: 'Carl Carlic', avatar: 'https://i.pravatar.cc/150?img=3' },
    { name: 'Denise Denisic', avatar: 'https://i.pravatar.cc/150?img=4' }
  ];

  closeModal() {
    this.NotesIsOpen = false;
  }

  openModal() {
    this.noteFocus.nativeElement.blur();
    this.NotesIsOpen = true;
  }

  closeRateModal() {
  this.rateIsOpen = false;
  }

  openRateModal() {
    this.driverRate = this.vehicleRate = 0;
    this.rateIsOpen = true;
  }

  submitForm(form: NgForm) {
    if(form.valid)
      console.log(form.value);
    this.closeModal();
  }

  submitRateForm(form: NgForm) {
    if(form.valid && this.driverRate > 0 && this.vehicleRate > 0)
      console.log(form.value, this.driverRate, this.vehicleRate);
    this.closeRateModal();
  }

  getDriverRateArray(): boolean[] {
    return Array(this.driverRate).fill(true).concat(Array(5 - this.driverRate).fill(false));
  }

  setDriverRate(value: number) {
    this.driverRate = value;
  }

  getVehicleRateArray(): boolean[] {
    return Array(this.vehicleRate).fill(true).concat(Array(5 - this.vehicleRate).fill(false));
  }

  setVehicleRate(value: number) {
    this.vehicleRate = value;
  }

}
