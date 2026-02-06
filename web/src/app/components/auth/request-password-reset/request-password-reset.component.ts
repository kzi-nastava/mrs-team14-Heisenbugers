import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router,RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapEnvelope } from '@ng-icons/bootstrap-icons';

@Component({
  selector: 'app-request-password-reset',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './request-password-reset.component.html',
  styleUrl: './request-password-reset.component.css',
  viewProviders: [provideIcons({ bootstrapEnvelope })]
})
export class RequestPasswordResetComponent {

  private fb = inject(FormBuilder);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  submitAttempted = false;
  loading = false;
  done = false;
  errorMsg: string | null = null;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  get f() {
    return this.form.controls;
  }

  isInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }

  submit() {
    this.submitAttempted = true;
    this.errorMsg = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const email = this.form.value.email!;
    this.loading = true;

    this.authService.forgotPassword(email).subscribe({
      next: () => {
        this.loading = false;
        this.done = true;

        // Можно либо оставаться на этой странице,
        // либо сразу редирект на логин:
        this.router.navigate(['auth/login'], { queryParams: { resetRequested: 1 } });
      },
      error: (err) => {
        this.loading = false;
        // Бек всё равно возвращает одно сообщение, так что можно не палить детали
        console.error(err);
        this.errorMsg = 'Something went wrong. Please try again later.';
      }
    });
  }
}
