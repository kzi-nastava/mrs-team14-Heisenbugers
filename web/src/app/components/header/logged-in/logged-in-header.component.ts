import { Component, HostListener } from '@angular/core';
import { NgIcon } from "@ng-icons/core";
import { bootstrapBell, bootstrapHeart } from '@ng-icons/bootstrap-icons';
import { provideIcons } from '@ng-icons/core';
import { Router } from "@angular/router";
import {AuthService} from '../../auth/auth.service';
import { DriverStatusToggleComponent } from '../../driver-status-toggle/driver-status-toggle.component';
import { HttpClient } from '@angular/common/http';
import { Notification } from '../../../models/notification.model';


@Component({
  selector: 'logged-in-header',
  standalone: true,
  templateUrl: './logged-in-header.component.html',
  imports: [NgIcon,DriverStatusToggleComponent],
  viewProviders: [provideIcons({bootstrapBell, bootstrapHeart})]
})
export class LoggedInHeaderComponent {
  profileMenuOpen = false;
  notificationsOpen = false;
  notifications: Notification[] = [];
  unreadCount: number = 0;
  private baseUrl = 'http://localhost:8081/api';

  //constructor(private router: Router) {}
  constructor(
    private router: Router,
    protected auth: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.http.get<Notification[]>(`${this.baseUrl}/notifications/unread`)
      .subscribe(nots => {
        this.notifications = nots;
        this.unreadCount = nots.filter(n => !n.read).length;
      });
  }

  toggleProfileMenu() {
    this.notificationsOpen = false;
    this.profileMenuOpen = !this.profileMenuOpen;
    console.log("toggling profile menu")
  }

  toggleNotifications() {
    this.profileMenuOpen = false;
    this.notificationsOpen = !this.notificationsOpen;
    console.log("toggling notifications")
  }

  isProfileClick(target: HTMLElement){
    return target.closest('#profile-menu-container') || target.closest('#profile-button')
  }

  @HostListener('document:click', ['$event'])
  closeOnOutsideClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (this.profileMenuOpen && !this.isProfileClick(target)) {
    this.profileMenuOpen = false;
    }
  }

  goToProfile(){
    this.router.navigate(['profile'])
    this.profileMenuOpen = false
  }
  goToHistory(){
    this.router.navigate(['driver-ride-history'])
    this.profileMenuOpen = false
  }/*
  logOut(){
    this.router.navigate(['/home'])
    this.menuOpen = false
  }*/
  logOut() {
    this.auth.logoutLocal();
    this.router.navigate(['/home']);
    this.profileMenuOpen = false;
  }

  goToRegisterDriver(){
    this.router.navigate(['driver-registration'])
    this.profileMenuOpen = false
  }

  goToProfileChanges(){
    this.router.navigate(['profile-requests'])
    this.profileMenuOpen = false
  }

  goToHome(){
    this.router.navigate(['base'])
    this.profileMenuOpen = false
  }
}
