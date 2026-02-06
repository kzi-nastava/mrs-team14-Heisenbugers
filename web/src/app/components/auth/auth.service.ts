import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {
  RegisterResponseDTO,
  MessageResponse,
  LoginRequestDTO,
  LoginResponseDTO,
  ForgotPasswordRequestDTO,
  ForgotPasswordResponseDTO,
  ResetPasswordRequestDTO
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

  user$ = new BehaviorSubject<string | null>(null);
  userState = this.user$.asObservable();


  constructor(private http: HttpClient) {
   // this.user$.next(this.getRole());
    this.restoreFromStorage();
  }

  private restoreFromStorage() {
    const token = localStorage.getItem('accessToken');

    if (token) {
      const helper = new JwtHelperService();
      if (!helper.isTokenExpired(token)) {
        const decoded = helper.decodeToken(token);
        const role = decoded?.role ?? null;
        this.user$.next(role);
        return;
      }
    }

    this.user$.next(null);
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

  /*
  logout() {
    localStorage.removeItem('accessToken');
    this.user$.next(null);
  }*/

  getRole(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('accessToken');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).role;
    }
    return null;
  }

  logout(): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/session`);
  }

  logoutLocal(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('userId');
    localStorage.removeItem('role');

    this.user$.next(null);
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('accessToken');
    if (!token) return false;

    const helper = new JwtHelperService();
    return !helper.isTokenExpired(token);
  }
  /*
  isLoggedIn$(): Observable<boolean> {
    return this.loggedIn$.asObservable();
  }*/

  setUser(): void {
    this.user$.next(this.getRole());
  }

  setAfterLogin(token: string) {
    localStorage.setItem('accessToken', token);

    const helper = new JwtHelperService();
    const decoded = helper.decodeToken(token);
    const role = decoded?.role ?? null;

    this.user$.next(role);
  }

  forgotPassword(email: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(
      `${this.baseUrl}/forgot-password`,
      { email },
      { headers: this.headers }
    );
  }

  resetPassword(token: string, newPassword: string, confirmPassword: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(
      `${this.baseUrl}/reset-password`,
      { token, newPassword, confirmPassword },
      { headers: this.headers }
    );
  }


}
