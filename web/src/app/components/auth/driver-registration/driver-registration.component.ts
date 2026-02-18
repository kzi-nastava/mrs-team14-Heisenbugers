import {ChangeDetectorRef, Component, inject} from '@angular/core';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {bootstrapCameraFill} from '@ng-icons/bootstrap-icons';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {DriverRegistrationService} from '../../../services/driver-registration.service';

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

  private registrationService = inject(DriverRegistrationService);
  private fb = inject(FormBuilder);

  form = this.fb.group(
    {
      firstName: ['', [Validators.required, Validators.maxLength(10)]],
      lastName: ['', [Validators.required, Validators.maxLength(40)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+\-\s()]{6,30}$/)]],
      address: ['', [Validators.required, Validators.maxLength(120)]],
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

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) {

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

    this.registrationService.registerDriver(this.form.value, this.imagePreview).subscribe({
      next: () => {
        this.submitted = true;
      },
      error: (err: HttpErrorResponse) => {
        this.submitted = false;
        const msg = err.error?.message ?? 'Registration failed.';
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
