import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";

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
  @Output() closeEdit = new EventEmitter<boolean>();

  @Input() vehicle!: {
    model: string;
    type: string;
    plateNumber: string;
    seats: string;
    babiesAllowed: boolean;
    petsAllowed: boolean;
  };

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
    if (this.vehicle) {
      this.form.patchValue(this.vehicle);
    }
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
  }
}
