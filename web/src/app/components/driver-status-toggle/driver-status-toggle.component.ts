import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { UserStateDTO } from '../../models/user-state.model';
import { DriverWorkingDTO } from '../../models/driver-working.model';
import { IsBlockedDTO } from '../../models/is-blocked.model';

@Component({
  selector: 'app-driver-status-toggle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-status-toggle.component.html',
})
export class DriverStatusToggleComponent implements OnInit {
  private baseUrl = 'http://localhost:8081/api';

  loading = true;
  saving = false;
  error: string | null = null;

  userState: UserStateDTO | null = null;
  working = false;

  isBlocked = false;
  blockNote: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadState();
  }


  private loadState(): void {
    this.loading = true;
    this.error = null;

    // 1) грузим state
    this.http.get<UserStateDTO>(`${this.baseUrl}/users/state`).subscribe({
      next: (state) => {
        this.userState = state;

        // 2) грузим blocked
        this.http.get<IsBlockedDTO>(`${this.baseUrl}/users/is-blocked`).subscribe({
          next: (b) => {
            this.isBlocked = !!b.blocked;
            this.blockNote = (b.blockNote ?? null);

            // 3) грузим working (даже если blocked — чтобы показать текущий статус)
            this.http.get<DriverWorkingDTO>(`${this.baseUrl}/driver/me/working`).subscribe({
              next: (w) => {
                this.working = !!w.working;
                this.loading = false;
              },
              error: () => {
                this.working = true;
                this.loading = false;
              },
            });
          },
          error: () => {
            // если не смогли узнать blocked — лучше не ломать экран
            this.isBlocked = false;
            this.blockNote = null;

            this.http.get<DriverWorkingDTO>(`${this.baseUrl}/driver/me/working`).subscribe({
              next: (w) => {
                this.working = !!w.working;
                this.loading = false;
              },
              error: () => {
                this.working = true;
                this.loading = false;
              },
            });
          }
        });
      },
      error: () => {
        this.error = 'Failed to load user state';
        this.loading = false;
      },
    });
  }

  get disabled(): boolean {
    if (!this.userState) return true;
    return (
      this.userState.state === 'DRIVING' ||
      this.userState.state === 'STARTING' ||
      this.saving
    );
  }

  get isActive(): boolean {
    return this.isBlocked ? false : this.working;
  }

  get label(): string {
    return this.isActive ? 'Active' : 'Inactive';
  }

  get hint(): string {
    if (!this.userState) return '';


    if (this.isBlocked) {
      return this.blockNote
        ? `You are blocked: ${this.blockNote}`
        : 'You are blocked and cannot become active';
    }

    if (this.userState.state === 'DRIVING') return 'You are in an ongoing ride';
    if (this.userState.state === 'STARTING') return 'You have an assigned ride';

    return this.isActive
      ? 'You are available for new rides'
      : 'You are not available for new rides';
  }

  toggle(): void {
    if (this.disabled || this.isBlocked) return;

    const newValue = !this.working;
    this.saving = true;
    this.error = null;

    this.http
      .put<DriverWorkingDTO>(`${this.baseUrl}/driver/me/working`, { working: newValue })
      .subscribe({
        next: (resp) => {
          this.working = !!resp.working;
          this.saving = false;
        },
        error: (err: HttpErrorResponse) => {

          if (err.status === 403) {
            this.error = 'You are blocked and cannot change your status';

            this.http.get<IsBlockedDTO>(`${this.baseUrl}/users/is-blocked`).subscribe({
              next: (b) => {
                this.isBlocked = !!b.blocked;
                this.blockNote = (b.blockNote ?? null);
              }
            });
          } else {
            this.error = 'Failed to change status';
          }
          this.saving = false;
        },
      });
  }
}
