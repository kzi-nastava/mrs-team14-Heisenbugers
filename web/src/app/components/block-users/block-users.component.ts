import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {BlockableUserDTO} from '../../models/users.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-block-users',
    imports: [
        CommonModule,
        FormsModule
    ],
  templateUrl: './block-users.component.html',
  styleUrl: './block-users.component.css',
})
export class BlockUsersComponent implements OnInit {

  users: BlockableUserDTO[] = [];
  loading = false;
  error: string | null = null;

  searchTerm: string = '';
  roleFilter: string = 'ALL';
  roleOptions: string[] = ['ALL'];
  selectedUser: BlockableUserDTO | null = null;
  infoMessage: string | null = null;
  blocking = false;
  modalNote: string = '';

  private baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient, private cd: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.error = null;
    this.http.get<BlockableUserDTO[]>(`${this.baseUrl}/users/blockable`).subscribe({
      next: (data) => {
        this.users = Array.isArray(data) ? data : [];
        const roles = Array.from(new Set(this.users.map(u => u.role || 'UNKNOWN'))).filter(r => r != null);
        this.roleOptions = ['ALL', ...roles];
        this.loading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load users', err);
        this.error = 'Failed to load users';
        this.loading = false;
        this.cd.detectChanges();
      }
    });
  }

  get filteredUsers(): BlockableUserDTO[] {
    const q = (this.searchTerm || '').trim().toLowerCase();
    return this.users.filter(u => {
      const matchesSearch = !q || (u.firstName || '').toLowerCase().includes(q) || (u.lastName || '').toLowerCase().includes(q) || (u.email || '').toLowerCase().includes(q);
      const matchesRole = this.roleFilter === 'ALL' || (u.role || '').toLowerCase() === (this.roleFilter || '').toLowerCase();
      return matchesSearch && matchesRole;
    });
  }

  openBlockModal(user: BlockableUserDTO) {
    this.selectedUser = user;
    this.infoMessage = null;
    this.modalNote = '';
  }

  closeModal() {
    this.selectedUser = null;
    this.blocking = false;
    this.modalNote = '';
  }

  confirmBlock() {
    if (!this.selectedUser) return;
    this.blocking = true;
    this.error = null;

    const isCurrentlyBlocked = this.selectedUser.blocked;
    const action = isCurrentlyBlocked ? 'unblock' : 'block';

    const body = action === 'block' ? this.modalNote : null;
    this.http.post(`${this.baseUrl}/users/${this.selectedUser.id}/${action}`, body).subscribe({
      next: () => {
        this.selectedUser!.blocked = !isCurrentlyBlocked;
        this.infoMessage = `User ${this.selectedUser!.email} has been ${isCurrentlyBlocked ? 'unblocked' : 'blocked'}.`;
        const idx = this.users.findIndex(u => u.id === this.selectedUser!.id);
        if (idx !== -1) this.users[idx].blocked = this.selectedUser!.blocked;
        this.blocking = false;
        this.closeModal();
        setTimeout(() => this.infoMessage = "", 900);
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error(`Failed to ${action} user`, err);
        this.error = `Failed to ${action} user`;
        this.blocking = false;
        this.cd.detectChanges();
      }
    });
  }
}
