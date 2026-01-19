import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { bootstrapArrowRight, bootstrapClock } from '@ng-icons/bootstrap-icons';

import { NgIcon, provideIcons } from "@ng-icons/core";

@Component({
  selector: 'app-scheduled-rides',
  imports: [NgIcon, CommonModule],
  templateUrl: './scheduled-rides.component.html',
  viewProviders: [provideIcons({bootstrapArrowRight, bootstrapClock})]
})
export class ScheduledRides {
  rides = [
    { date: '2024-07-01', time: new Date('2025-12-19T08:12:00'), startLocation: '123 Main St', finishLocation: '456 Oak Ave' },
    { date: '2024-07-02', time: new Date('2025-12-19T08:12:00'), startLocation: '789 Pine Rd', finishLocation: '321 Maple Ln' },
    { date: '2024-07-03', time: new Date('2025-12-19T08:12:00'), startLocation: '654 Cedar St', finishLocation: '987 Birch Blvd' },
  ];

}
