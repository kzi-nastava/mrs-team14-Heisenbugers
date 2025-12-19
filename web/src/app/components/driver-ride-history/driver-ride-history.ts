import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapCaretDownFill, bootstrapCaretUpFill } from '@ng-icons/bootstrap-icons';

interface Ride {
    route: string;
    time: string;
    price: number;
    details: string;
    open?: boolean;
}

@Component({
standalone: true,
  imports: [CommonModule, NgIcon],
  selector: 'app-ride-history',
  templateUrl: './driver-ride-history.html',
  styleUrls: ['./driver-ride-history.css'],
  viewProviders: [provideIcons({ bootstrapCaretDownFill, bootstrapCaretUpFill })]
})
export class RideHistoryComponent {
  sort: 'date' | 'price' | 'route' = 'date';

  rides: Ride[] = Array.from({ length: 5 }).map(() => ({
    route: 'ул.Атамана Головатого 2а → ул.Красная 113',
    time: '08:12-10:12',
    price: 350,
    details: "Neki podaci jos"
  }));

  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }
}
