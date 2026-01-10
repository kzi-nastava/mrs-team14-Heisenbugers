import { Component, ElementRef, ViewChild } from '@angular/core';
import { carSelectedIcon, MapComponent } from '../components/map/map.component';
import { NgIcon, provideIcons } from "@ng-icons/core";
import { bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather } from '@ng-icons/bootstrap-icons';
import { MapPin } from '../components/map/map.component';
import { FormsModule, NgForm } from '@angular/forms';


@Component({
  selector: 'app-during-ride',
  imports: [MapComponent, NgIcon, FormsModule],
  templateUrl: './during-ride.component.html',
  viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather })]

})
export class DuringRide {
  NotesIsOpen: boolean = true;
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
    this.NotesIsOpen = true;
  }

  submitForm() {
    console.log("Form submitted");
    this.closeModal();
  }

}
