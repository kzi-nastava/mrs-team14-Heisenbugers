import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AdminUsersService } from '../admin-users.service';
import { AdminUserListItemDTO } from '../admin-users.model';

import { AdminUserRidesModalComponent } from './admin-user-rides-modal.component';

type Tab = 'PASSENGERS' | 'DRIVERS';

@Component({
  selector: 'app-admin-rides',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminUserRidesModalComponent],
  templateUrl: './admin-rides.component.html',
})
export class AdminRidesComponent implements OnInit {
  private usersApi = inject(AdminUsersService);

  tab: Tab = 'PASSENGERS';

  searchText = '';
  loading = false;
  error: string | null = null;

  passengers: AdminUserListItemDTO[] = [];
  drivers: AdminUserListItemDTO[] = [];

  // modal state
  modalOpen = false;
  selectedUser: AdminUserListItemDTO | null = null;
  backendOrigin = 'http://localhost:8081';

  avatarUrl(url?: string | null): string {
    if (!url) return `${this.backendOrigin}/images/default-avatar.png`;
    if (url.startsWith('http')) return url;
    if (!url.startsWith('/')) url = '/' + url;
    return `${this.backendOrigin}${url}`;
  }

  onAvatarError(e: Event) {
    (e.target as HTMLImageElement).src = `${this.backendOrigin}/images/default-avatar.png`;
  }
  ngOnInit(): void {
    this.loadTab();
  }

  setTab(t: Tab) {
    this.tab = t;
    this.searchText = '';
    this.loadTab();
  }

  private loadTab() {
    this.loading = true;
    this.error = null;

    const req = this.tab === 'PASSENGERS'
      ? this.usersApi.getPassengers()
      : this.usersApi.getDrivers();

    req.subscribe({
      next: (data) => {
        if (this.tab === 'PASSENGERS') this.passengers = data ?? [];
        else this.drivers = data ?? [];
        this.loading = false;
      },
      error: (e) => {
        this.loading = false;
        this.error = e?.error?.message ?? 'Failed to load users';
      }
    });
  }

  get list(): AdminUserListItemDTO[] {
    const src = this.tab === 'PASSENGERS' ? this.passengers : this.drivers;
    const q = this.searchText.trim().toLowerCase();
    if (!q) return src;

    return src.filter(u =>
      (u.fullName ?? '').toLowerCase().includes(q) ||
      (u.email ?? '').toLowerCase().includes(q)
    );
  }

  openUser(u: AdminUserListItemDTO) {
    this.selectedUser = u;
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedUser = null;
  }
}
