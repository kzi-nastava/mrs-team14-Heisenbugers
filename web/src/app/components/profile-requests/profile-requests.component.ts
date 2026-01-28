import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { DriverProfileRequestComponent } from '../driver-profile-request/driver-profile-request.component';

@Component({
  selector: 'app-profile-requests',
  standalone: true,
  imports: [CommonModule, DriverProfileRequestComponent],
  templateUrl: './profile-requests.component.html',
  styleUrls: ['./profile-requests.component.css'],
})
export class ProfileRequestsComponent implements OnInit {
  requests: any[] = [];
  loadingList = false;
  listError: string | null = null;

  loadingDetail = false;
  detailError: string | null = null;
  selectedRequest: any = null;
  selectedOldProfile: any = null;
  selectedNewProfile: any = null;

  private baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient, private cd: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadList();
    this.cd.detectChanges();
  }

  loadList() {
    this.loadingList = true;
    this.listError = null;
    this.http.get<any[]>(`${this.baseUrl}/driver-requests`).subscribe({
      next: (data) => {
        this.requests = Array.isArray(data) ? data : [];
        this.loadingList = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.listError = 'Failed to load requests';
        console.error(err);
        this.loadingList = false;
        this.cd.detectChanges();
      }
    });
  }

  openRequest(id: string | number) {
    this.loadingDetail = true;
    this.detailError = null;
    this.selectedRequest = null;
    this.selectedOldProfile = null;
    this.selectedNewProfile = null;

    this.http.get<any>(`${this.baseUrl}/driver-requests/${id}`).subscribe({
      next: (data) => {
        this.selectedRequest = data;

        this.selectedOldProfile = data.oldProfile ?? {
          firstName: data.firstNameOld || data.firstNameCurrent || '',
          lastName: data.lastNameOld || data.lastNameCurrent || '',
          phone: data.phoneOld || data.phoneCurrent || data.phone || '',
          address: data.addressOld || data.addressCurrent || data.address || '',
          profileImageUrl: data.profileImageUrlOld || data.profileImageUrlCurrent || data.profileImageUrl || '',
          model: data.modelOld || data.modelCurrent || '',
          type: data.typeOld || data.typeCurrent || '',
          licensePlate: data.licensePlateOld || data.licensePlateCurrent || '',
          seatCount: data.seatCountOld ?? data.seatCountCurrent ?? null,
          babyTransport: data.babyTransportOld ?? data.babyTransportCurrent ?? false,
          petTransport: data.petTransportOld ?? data.petTransportCurrent ?? false,
        };

        this.selectedNewProfile = data.newProfile ?? {
          firstName: data.firstName || data.firstNameNew || '',
          lastName: data.lastName || data.lastNameNew || '',
          phone: data.phone || data.phoneNew || '',
          address: data.address || data.addressNew || '',
          profileImageUrl: data.profileImageUrl || data.profileImageUrlNew || '',
          model: data.model || data.modelNew || '',
          type: data.type || data.typeNew || '',
          licensePlate: data.licensePlate || data.licensePlateNew || '',
          seatCount: data.seatCount ?? data.seatCountNew ?? null,
          babyTransport: data.babyTransport ?? data.babyTransportNew ?? false,
          petTransport: data.petTransport ?? data.petTransportNew ?? false,
        };

        this.loadingDetail = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.detailError = 'Failed to load request details';
        this.loadingDetail = false;
        this.cd.detectChanges();
      }
    });
  }

  closeDetail() {
    this.selectedRequest = null;
    this.selectedOldProfile = null;
    this.selectedNewProfile = null;
  }
}
