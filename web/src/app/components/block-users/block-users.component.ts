import {ChangeDetectorRef, Component, EventEmitter, Output} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {NgIcon} from "@ng-icons/core";
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {LocationDTO} from '../../models/ride.model';
import {BlockableUserDTO} from '../../models/users.model';

@Component({
  selector: 'app-block-users',
    imports: [
        NgIcon
    ],
  templateUrl: './block-users.component.html',
  styleUrl: './block-users.component.css',
})
export class BlockUsersComponent {

  users: BlockableUserDTO[] = [];
  loading = false;
  error: string | null = null;

  private baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient, private cd: ChangeDetectorRef, private router: Router) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.error = null;
    this.http.get<BlockableUserDTO[]>(`${this.baseUrl}/users/blockable`).subscribe({
      next: (data) => {
        this.users = Array.isArray(data) ? data : [];
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

  // useRoute(route: FavoriteRoute) {
  //   this.routeSelected.emit(route);
  //   this.router.navigate(['/base'], {
  //     state: { favoriteRoute: route }
  //   });
  //   console.log('Use favorite route', route);
  // }
  //
  // removeFavorite(route: FavoriteRoute) {
  //   if (!confirm('Remove this route from favorites?')) return;
  //   this.loading = true;
  //   this.http.delete(`${this.baseUrl}/favorite-routes/${route.Id}`).subscribe({
  //     next: () => {
  //       this.favorites = this.favorites.filter(r => r.Id !== route.Id);
  //       this.loading = false;
  //       this.cd.detectChanges();
  //     },
  //     error: (err) => {
  //       console.error('Failed to remove favorite', err);
  //       this.error = 'Failed to remove favorite route';
  //       this.loading = false;
  //     }
  //   });
  // }

  formatMinutes(minutes?: number | null): string {
    if (minutes == null || isNaN(minutes as any)) return '-';
    const m = Math.round(minutes);
    const h = Math.floor(m / 60);
    const rem = m % 60;
    return h > 0 ? `${h} H ${rem} min` : `${rem} min`;
  }
}
