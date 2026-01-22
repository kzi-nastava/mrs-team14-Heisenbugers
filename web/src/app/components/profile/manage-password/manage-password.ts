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
import {ChangePasswordDTO} from '../../../models/profile.model';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';

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
  errorMessage: string | null = null;
  successMessage: string | null = null;

  showPassword = false;
  showConfirmPassword = false;
  showOldPassword = false;

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) {

  }

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
    this.errorMessage = null;
    this.successMessage = null;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const dto: ChangePasswordDTO = {
      oldPassword: this.form.value.oldPassword!,
      newPassword: this.form.value.password!,
    };

    this.http.put<void>('http://localhost:8081/api/profile/me/password', dto)
      .subscribe({
        next: () => {
          this.submitted = true;
          this.form.reset();
          this.submitAttempted = false;
          this.successMessage = 'Password successfully changed';
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.message ?? 'Failed to change password';
          this.cdr.markForCheck();
        }
      });
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

}
