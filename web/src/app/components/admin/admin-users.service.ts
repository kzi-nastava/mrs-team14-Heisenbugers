import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminUserListItemDTO } from './admin-users.model';

@Injectable({ providedIn: 'root' })
export class AdminUsersService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8081/api/admin/users';

  getDrivers(): Observable<AdminUserListItemDTO[]> {
    return this.http.get<AdminUserListItemDTO[]>(`${this.base}/drivers`);
  }

  getPassengers(): Observable<AdminUserListItemDTO[]> {
    return this.http.get<AdminUserListItemDTO[]>(`${this.base}/passengers`);
  }
}
