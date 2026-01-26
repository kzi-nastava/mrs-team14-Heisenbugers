import { Component, inject } from '@angular/core';

import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {bootstrapCameraFill, bootstrapEye, bootstrapEyeSlash} from '@ng-icons/bootstrap-icons';
import {NgIcon, provideIcons} from '@ng-icons/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../auth.service';
import { LoginRequestDTO } from '../auth.api';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgIcon],
  templateUrl: './login.html',
  styleUrl: './login.css',
  viewProviders: [provideIcons({bootstrapEye,bootstrapEyeSlash})]

})
export class LoginComponent {

  constructor(private router: Router, private authService: AuthService) {

  }

  private fb = inject(FormBuilder).nonNullable;
  private auth = inject(AuthService);

  activatedMsg = false;
  submitAttempted = false;
  showPassword = false;

  loading = false;
  serverError: string | null = null;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  get f() {
    return this.form.controls;
  }



  isInvalid(name: 'email' | 'password'): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  submit() {
    this.submitAttempted = true;
    this.serverError = null;
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    /*const dto = {
      email: this.form.value.email,
      password: this.form.value.password,
    }*/

    this.loading = true;
    const dto: LoginRequestDTO = this.form.getRawValue();
    this.auth.login(dto).subscribe({
      next: (resp) => {
        this.loading = false;

        localStorage.setItem('accessToken', resp.accessToken);
        localStorage.setItem('tokenType', resp.tokenType);
        localStorage.setItem('userId', resp.userId);
        localStorage.setItem('role', resp.role);

        this.authService.setUser();

        this.router.navigate(['/base']);

      },
      error: (e:HttpErrorResponse) => {
        this.loading = false;

        const msg = e.error?.message;
        if (e.status === 401) this.serverError = msg ?? 'Invalid credentials.';
        else if (e.status === 403) this.serverError = msg ?? 'Access forbidden.';
        else this.serverError = msg ?? 'Server error. Try again later.';
      },
    });

  }
}
