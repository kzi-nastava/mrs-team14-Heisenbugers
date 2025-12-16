import { Component, inject } from '@angular/core';

import { NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';




@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  private fb = inject(FormBuilder);

  submitAttempted = false;
  showPassword = false;

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
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    console.log(this.form.value);
    // then there will be a call to the auth service here
  }
}
