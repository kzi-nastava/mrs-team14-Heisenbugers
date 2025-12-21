import {Component, inject, ChangeDetectorRef } from '@angular/core';
import {RouterLink} from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapPersonCircle, bootstrapPencilFill, bootstrapClockFill, bootstrapEye, bootstrapEyeSlash, bootstrapCameraFill, bootstrapCarFrontFill } from '@ng-icons/bootstrap-icons';
import { ProfileCard} from './profile-card/profile-card';
import {
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {ManagePassword} from './manage-password/manage-password';
import {EditProfile} from './edit-profile/edit-profile';

@Component({
  selector: 'app-profile',
  imports: [
    RouterLink,
    NgIcon,
    ProfileCard,
    FormsModule,
    ReactiveFormsModule,
    ManagePassword,
    EditProfile
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  viewProviders: [provideIcons({ bootstrapPersonCircle,bootstrapPencilFill, bootstrapClockFill, bootstrapEye, bootstrapEyeSlash, bootstrapCameraFill, bootstrapCarFrontFill })]
})
export class ProfileComponent {

  selectedTab: string = 'Personal Information';
  isEditing = false;
  isVehicleEditing = false;


  petsAllowed = false;
  babiesAllowed = true;

  setProfileTab(tab: string) {
    this.selectedTab = tab;
  }

  setIsEditing(value: boolean) {
    this.isEditing = value;
  }

  setIsEditingVehicle(value: boolean) {
    this.isVehicleEditing = value;
  }

  user = {
    name: 'John Doe',
    email: 'johndoe@gmail.com',
    address: 'Bulevar Jovana Ducica 35, Novi Sad',
    phoneNumber: '381645712833'
  };
}
