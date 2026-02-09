import {ChangeDetectorRef, Component, inject} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import {bootstrapEye,bootstrapEyeSlash,bootstrapCameraFill} from '@ng-icons/bootstrap-icons';
import {
  AbstractControl,
  FormBuilder,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {NgIcon, provideIcons} from '@ng-icons/core';

import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../auth.service';
import { RegisterPassengerRequestDTO } from '../auth.api';

function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const p = group.get('password')?.value;
  const c = group.get('confirmPassword')?.value;
  if (!p || !c) return null;
  return p === c ? null : { passwordMismatch: true };
}



const phonePattern = /^[0-9+\-\s()]{6,30}$/;

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgIcon],
  templateUrl: './register.html',
  styleUrl: './register.css',
  viewProviders: [provideIcons({bootstrapEye,bootstrapEyeSlash,bootstrapCameraFill})]
})


export class RegisterComponent {

  imagePreview: string | null = null;
  selectedImageFile: File | null = null;
  submitted = false;
  submitAttempted = false;
  showPassword = false;
  showConfirmPassword = false;

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }


  private auth = inject(AuthService);
  serverError: string | null = null;
  private fb = inject(FormBuilder);

  constructor(private cdr: ChangeDetectorRef) {
  }

  form = this.fb.group(
    {
      firstName: ['', [Validators.required, Validators.maxLength(10)]],
      lastName: ['', [Validators.required, Validators.maxLength(40)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(phonePattern)]],
      address: ['', [Validators.required, Validators.maxLength(120)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatch }
  );

  get f() {
    return this.form.controls;
  }

  get passwordMismatch(): boolean {
    return !!this.form.errors?.['passwordMismatch'] &&
      (this.f.password.touched || this.f.confirmPassword.touched);
  }

  onPickImage(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.selectedImageFile = file;

    const reader = new FileReader();
    reader.onload = () => {this.imagePreview = String(reader.result); this.cdr.markForCheck();};
    reader.readAsDataURL(file);
  }

  isInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }
  get mismatchActive(): boolean {
    return !!this.form.errors?.['passwordMismatch'] &&
      (this.submitAttempted || this.f.password.touched || this.f.confirmPassword.touched);
  }

  submit() {
    console.log('SUBMIT CLICKED', this.form.value, this.form.valid);
    this.submitAttempted = true;
    this.serverError = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formData = new FormData();
    formData.append('email', this.f.email.value!);
    formData.append('password', this.f.password.value!);
    formData.append('confirmPassword', this.f.confirmPassword.value!);
    formData.append('firstName', this.f.firstName.value!);
    formData.append('lastName', this.f.lastName.value!);
    formData.append('phone', this.f.phone.value!);
    formData.append('address', this.f.address.value!);

    if (this.selectedImageFile) {
      formData.append('profileImage', this.selectedImageFile);
    }
    this.auth.register(formData).subscribe({
      next: () => {
        this.submitted = true; // "Activation email sent..."
      },
      error: (err: HttpErrorResponse) => {
        this.submitted = false;
        const msg = err.error?.message ?? 'Registration failed.';

        if (err.status === 409) this.serverError = msg;       // Email already exists
        else if (err.status === 400) this.serverError = msg;  // validation
        else this.serverError = msg;
      }
    });
  }
}
