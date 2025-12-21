import {ChangeDetectorRef, Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {NgIcon} from "@ng-icons/core";
import {
  AbstractControl,
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';

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
  @Output() closeEdit = new EventEmitter<boolean>();

  @Input() user!: {
    name: string;
    email: string;
    address: string;
    phoneNumber: string;
  };

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
    if (this.user) {
      this.form.patchValue(this.user);
    }
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
