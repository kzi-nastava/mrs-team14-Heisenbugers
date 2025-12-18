import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapPersonCircle, bootstrapPencilFill, bootstrapClockFill } from '@ng-icons/bootstrap-icons';
import { ProfileCard} from './profile-card/profile-card';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-profile',
  imports: [
    RouterLink,
    NgIcon,
    ProfileCard,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  viewProviders: [provideIcons({ bootstrapPersonCircle,bootstrapPencilFill, bootstrapClockFill })]
})
export class ProfileComponent {
  public selectedTab: string = 'Personal Information';

  showPassword = false;
  showConfirmPassword = false;
  showOldPassword = false;

  toggleOldPassword() {
    this.showOldPassword = !this.showOldPassword;
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  constructor() {
  }

  setProfileTab(tab: string) {
    this.selectedTab = tab;
  }
}
