import { Component, inject } from '@angular/core';

import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {bootstrapCameraFill, bootstrapEye, bootstrapEyeSlash} from '@ng-icons/bootstrap-icons';
import {NgIcon, provideIcons} from '@ng-icons/core';



@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgIcon],
  templateUrl: './login.html',
  styleUrl: './login.css',
  viewProviders: [provideIcons({bootstrapEye,bootstrapEyeSlash})]

})
export class LoginComponent {

  constructor(private router: Router) {

  }

  private fb = inject(FormBuilder);
  activatedMsg = false;
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
    this.router.navigate(['profile'])
    // then there will be a call to the auth service here
  }
}
