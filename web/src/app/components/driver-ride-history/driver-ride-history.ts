import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Ride {
  route: string;
  time: string;
  price: number;
}

@Component({
standalone: true,
  imports: [CommonModule],
  selector: 'app-ride-history',
  templateUrl: './driver-ride-history.html',
  styleUrls: ['./driver-ride-history.css']
})
export class RideHistoryComponent {
  sort: 'date' | 'price' | 'route' = 'date';

  rides: Ride[] = Array.from({ length: 5 }).map(() => ({
    route: 'ул.Атамана Головатого 2а → ул.Красная 113',
    time: '08:12-10:12',
    price: 350
  }));

  setSort(type: 'date' | 'price' | 'route') {
    this.sort = type;
  }
}
