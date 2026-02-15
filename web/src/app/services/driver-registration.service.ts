import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {CreateDriverDTO, CreateVehicleDTO} from '../models/driver-registration.model';


@Injectable({
  providedIn: 'root',
})
export class DriverRegistrationService {
  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8081/api/drivers';


  registerDriver(formValue: any, imageBase64: string | null): Observable<any> {
    const vehicleDTO: CreateVehicleDTO = {
      vehicleModel: formValue.model,
      vehicleType: formValue.type,
      licensePlate: formValue.plateNumber,
      seatCount: Number(formValue.seats),
      babyTransport: Boolean(formValue.babiesAllowed),
      petTransport: Boolean(formValue.petsAllowed),
    };

    const driverDTO: CreateDriverDTO = {
      email: formValue.email,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      phone: formValue.phone,
      address: formValue.address,
      profileImageUrl: null,
      vehicle: vehicleDTO,
    };

    const formData = new FormData();

    formData.append(
      'data',
      new Blob([JSON.stringify(driverDTO)], { type: 'application/json' })
    );

    if (imageBase64) {
      const file = this.base64ToFile(imageBase64, 'profile.png');
      formData.append('image', file);
    }

    return this.http.post<any>(this.API_URL, formData);
  }


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
}
