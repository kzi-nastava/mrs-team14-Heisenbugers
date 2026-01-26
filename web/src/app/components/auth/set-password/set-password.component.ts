import {ChangeDetectorRef, Component, inject} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {NgIcon, provideIcons} from "@ng-icons/core";
import {Router, ActivatedRoute} from '@angular/router';
import {bootstrapEye, bootstrapEyeSlash} from '@ng-icons/bootstrap-icons';
import {HttpClient} from '@angular/common/http';
import {CreateVehicleDTO, SetDriverPasswordDTO} from '../../../models/driver-registration.model';

function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const p = group.get('password')?.value;
  const c = group.get('confirmPassword')?.value;
  if (!p || !c) return null;
  return p === c ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-set-password',
    imports: [
        FormsModule,
        NgIcon,
        ReactiveFormsModule
    ],
  templateUrl: './set-password.component.html',
  styleUrl: './set-password.component.css',
  viewProviders: [provideIcons({bootstrapEye,bootstrapEyeSlash})]
})
export class SetPasswordComponent {
  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef, private route: ActivatedRoute) {

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

    const passwordDTO: SetDriverPasswordDTO = {
      password: this.f.password.value!,
      confirmPassword: this.f.confirmPassword.value!,
    }

    const token = this.route.snapshot.queryParamMap.get('token') || '';

    if (!token) {
      console.error('No token provided in URL for set-password');
      return;
    }

    const url = `http://localhost:8081/api/drivers/password?token=${encodeURIComponent(token)}`;

    this.http.put<SetDriverPasswordDTO>(url, passwordDTO)
      .subscribe({
        next: updated => {
          this.submitted = true;
          this.router.navigate(['auth/login']);
        },
        error: err => {
          console.error('Failed to set password', err);
        }
      });
  }
}
