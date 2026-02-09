import { Routes } from '@angular/router';
import { AppLayoutComponent } from './layouters';

import { HomeComponent } from './components/home/home';

import { RegisterComponent } from './components/auth/register/register';
import { LoginComponent } from './components/auth/login/login';
import { ForgotPasswordComponent } from './components/auth/forgot-password/forgot-password';
import { RequestPasswordResetComponent } from './components/auth/request-password-reset/request-password-reset.component';
import { SetPasswordComponent } from './components/auth/set-password/set-password.component';
import { TokenUsedComponent } from './components/auth/token-used/token-used.component';

import { ProfileComponent } from './components/profile/profile';
import { RideHistoryComponent } from './components/driver-ride-history/driver-ride-history';
import { RideCardComponent } from './components/driver-ride-history/ride-card/ride-card.component';
import { DriverRegistrationComponent } from './components/auth/driver-registration/driver-registration.component';
import { StartRideComponent } from './components/start-ride/start-ride.component';
import { DuringRide } from './components/during-ride/during-ride.component';
import { HomeContainer } from './home-container/home-container.component';

import { AdminLayoutComponent } from './components/admin/admin-layout/admin-layout.component';
import { AdminPanicComponent } from './components/admin/admin-panic/admin-panic.component';
import { AdminGuard } from './components/admin/admin.guard';
import {DriverProfileRequestComponent} from './components/driver-profile-request/driver-profile-request.component';
import {ProfileRequestsComponent} from './components/profile-requests/profile-requests.component';
import {FavoriteRoutesComponent} from './components/favorite-routes/favorite-routes.component';

export const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },

      { path: 'home', component: HomeComponent },
      { path: 'base', component: HomeContainer },

      // auth
      { path: 'auth/register', component: RegisterComponent },
      { path: 'auth/login', component: LoginComponent },
      { path: 'auth/reset-password', component: ForgotPasswordComponent },
      { path: 'auth/forgot-password', component: RequestPasswordResetComponent },
      { path: 'auth/set-password', component: SetPasswordComponent },
      { path: 'auth/token-used', component: TokenUsedComponent },

      // logged-in
      { path: 'profile', component: ProfileComponent },
      { path: 'driver-ride-history', component: RideHistoryComponent },
      { path: 'driver-ride-history/ride', component: RideCardComponent },
      { path: 'driver-registration', component: DriverRegistrationComponent },
      { path: 'start-ride', component: StartRideComponent },
      { path: 'during-ride/:rideId', component: DuringRide },
      { path: 'profile-requests', component: ProfileRequestsComponent},
      { path: 'driver-profile-requests', component: DriverProfileRequestComponent},
      { path: 'favorite-rides', component: FavoriteRoutesComponent },

      // admin
      {
        path: 'admin',
        component: AdminLayoutComponent,
        canActivate: [AdminGuard],
        children: [
          { path: 'panic', component: AdminPanicComponent },
          { path: '', redirectTo: 'panic', pathMatch: 'full' }
        ]
      }
    ]
  }
];
