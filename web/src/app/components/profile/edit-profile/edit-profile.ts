import {ChangeDetectorRef, Component, inject, input, output} from '@angular/core';
import {NgIcon} from "@ng-icons/core";
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {User} from '../model/user.model';

@Component({
  selector: 'app-edit-profile',
  imports: [
    NgIcon,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css',
})
export class EditProfile {
  closeEdit = output<boolean>()
  saveProfile = output<User>();

  user = input<User>();

  cancel() {
    this.closeEdit.emit(false);
  }

  private fb = inject(FormBuilder);

  submitAttempted = false;
  submitted = false;

  imagePreview: string | null = null;

  form = this.fb.group(
    {
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9+\-\s()]{6,30}$/)]],

    },
  );

  get f() {
    return this.form.controls;
  }

  ngOnInit() {
    const u = this.user();
    if (!u) return;

    this.form.patchValue({
      name: u.name != null ? String(u.name) : '',
      email: u.email != null ? String(u.email) : '',
      address: u.address != null ? String(u.address) : '',
      phoneNumber: u.phoneNumber != null ? String(u.phoneNumber) : '',
    });

    this.imagePreview = u.profilePicture ?? null;
  }

  isInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || this.submitAttempted);
  }

  submit() {
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted = true;
    console.log('updated profile:', this.form.value);

    this.saveProfile.emit({
      ...this.form.value,
      profilePicture: this.imagePreview,
    } as User);

    this.closeEdit.emit(false);
  }

  constructor(private cdr: ChangeDetectorRef) {
  }

  onPickImage(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }
}
