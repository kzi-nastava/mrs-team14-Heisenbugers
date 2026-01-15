import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  RegisterPassengerRequestDTO,
  RegisterResponseDTO,
  MessageResponse,
  LoginRequestDTO,
  LoginResponseDTO
} from './auth.api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // without proxy.conf.json
  private baseUrl = 'http://localhost:8081/api/auth';

  constructor(private http: HttpClient) {}

  register(dto: RegisterPassengerRequestDTO): Observable<RegisterResponseDTO> {
    return this.http.post<RegisterResponseDTO>(`${this.baseUrl}/register`, dto);
  }

  activate(token: string): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(`${this.baseUrl}/activate`, { params: { token } });
  }

  login(dto: LoginRequestDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.baseUrl}/login`, dto);
  }

  logout(): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/session`);
  }
}
