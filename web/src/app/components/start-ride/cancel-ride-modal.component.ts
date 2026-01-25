import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-cancel-ride-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <!-- overlay -->
    <div class="fixed inset-0 z-[999] bg-black/40 flex items-center justify-center p-6">
      <!-- modal -->
      <div class="w-full max-w-[520px] rounded-2xl bg-white shadow-xl border border-neutral-200 p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-[18px] font-bold">Cancel ride</h2>
          <button
            type="button"
            class="h-9 w-9 rounded-full hover:bg-neutral-100 text-neutral-600"
            (click)="close.emit()"
            aria-label="Close"
          >✕</button>
        </div>

        <p class="text-[12px] text-neutral-500 mb-3">
          Please provide a reason for canceling this ride.
        </p>

        <form [formGroup]="form" (ngSubmit)="submit()">
          <textarea
            formControlName="reason"
            rows="4"
            class="w-full rounded-[12px] border border-neutral-200 bg-white px-4 py-3 shadow-sm
                   focus:outline-none focus:ring-4 focus:ring-lime-200"
            [class.border-red-400]="isInvalid"
            [class.focus:ring-red-200]="isInvalid"
            placeholder="e.g. Passenger not at pickup / health issue / ..."
          ></textarea>

          @if (isInvalid) {
            <p class="mt-1 text-xs text-red-500">Reason is required (min 5 chars).</p>
          }

          <div class="flex gap-3 justify-end mt-5">
            <button
              type="button"
              class="h-[40px] px-5 rounded-[10px] border border-neutral-300 bg-white text-black font-semibold
                     hover:bg-neutral-100"
              (click)="close.emit()"
              [disabled]="loading"
            >
              Back
            </button>

            <button
              type="submit"
              class="h-[40px] px-5 rounded-[10px] bg-[#C5E55D] text-black font-semibold shadow
                     hover:bg-lime-200 active:scale-[0.99]"
              [disabled]="loading"
            >
              @if (loading) { <span>Sending...</span> } @else { <span>Confirm cancel</span> }
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class CancelRideModalComponent {
  private fb = inject(FormBuilder);

  @Input() loading = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<string>();

  submitAttempted = false;

  form = this.fb.group({
    reason: ['', [Validators.required, Validators.minLength(5)]],
  });

  get isInvalid() {
    const c = this.form.controls.reason;
    return c.invalid && (c.touched || this.submitAttempted);
  }

  submit() {
    this.submitAttempted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.confirm.emit(String(this.form.value.reason).trim());
  }
}
