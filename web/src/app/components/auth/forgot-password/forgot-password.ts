import {Component, inject} from '@angular/core';

import { ReactiveFormsModule } from '@angular/forms';
import {bootstrapCameraFill, bootstrapEye, bootstrapEyeSlash} from '@ng-icons/bootstrap-icons';
import {NgIcon, provideIcons} from '@ng-icons/core';
import { FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';

function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const p = group.get('password')?.value;
  const c = group.get('confirmPassword')?.value;
  if (!p || !c) return null;
  return p === c ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-forgot-password',
  imports: [
    ReactiveFormsModule,
    NgIcon
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
  viewProviders: [provideIcons({bootstrapEye,bootstrapEyeSlash})]
})
export class ForgotPasswordComponent {

  constructor(private router: Router) {
    
  }

  private fb = inject(FormBuilder);

  submitAttempted = false;
  submitted = false;

  showPassword = false;
  showConfirmPassword = false;

  form = this.fb.group(
    {
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatch }
  );

  get f() {
    return this.form.controls;
  }

  isInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }
  get mismatchActive(): boolean {
    return !!this.form.errors?.['passwordMismatch'] &&
      (this.submitAttempted || this.f.password.touched || this.f.confirmPassword.touched);
  }

  get passwordMismatch(): boolean {
    return !!this.form.errors?.['passwordMismatch'] &&
      (this.f.password.touched || this.f.confirmPassword.touched);
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  submit() {
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted = true;
    console.log('new password:', this.form.value.password);

    this.router.navigate(['auth/login'])
  }
}
