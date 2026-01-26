import { Component, ElementRef, ViewChild, inject } from '@angular/core';
import { carSelectedIcon, MapComponent } from '../map/map.component';
import { NgIcon, provideIcons } from "@ng-icons/core";
import { bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX } from '@ng-icons/bootstrap-icons';
import { MapPin } from '../map/map.component';
import { FormsModule, NgForm } from '@angular/forms';
import { RateModal } from "../rate-modal/rate-modal.component";
import { RideInfo } from '../driver-ride-history/driver-info.model';
import { PanicService } from '../../services/panic.service';

@Component({
  selector: 'app-during-ride',
  imports: [MapComponent, NgIcon, FormsModule, RateModal],
  templateUrl: './during-ride.component.html',
  viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX })]

})
export class DuringRideComponent {

  private panicApi = inject(PanicService);
  @ViewChild('noteFocus') noteFocus!: ElementRef<HTMLInputElement>;

  ride: any;
/*
  ride: RideInfo = {
    rideId: 'ride-' + Math.random().toString(36).substr(2, 9),
    driverName: 'Vozac Vozacovic',
    startAddress: 'ул.Атамана Головатого 2а',
    endAddress: 'ул.Красная 113',
    startedAt: new Date('2025-12-19T08:12:00'),
    endedAt: new Date('2025-12-19T10:12:00'),
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
    trafficViolations: [{type: 'Red light'}],
    panicTriggered: false

  };*/
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

  async panicClick() {
    const rideId = this.ride?.rideId;
    if (!rideId) return;

    const msg = prompt('Describe the problem (optional):') ?? '';
    this.panicApi.panic(String(rideId), msg).subscribe({
      next: () => alert('Panic sent to administrators.'),
      error: (e) => alert(e?.error?.message ?? 'Panic failed')
    });
  }

}
