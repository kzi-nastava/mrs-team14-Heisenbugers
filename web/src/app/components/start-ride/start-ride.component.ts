import { Component, OnInit, inject } from '@angular/core'

import {  ReactiveFormsModule,FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RideActionsService } from '../../services/ride-actions.service';
import { CancelRideModalComponent } from './cancel-ride-modal.component';
import { HttpClient } from '@angular/common/http';


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

  ride: any = null;
  loadingRide = false;

  cancelOpen = false;
  cancelSubmitting = false;
  cancelError: string | null = null;

  cancelForm = this.fb.group({
    reason: ['', [Validators.required, Validators.minLength(3)]],
  });


  async ngOnInit() {
    this.loadingRide = true;
    try {
      this.ride = await this.api.getMyActiveRide().toPromise();
    } catch (e: any) {
      this.ride = null;
    } finally {
      this.loadingRide = false;
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

  startRide() {}


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

    try {
      await this.api.cancelRide(rideId, reason).toPromise();
      this.cancelOpen = false;

      // можно убрать карточку заказа
      this.ride = null;
    } catch (e: any) {
      this.cancelError = e?.error?.message ?? 'Cancel failed';
    } finally {
      this.cancelSubmitting = false;
    }
  }

}
