import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home';

import { RegisterComponent } from './components/auth/register/register';
import { LoginComponent } from './components/auth/login/login';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'auth/login', component: LoginComponent },
];
