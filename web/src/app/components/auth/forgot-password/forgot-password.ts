import {Component, inject} from '@angular/core';

import { ReactiveFormsModule } from '@angular/forms';
import {bootstrapCameraFill, bootstrapEye, bootstrapEyeSlash} from '@ng-icons/bootstrap-icons';
import {NgIcon, provideIcons} from '@ng-icons/core';
import { FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';

import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth.service'

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

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    this.token = this.route.snapshot.queryParamMap.get('token');
  }

  private fb = inject(FormBuilder);

  submitAttempted = false;
  submitted = false;

  showPassword = false;
  showConfirmPassword = false;
  token: string | null = null;

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
    if (this.form.invalid || !this.token) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted = true;

    const newPassword = this.form.value.password!;
    const confirmPassword = this.form.value.confirmPassword!;

    this.authService.resetPassword(this.token, newPassword, confirmPassword)
      .subscribe({
        next: () => {
          // TODO: можно вставить snackbar/toast
          this.router.navigate(['auth/login'], { queryParams: { resetSuccess: 1 } });
        },
        error: (err) => {
          this.submitted = false;
          // TODO: показать ошибку пользователю
          console.error(err);
        }
      });
  }
}
