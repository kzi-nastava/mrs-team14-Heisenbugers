import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home';

import { RegisterComponent } from './components/auth/register/register';
import { LoginComponent } from './components/auth/login/login';
import { ProfileComponent } from './components/profile/profile';
import { RideHistoryComponent } from './components/driver-ride-history/driver-ride-history';
import { RideCardComponent } from './components/driver-ride-history/ride-card/ride-card.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'driver-ride-history', component: RideHistoryComponent },
  { path: 'driver-ride-history/ride', component: RideCardComponent }
];
