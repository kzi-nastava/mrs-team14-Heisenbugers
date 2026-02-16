import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective  } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { RideAnalyticsService } from '../../services/ride-analytics.service';
import { AuthService } from '../auth/auth.service';
import { RideAnalyticsResponse, DailyItem } from '../../models/ride-analytics.model';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-ride-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective ],
  templateUrl: './ride-analytics.component.html',
})
export class RideAnalyticsComponent implements OnInit {
  private analyticsService = inject(RideAnalyticsService);
  private authService = inject(AuthService);
  private http = inject(HttpClient);

  userRole: string | null = null;
  selectedRoleFilter: 'DRIVER' | 'PASSENGER' | 'ALL_DRIVERS' | 'ALL_PASSENGERS' | null = null;
  selectedUserId: string | null = null;

  users: { id: string; firstName: string; lastName: string; email?: string }[] = [];

  startDate = this.formatDate(new Date(new Date().setDate(new Date().getDate() - 6)));
  endDate = this.formatDate(new Date());

  loading = false;
  error: string | null = null;

  daily: DailyItem[] = [];

  labels: string[] = [];
  ridesData: number[] = [];
  kmData: number[] = [];
  moneyData: number[] = [];

  lineChartOptions: ChartOptions = {
    responsive: true,
    plugins: {
      legend: { display: true }
    }
  };

  ridesChartData?: ChartConfiguration<'line'>['data'];
  kmChartData?: ChartConfiguration<'line'>['data'];
  moneyChartData?: ChartConfiguration<'line'>['data'];

  totals = { rides: 0, kilometers: 0, money: 0, days: 0 };

  constructor(private cdf: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.userRole = this.authService.getRole();
    if (this.userRole === 'ADMIN') {
      this.loadUsers();
    }
    this.loadData();
  }

  private formatDate(d: Date): string {
    return new Date(d.getFullYear(), d.getMonth(), d.getDate()).toISOString().slice(0, 10);
  }

  applyRange(): void {
    this.loadData();
  }

  private loadUsers(): void {
    this.http.get<any[]>(`http://localhost:8081/api/users/blockable`).subscribe({
      next: (res) => {
        this.users = res.map(u => ({ id: u.id, firstName: u.firstName || '', lastName: u.lastName || '', email: u.email }));
        this.cdf.detectChanges();
      },
      error: (err) => {
        console.warn('Failed to load users for admin dropdown', err);
      }
    });
  }

  private loadData(): void {
    this.loading = true;
    this.error = null;

    const role = this.userRole ?? undefined;
    const aggregate = this.userRole === 'ADMIN' && (this.selectedRoleFilter === 'ALL_DRIVERS' || this.selectedRoleFilter === 'ALL_PASSENGERS');
    const roleParam = (this.selectedRoleFilter === 'ALL_PASSENGERS' || this.selectedRoleFilter === 'ALL_DRIVERS') ? (this.selectedRoleFilter === 'ALL_DRIVERS' ? 'DRIVER' : 'PASSENGER') : role;

    this.analyticsService.getDailyAggregates(this.startDate, this.endDate, roleParam ?? undefined, this.selectedUserId ?? undefined, aggregate)
      .subscribe({
        next: (res: RideAnalyticsResponse) => this.handleResponse(res),
        error: (err) => {
          console.warn(err);
          this.error = 'Failed to load analytics';
          this.loading = false;
          this.cdf.detectChanges();
        }
      });
  }

  private handleResponse(res: RideAnalyticsResponse): void {
    this.daily = res.daily || [];

    const totals = this.daily.reduce((acc, cur) => {
      acc.rides += cur.rides;
      acc.kilometers += cur.kilometers;
      acc.money += cur.money;
      return acc;
    }, { rides: 0, kilometers: 0, money: 0 });

    this.totals.days = this.daily.length || 0;
    this.totals.rides = res.totals?.rides ?? totals.rides;
    this.totals.kilometers = res.totals?.kilometers ?? totals.kilometers;
    this.totals.money = res.totals?.money ?? totals.money;

    this.buildCharts();

    this.loading = false;
    this.cdf.detectChanges();
  }

  private buildCharts(): void {
    this.labels = this.daily.map(d => d.date);
    this.ridesData = this.daily.map(d => d.rides);
    this.kmData = this.daily.map(d => +d.kilometers);
    this.moneyData = this.daily.map(d => +d.money);

    this.ridesChartData = {
      labels: this.labels,
      datasets: [
        { data: this.ridesData, label: 'Rides', borderColor: '#16a34a', backgroundColor: '#16a34a', fill: false, tension: 0.3 }
      ]
    };

    this.kmChartData = {
      labels: this.labels,
      datasets: [
        { data: this.kmData, label: 'Kilometers', borderColor: '#0ea5e9', backgroundColor: '#0ea5e9', fill: false, tension: 0.3 }
      ]
    };

    this.moneyChartData = {
      labels: this.labels,
      datasets: [
        { data: this.moneyData, label: 'Money', borderColor: '#f59e0b', backgroundColor: '#f59e0b', fill: false, tension: 0.3 }
      ]
    };
  }

  get avgRides(): number { return this.totals.days ? +(this.totals.rides / this.totals.days).toFixed(2) : 0; }
  get avgKm(): number { return this.totals.days ? +(this.totals.kilometers / this.totals.days).toFixed(2) : 0; }
  get avgMoney(): number { return this.totals.days ? +(this.totals.money / this.totals.days).toFixed(2) : 0; }
}
