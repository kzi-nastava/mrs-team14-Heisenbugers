export interface DailyItem {
  date: string;
  rides: number;
  kilometers: number;
  money: number;
}

export interface Totals {
  rides: number;
  kilometers: number;
  money: number;
  days: number;
}

export interface RideAnalyticsResponse {
  daily: DailyItem[];
  totals?: Totals;
}

