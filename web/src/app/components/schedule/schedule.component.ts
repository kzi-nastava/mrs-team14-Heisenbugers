import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './schedule.component.html',
})
export class ScheduleComponent {
  @Output() scheduled = new EventEmitter<string>();
  @Output() cancel = new EventEmitter<void>();

  min = this.toDateTimeLocal(new Date());
  max = this.toDateTimeLocal(new Date(Date.now() + 5 * 60 * 60 * 1000));
  value: string = this.roundToMinutes(new Date(), 15);
  error: string | null = null;

  private pad(n: number) { return n < 10 ? '0' + n : '' + n; }
  private toDateTimeLocal(d: Date) {
    return d.getFullYear() + '-' + this.pad(d.getMonth() + 1) + '-' + this.pad(d.getDate()) + 'T' + this.pad(d.getHours()) + ':' + this.pad(d.getMinutes());
  }

  private roundToMinutes(d: Date, minutes: number) {
    const ms = 1000 * 60 * minutes;
    const rounded = new Date(Math.ceil(d.getTime() / ms) * ms);
    return this.toDateTimeLocal(rounded);
  }

  confirm() {
    const selected = new Date(this.value);
    const now = new Date();
    const maxDt = new Date(Date.now() + 5 * 60 * 60 * 1000);
    if (selected < now) {
      this.error = 'Selected time must be in the future';
      return;
    }
    if (selected > maxDt) {
      this.error = 'You can schedule only up to 5 hours in advance';
      return;
    }
    this.error = null;
    this.scheduled.emit(selected.toISOString());
  }

  doCancel() {
    this.cancel.emit();
  }
}
