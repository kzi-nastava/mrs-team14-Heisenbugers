import {Component, OnInit, Output, EventEmitter, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {LocationDTO} from '../../models/ride.model';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {
  bootstrapTrash3Fill
} from '@ng-icons/bootstrap-icons';
import {Router} from '@angular/router';

interface FavoriteRoute {
  Id: string | number;
  name?: string;
  startAddress?: LocationDTO;
  endAddress?: LocationDTO;
  distanceKm?: number;
  timeMin?: number;
}

@Component({
  selector: 'app-favorite-routes',
  standalone: true,
  imports: [CommonModule, NgIcon],
  templateUrl: './favorite-routes.component.html',
  styleUrls: ['./favorite-routes.component.css'],
  viewProviders: [provideIcons({ bootstrapTrash3Fill })]
})
export class FavoriteRoutesComponent implements OnInit {
  @Output() routeSelected = new EventEmitter<FavoriteRoute>();

  favorites: FavoriteRoute[] = [];
  loading = false;
  error: string | null = null;

  private baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient, private cd: ChangeDetectorRef, private router: Router) { }

  ngOnInit(): void {
    this.loadFavorites();
  }

  loadFavorites() {
    this.loading = true;
    this.error = null;
    this.http.get<FavoriteRoute[]>(`${this.baseUrl}/favorite-routes`).subscribe({
      next: (data) => {
        this.favorites = Array.isArray(data) ? data : [];
        this.loading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load favorites', err);
        this.error = 'Failed to load favorite routes';
        this.loading = false;
      }
    });
  }

  useRoute(route: FavoriteRoute) {
    this.routeSelected.emit(route);
    this.router.navigate(['/base'], {
      state: { favoriteRoute: route }
    });
    console.log('Use favorite route', route);
  }

  removeFavorite(route: FavoriteRoute) {
    if (!confirm('Remove this route from favorites?')) return;
    this.loading = true;
    this.http.delete(`${this.baseUrl}/favorite-routes/${route.Id}`).subscribe({
      next: () => {
        this.favorites = this.favorites.filter(r => r.Id !== route.Id);
        this.loading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Failed to remove favorite', err);
        this.error = 'Failed to remove favorite route';
        this.loading = false;
      }
    });
  }

  formatMinutes(minutes?: number | null): string {
    if (minutes == null || isNaN(minutes as any)) return '-';
    const m = Math.round(minutes);
    const h = Math.floor(m / 60);
    const rem = m % 60;
    return h > 0 ? `${h} H ${rem} min` : `${rem} min`;
  }
}
