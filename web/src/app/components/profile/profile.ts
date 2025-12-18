import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapPersonCircle, bootstrapPencilFill } from '@ng-icons/bootstrap-icons';
import { ProfileCard} from './profile-card/profile-card';

@Component({
  selector: 'app-profile',
  imports: [
    RouterLink,
    NgIcon,
    ProfileCard
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  viewProviders: [provideIcons({ bootstrapPersonCircle,bootstrapPencilFill })]
})
export class ProfileComponent {
  public selectedTab: string = 'Personal Information';
  constructor() {
  }

  setProfileTab(tab: string) {
    this.selectedTab = tab;
  }
}
