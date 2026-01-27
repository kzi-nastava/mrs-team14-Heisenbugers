import {ChangeDetectorRef, Component, signal} from '@angular/core';
import {Router} from '@angular/router';
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
import {AuthService} from '../auth/auth.service';
import {CreateVehicleDTO} from '../../models/driver-registration.model';

@Component({
  selector: 'app-profile',
  imports: [
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
  userRole = "";
  driverActiveHours = "0 H 0 MIN";

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef, private authService: AuthService) {

  }

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.http.get<GetProfileDTO>(`http://localhost:8081/api/profile/me`).subscribe({
      next: (data) => {
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
    if (this.userRole === 'DRIVER') {
      this.http.get<any>(`http://localhost:8081/api/profile/me/driver`).subscribe({
        next: (data) => {
          const minutes = Number(data) || 0;
          this.driverActiveHours = this.formatMinutesToHours(minutes);
        },
        error: (error) => {
          console.warn('Error:', error);
        }
      });
      this.http.get<CreateVehicleDTO>(`http://localhost:8081/api/profile/me/vehicle`).subscribe({
        next: (data) => {
          this.vehicle.set({
            model: data.vehicleModel,
            type: data.vehicleType,
            plateNumber: data.licensePlate,
            seats: data.seatCount.toString(),
            babiesAllowed: data.babyTransport,
            petsAllowed: data.petTransport
          });

        },

        error: (error) => {
          console.warn('Error:', error);
        }
      });
    }
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

  logOut(): void {
    localStorage.removeItem('accessToken');
    this.authService.setUser();
    this.router.navigate(["auth/login"]);
  }

  updateVehicle(updatedVehicle: any) {
    this.vehicle.update(v => ({
      ...v,
      ...updatedVehicle
    }));
  }

  private formatMinutesToHours(minutes: number): string {
    if (!Number.isFinite(minutes) || minutes <= 0) return '0 H 0 MIN';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours} H ${mins} MIN`;
  }
}
