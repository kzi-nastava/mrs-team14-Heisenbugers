import {Component, OnInit, inject, Input, Output, EventEmitter} from '@angular/core'

import {  ReactiveFormsModule,FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RideActionsService } from '../../services/ride-actions.service';
import { CancelRideModalComponent } from './cancel-ride-modal.component';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-start-ride',
  standalone: true,
  imports: [ ReactiveFormsModule],
  templateUrl: './start-ride.component.html',
  styleUrl: './start-ride.component.css',
})
export class StartRideComponent {
  private api = inject(RideActionsService);
  private fb = inject(FormBuilder);

  pins: { lat: number; lng: number; snapToRoad: boolean; popup: string; iconUrl: string }[] = [];

  @Output() pinsChange = new EventEmitter<
    { lat: number; lng: number; snapToRoad: boolean; popup: string; iconUrl: string }[]
  >();

  private emitPins() {
    this.pinsChange.emit([...this.pins]);
  }

  ride: any = null;
  loadingRide = false;

  cancelOpen = false;
  cancelSubmitting = false;
  cancelError: string | null = null;

  cancelForm = this.fb.group({
    reason: ['', [Validators.required, Validators.minLength(3)]],
  });

  constructor(private http: HttpClient,private router: Router) {
  }

  async ngOnInit() {
    this.loadingRide = true;
    try {
      this.ride = await this.api.getMyActiveRide().toPromise();
    } catch (e: any) {
      this.ride = null;
    } finally {
      this.loadingRide = false;
      if(this.ride){
        this.pins.push({ lat: this.ride.start.latitude, lng: this.ride.start.longitude, snapToRoad: true, popup: 'Start', iconUrl: 'icons/pin.svg' });
        this.pins.push({ lat: this.ride.end.latitude, lng: this.ride.end.longitude, snapToRoad: true, popup: 'End', iconUrl: 'icons/pin.svg' });
        for (let i = 0; i < this.ride.stops.length; i++){
          if (i == 0 || i == this.ride.stops.length - 1) continue;
          this.pins.push({ lat: this.ride.stops[i].latitude, lng: this.ride.stops[i].longitude, snapToRoad: true, popup: `Stop ${i+1}`, iconUrl: 'icons/pin.svg' });
        }
        this.emitPins();
      }
    }
  }
  openCancel() {
    this.cancelOpen = true;
    this.cancelError = null;
    this.cancelForm.reset({ reason: '' });
  }


  closeCancel(): void {
    this.cancelOpen = false;
  }

  startRide() {
    if (!this.ride?.rideId && !this.ride?.id) return;

    const rideId = this.ride.rideId ?? this.ride.id;
    this.http.post(
      `http://localhost:8081/api/rides/${this.ride.rideId}/start`,
      {}
    ).subscribe({
      next: () => {
        //after start
        
        window.location.reload();
        // this.router.navigate(['/during-ride', rideId]);
      },
      error: err => {
        console.error('Failed to start ride', err);
      }
    });
  }



  async submitCancel() {
    if (!this.ride?.rideId && !this.ride?.id) return;

    if (this.cancelForm.invalid) {
      this.cancelForm.markAllAsTouched();
      return;
    }

    const rideId = this.ride.rideId ?? this.ride.id;
    const reason = String(this.cancelForm.value.reason);

    this.cancelSubmitting = true;
    this.cancelError = null;

    this.api.cancelRide(rideId, reason).subscribe({
      next: () => {
        this.cancelSubmitting = false;
        this.cancelOpen = false;
        this.ride = null;
      },
      error: (e: any) => {
        this.cancelSubmitting = false;
        this.cancelError = e?.error?.message ?? 'Cancel failed';
      },
    });
  }

}
