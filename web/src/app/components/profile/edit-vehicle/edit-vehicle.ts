import {Component, EventEmitter, inject, input, Input, output, Output} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Vehicle} from '../model/vehicle.model';
import {User} from '../model/user.model';
import {UpdateProfileDTO} from '../../../models/profile.model';
import {CreateVehicleDTO} from '../../../models/driver-registration.model';

@Component({
  selector: 'app-edit-vehicle',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './edit-vehicle.html',
  styleUrl: './edit-vehicle.css',
})
export class EditVehicle {
  closeEdit = output<boolean>();
  saveVehicle = output<CreateVehicleDTO>();

  vehicle = input<Vehicle>();

  cancel() {
    this.closeEdit.emit(false);
  }

  private fb = inject(FormBuilder);

  submitAttempted = false;
  submitted = false;

  form = this.fb.group(
    {
      model: ['', [Validators.required]],
      type: ['', [Validators.required]],
      plateNumber: ['', [Validators.required]],
      seats: ['', [Validators.required]],
      babiesAllowed: [false],
      petsAllowed: [false],
    },
  );

  get f() {
    return this.form.controls;
  }

  ngOnInit() {
    const v = this.vehicle();
    if (!v) return;

    this.form.patchValue({
      model: v.model != null ? String(v.model) : '',
      type: v.type != null ? String(v.type) : '',
      plateNumber: v.plateNumber != null ? String(v.plateNumber) : '',
      seats: v.seats != null ? String(v.seats) : '',
      babiesAllowed: v.babiesAllowed != null ? Boolean(v.babiesAllowed) : false,
      petsAllowed: v.petsAllowed != null ? Boolean(v.petsAllowed) : false,
    });
  }

  isInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }

  submit() {
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted = true;
    console.log('updated vehicle:', this.form.value);

    const dto: CreateVehicleDTO = {
      vehicleModel: this.form.value.model!,
      vehicleType: this.form.value.type!,
      licensePlate: this.form.value.plateNumber!,
      seatCount: Number(this.form.value.seats)!,
      babyTransport: this.form.value.babiesAllowed!,
      petTransport: this.form.value.petsAllowed!,
    };

    this.saveVehicle.emit(dto);

    this.closeEdit.emit(false);
  }
}
