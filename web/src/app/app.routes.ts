import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home';

import { RegisterComponent } from './components/auth/register/register';
import { LoginComponent } from './components/auth/login/login';
import { ProfileComponent } from './components/profile/profile';
import { ForgotPasswordComponent } from './components/auth/forgot-password/forgot-password';
import { RideHistoryComponent } from './components/driver-ride-history/driver-ride-history';
import { RideCardComponent } from './components/driver-ride-history/ride-card/ride-card.component';
import { BaseLayoutComponent, LoggedLayoutComponent } from './layouters';

/*
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/forgot-password', component: ForgotPasswordComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'driver-ride-history', component: RideHistoryComponent },
  { path: 'driver-ride-history/ride', component: RideCardComponent }
];
*/

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  {
    path: '',
    component: BaseLayoutComponent,
    children: [
      { path: 'auth/register', component: RegisterComponent },
      { path: 'auth/login', component: LoginComponent },
      { path: 'auth/forgot-password', component: ForgotPasswordComponent },
    ]
  },
  {
    path: '',
    component: LoggedLayoutComponent,
    children: [
      { path: 'profile', component: ProfileComponent },
      { path: 'driver-ride-history', component: RideHistoryComponent },
      { path: 'driver-ride-history/ride', component: RideCardComponent },
    ]
  }
];
