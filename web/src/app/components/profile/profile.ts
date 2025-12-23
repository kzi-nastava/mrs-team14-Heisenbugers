import {Component, signal} from '@angular/core';
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
import {EditVehicle} from './edit-vehicle/edit-vehicle';
import {User} from './model/user.model';

@Component({
  selector: 'app-profile',
  imports: [
    RouterLink,
    NgIcon,
    ProfileCard,
    FormsModule,
    ReactiveFormsModule,
    ManagePassword,
    EditProfile,
    EditVehicle
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  viewProviders: [provideIcons({ bootstrapPersonCircle,bootstrapPencilFill, bootstrapClockFill, bootstrapEye, bootstrapEyeSlash, bootstrapCameraFill, bootstrapCarFrontFill })]
})
export class ProfileComponent {

  selectedTab: string = 'Personal Information';
  isEditing = false;
  isVehicleEditing = false;

  setProfileTab(tab: string) {
    this.selectedTab = tab;
  }

  setIsEditing(value: boolean) {
    this.isEditing = value;
  }

  setIsEditingVehicle(value: boolean) {
    this.isVehicleEditing = value;
  }

  user = signal<User>({
    name: 'John Doe',
    email: 'johndoe@gmail.com',
    address: 'Bulevar Jovana Ducica 35, Novi Sad',
    phoneNumber: '381645712833',
    profilePicture: null
  });

  vehicle = {
    model: 'Ford Fiesta',
    type: 'Standard',
    plateNumber: 'NS-215-KL',
    seats: '5',
    babiesAllowed: true,
    petsAllowed: false
  };

  updateUser(updatedUser: any) {
    this.user.update(u => ({
      ...u,
      ...updatedUser
    }));
  }
}
