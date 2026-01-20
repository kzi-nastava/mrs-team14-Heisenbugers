import { Component } from '@angular/core';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {bootstrapPinMapFill, bootstrapPlusSquare} from '@ng-icons/bootstrap-icons';

@Component({
  selector: 'app-ride-booking',
  imports: [
    NgIcon
  ],
  templateUrl: './ride-booking.component.html',
  styleUrl: './ride-booking.component.css',
  viewProviders: [provideIcons({bootstrapPinMapFill, bootstrapPlusSquare})]
})
export class RideBookingComponent {

}
