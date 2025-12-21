import {ChangeDetectorRef, Component, inject} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {NgIcon} from "@ng-icons/core";

@Component({
  selector: 'app-manage-password',
  imports: [
    FormsModule,
    NgIcon,
    ReactiveFormsModule
  ],
  templateUrl: './manage-password.html',
  styleUrl: './manage-password.css',
})
export class ManagePassword {
  private fb = inject(FormBuilder);

  submitAttempted = false;
  submitted = false;

  selectedTab: string = 'Personal Information';

  showPassword = false;
  showConfirmPassword = false;
  showOldPassword = false;

  passwordsMatch(group: AbstractControl): ValidationErrors | null {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;
    if (!p || !c) return null;
    return p === c ? null : { passwordMismatch: true };
  }

  form = this.fb.group(
    {
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      oldPassword: ['', [Validators.required]],
    },
    { validators: this.passwordsMatch }
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

  submit() {
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted = true;
    console.log('new password:', this.form.value.password);
  }

  toggleOldPassword() {
    this.showOldPassword = !this.showOldPassword;
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  constructor() {
  }

}
