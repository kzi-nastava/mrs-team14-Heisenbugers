import {ChangeDetectorRef, Component, inject} from '@angular/core';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {bootstrapCameraFill} from '@ng-icons/bootstrap-icons';
import {RegisterPassengerRequestDTO} from '../auth.api';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {CreateDriverDTO, CreateVehicleDTO} from '../../../models/driver-registration.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-driver-registration',
  imports: [
    FormsModule,
    NgIcon,
    ReactiveFormsModule,
  ],
  templateUrl: './driver-registration.component.html',
  styleUrl: './driver-registration.component.css',
  viewProviders: [provideIcons({bootstrapCameraFill})]
})
export class DriverRegistrationComponent {
  imagePreview: string | null = null;

  submitted = false;
  submitAttempted = false;

  private fb = inject(FormBuilder);

  form = this.fb.group(
    {
      firstName: ['', [Validators.required, Validators.maxLength(10)]],
      lastName: ['', [Validators.required, Validators.maxLength(40)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+\-\s()]{6,30}$/)]],
      address: ['', [Validators.required, Validators.maxLength(120)]],
      model: ['', [Validators.required]],
      type: [null, [Validators.required]],
      plateNumber: ['', [Validators.required]],
      seats: ['', [Validators.required]],
      babiesAllowed: [false],
      petsAllowed: [false],
    },
  );

  get f() {
    return this.form.controls;
  }

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) {

  }

  isInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }

  private base64ToFile(base64: string, filename: string): File {
    const arr = base64.split(',');
    const mime = arr[0].match(/:(.*?);/)![1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);

    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }

    return new File([u8arr], filename, { type: mime });
  }

  submit() {
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    console.log(this.f.type.value)

    const vehicleDTO: CreateVehicleDTO = {
      vehicleModel: this.f.model.value!,
      vehicleType: this.f.type.value!,
      licensePlate: this.f.plateNumber.value!,
      seatCount: Number(this.f.seats.value!),
      babyTransport: Boolean(this.f.babiesAllowed.value!),
      petTransport: Boolean(this.f.petsAllowed.value!),
    }



    const driverDTO: CreateDriverDTO = {
      email: this.f.email.value!,
      firstName: this.f.firstName.value!,
      lastName: this.f.lastName.value!,
      phone: this.f.phone.value!,
      address: this.f.address.value!,
      profileImageUrl: this.imagePreview,
      vehicle: vehicleDTO,
    };

    const formData = new FormData();
    if (this.imagePreview != null) {
      const image = this.imagePreview
      driverDTO.profileImageUrl = null;
      formData.append(
        'data',
        new Blob([JSON.stringify(driverDTO)], { type: 'application/json' })
      );
      const file = this.base64ToFile(image, 'profile.png');
      formData.append('image', file);
    }else{
      formData.append(
        'data',
        new Blob([JSON.stringify(driverDTO)], { type: 'application/json' })
      );
    }

    this.http.post<CreateDriverDTO>("http://localhost:8081/api/drivers", formData).subscribe({
      next: () => {
        this.submitted = true; // "Activation email sent..."
      },
      error: (err: HttpErrorResponse) => {
        this.submitted = false;
        const msg = err.error?.message ?? 'Registration failed.';

        /*if (err.status === 409) this.serverError = msg;       // Email already exists
        else if (err.status === 400) this.serverError = msg;  // validation
        else this.serverError = msg;*/
      }
    });
  }


  onPickImage(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }
}
