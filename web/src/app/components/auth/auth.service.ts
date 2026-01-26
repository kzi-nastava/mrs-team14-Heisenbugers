import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {
  RegisterResponseDTO,
  MessageResponse,
  LoginRequestDTO,
  LoginResponseDTO
} from './auth.api';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // without proxy.conf.json
  private baseUrl = 'http://localhost:8081/api/auth';

  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });

  user$ = new BehaviorSubject("");
  userState = this.user$.asObservable();

  constructor(private http: HttpClient) {
    this.user$.next(this.getRole());
  }

  register(formData: FormData): Observable<RegisterResponseDTO> {
    //return this.http.post<RegisterResponseDTO>(`${this.baseUrl}/register`, dto);
    return this.http.post<RegisterResponseDTO>(`${this.baseUrl}/register`, formData);
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

  getRole(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('accessToken');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).role;
    }
    return null;
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('accessToken') != null;
  }

  setUser(): void {
    this.user$.next(this.getRole());
  }
}
