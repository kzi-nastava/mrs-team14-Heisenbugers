import { ChangeDetectorRef, Component, HostListener, NgZone, OnInit } from '@angular/core';
import { NgIcon } from "@ng-icons/core";
import { bootstrapBell, bootstrapHeart } from '@ng-icons/bootstrap-icons';
import { provideIcons } from '@ng-icons/core';
import { Router } from "@angular/router";
import {AuthService} from '../../auth/auth.service';
import { DriverStatusToggleComponent } from '../../driver-status-toggle/driver-status-toggle.component';
import { HttpClient } from '@angular/common/http';
import { Notification } from '../../../models/notification.model';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';


@Component({
  selector: 'logged-in-header',
  standalone: true,
  templateUrl: './logged-in-header.component.html',
  imports: [NgIcon,DriverStatusToggleComponent],
  viewProviders: [provideIcons({bootstrapBell, bootstrapHeart})]
})
export class LoggedInHeaderComponent implements OnInit {
  profileMenuOpen = false;
  notificationsOpen = false;
  notifications: Notification[] = [];
  unreadCount: number = 0;
  private baseUrl = 'http://localhost:8081/api';
  stompClient?: Client;

  //constructor(private router: Router) {}
  constructor(
    private router: Router,
    protected auth: AuthService,
    private http: HttpClient,
    private ngZone: NgZone,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Load unread notifications initially
    this.http.get<Notification[]>(`${this.baseUrl}/notifications/unread`)
      .subscribe(nots => {
        this.notifications = nots;
        this.unreadCount = nots.filter(n => !n.read).length;
      });

    // Connect to WebSocket
    this.stompClient = new Client({
      webSocketFactory: () =>
        new SockJS(`${this.baseUrl.replace('/api', '')}/ws`),
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      },
      reconnectDelay: 5000
    });

    this.stompClient.onConnect = () => {
      this.stompClient?.subscribe(
        '/user/queue/notifications',
        (msg: IMessage) => {
          this.ngZone.run(() => {
            const newNot: Notification = JSON.parse(msg.body);
            this.notifications.unshift(newNot);
            this.unreadCount += 1;
            this.cdr.markForCheck();
          });
        }
      );
    };

    this.stompClient.activate();
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

  openFavorites(){
    this.router.navigate(['favorite-rides'])
    this.profileMenuOpen = false
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
    const role = this.auth.getRole();


    if (role === 'ADMIN') {
      this.router.navigate(['/profile/'],{ queryParams: { tab: 'Admin Ride History' } });
    } else if (role === 'DRIVER') {
      this.router.navigate(['/driver-ride-history']);
    } else {
      this.router.navigate(['/profile'],{ queryParams: { tab: 'Ride History' } });
    }

    this.profileMenuOpen = false;
    //this.router.navigate(['driver-ride-history'])
    //this.profileMenuOpen = false
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

  get isDriver(): boolean {
    const r = this.auth.getRole();
    return r === 'DRIVER' || r === 'ROLE_DRIVER';
  }
}
