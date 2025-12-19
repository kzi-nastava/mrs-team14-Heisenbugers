import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
    bootstrapCaretDownFill,
    bootstrapCaretUpFill,
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash }
    from '@ng-icons/bootstrap-icons';

interface Ride {
    beginSpot: string;
    endSpot: string;
    beginTime: Date;
    endTime: Date;
    price: number;
    panic: boolean;
    notes: string;
    open?: boolean;
}

interface Passenger {
    firstName: string;
    lastName: string;
}

@Component({
standalone: true,
  imports: [CommonModule, NgIcon],
  selector: 'app-ride-history',
  templateUrl: './driver-ride-history.html',
  styleUrls: ['./driver-ride-history.css'],
  viewProviders: [provideIcons({ bootstrapCaretDownFill,
    bootstrapCaretUpFill,
    bootstrapArrowRight,
    bootstrapClock,
    bootstrapCash
    })]
})
export class RideHistoryComponent {
  sort: 'date' | 'price' | 'route' = 'date';

  rides: Ride[] = Array.from({ length: 5 }).map(() => ({
    beginSpot: 'ул.Атамана Головатого 2а',
    endSpot: 'ул.Красная 113',
    beginTime: new Date('2025-12-19T08:12:00'),
    endTime: new Date('2025-12-19T10:12:00'),
    price: 350,
    panic: false,
    notes: "Neki podaci jos"
  }));

  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }
}
