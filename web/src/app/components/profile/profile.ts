import {ChangeDetectorRef, Component, signal} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
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
import {Vehicle} from './model/vehicle.model';
import {HttpClient} from '@angular/common/http';
import {GetProfileDTO, UpdateProfileDTO} from '../../models/profile.model';

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

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef) {

  }

  ngOnInit() {
    this.http.get<GetProfileDTO>(`http://localhost:8081/api/profile/me`).subscribe({
      next: (data) => {
        console.log(data);
        this.user.set({
          name: `${data.firstName} ${data.lastName}`,
          email: data.email,
          address: data.address,
          phoneNumber: data.phoneNumber,
          profilePicture: data.profileImageUrl
        });

      },
      error: (error) => {
        console.warn('Error:', error);
      }
    });
  }

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

  vehicle = signal<Vehicle>({
    model: 'Ford Fiesta',
    type: 'Standard',
    plateNumber: 'NS-215-KL',
    seats: '5',
    babiesAllowed: true,
    petsAllowed: false
  });

  updateUser(updatedUser: UpdateProfileDTO) {
      this.http.put<GetProfileDTO>('http://localhost:8081/api/profile/me', updatedUser)
        .subscribe(updated => {
          this.user.set({
            name: `${updated.firstName} ${updated.lastName}`,
            email: updated.email,
            address: updated.address,
            phoneNumber: updated.phoneNumber,
            profilePicture: updated.profileImageUrl
          });
        });
  }

  updateVehicle(updatedVehicle: any) {
    this.vehicle.update(v => ({
      ...v,
      ...updatedVehicle
    }));
  }
}
