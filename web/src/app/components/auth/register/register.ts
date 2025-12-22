import { Component,inject } from '@angular/core';
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



  private fb = inject(FormBuilder);



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

    const reader = new FileReader();
    reader.onload = () => (this.imagePreview = String(reader.result));
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
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }


    this.submitted = true;
    // toast/snackbar
  }
}
