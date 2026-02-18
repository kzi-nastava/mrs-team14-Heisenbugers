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
import {FavoriteRoutesComponent} from '../favorite-routes/favorite-routes.component';
import {AdminRidesComponent} from '../admin/admin-rides/admin-rides.component';
import {PassengerRideHistoryComponent} from '../passenger-ride-history/passenger-ride-history.component';
import {ActivatedRoute} from '@angular/router';
import {IsBlockedDTO} from '../../models/users.model';
import {RideAnalyticsComponent} from '../ride-analytics/ride-analytics.component';
import { RideHistoryComponent } from "../driver-ride-history/driver-ride-history";

@Component({
  selector: 'app-profile',
  imports: [
    NgIcon,
    ProfileCard,
    FormsModule,
    ReactiveFormsModule,
    ManagePassword,
    EditProfile,
    EditVehicle,
    FavoriteRoutesComponent,
    AdminRidesComponent,
    PassengerRideHistoryComponent,
    RideAnalyticsComponent,
    RideHistoryComponent
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
  isDriverBlocked = false;
  blockNote = "";

  constructor(private router: Router, private http: HttpClient, private cdr: ChangeDetectorRef, private authService: AuthService,private ar: ActivatedRoute) {

  }

  isAdmin(): boolean {
    return this.userRole === 'ADMIN';
  }

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.ar.queryParams.subscribe(params => {
      const tab = params['tab'];
      if (tab) this.selectedTab = tab;
    });
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
      this.http.get<IsBlockedDTO>(`http://localhost:8081/api/users/is-blocked`).subscribe({
        next: (data) => {
          this.isDriverBlocked = data.blocked;
          this.blockNote = data.blockNote || '';
          this.cdr.detectChanges()
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

  private base64ToFile(base64: string, filename: string): File {
    const arr = base64.split(',');
    const mime = arr[0].match(/:(.*?);/)![1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);

    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }

    return new File([u8arr], filename, { type: mime });
  }

  updateUser(updatedUser: UpdateProfileDTO) {
      const formData = new FormData();
      if (updatedUser.image != null) {
        const image = updatedUser.image;
        updatedUser.image = null;
        formData.append(
          'data',
          new Blob([JSON.stringify(updatedUser)], { type: 'application/json' })
        );
        const file = this.base64ToFile(image, 'profile.png');
        formData.append('image', file);
      }else{
        formData.append(
          'data',
          new Blob([JSON.stringify(updatedUser)], { type: 'application/json' })
        );
      }

      this.http.put<GetProfileDTO>('http://localhost:8081/api/profile/me', formData)
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

  updateVehicle(updatedVehicle: CreateVehicleDTO) {
    this.http.put<CreateVehicleDTO>('http://localhost:8081/api/profile/me/vehicle', updatedVehicle)
      .subscribe(updated => {
        this.vehicle.set({
          model: updated.vehicleModel,
          type: updated.vehicleType,
          plateNumber: updated.licensePlate,
          seats: updated.seatCount.toString(),
          babiesAllowed: updated.babyTransport,
          petsAllowed: updated.petTransport
        });
      });
  }

  private formatMinutesToHours(minutes: number): string {
    if (!Number.isFinite(minutes) || minutes <= 0) return '0 H 0 MIN';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours} H ${mins} MIN`;
  }
}
