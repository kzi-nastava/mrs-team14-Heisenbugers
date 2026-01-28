import { Component, HostListener } from '@angular/core';
import { NgIcon } from "@ng-icons/core";
import { bootstrapBell, bootstrapHeart } from '@ng-icons/bootstrap-icons';
import { provideIcons } from '@ng-icons/core';
import { Router } from "@angular/router";
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'logged-in-header',
  standalone: true,
  templateUrl: './logged-in-header.component.html',
  imports: [NgIcon],
  viewProviders: [provideIcons({bootstrapBell, bootstrapHeart})]
})
export class LoggedInHeaderComponent {
  menuOpen = false;

  //constructor(private router: Router) {}
  constructor(
    private router: Router,
    protected auth: AuthService
  ) {}

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
    console.log("toggling menu")
  }

  isProfileClick(target: HTMLElement){
    return target.closest('#profile-menu-container') || target.closest('#profile-button')
  }

  @HostListener('document:click', ['$event'])
  closeOnOutsideClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (this.menuOpen && !this.isProfileClick(target)) {
    this.menuOpen = false;
    }
  }

  goToProfile(){
    this.router.navigate(['profile'])
    this.menuOpen = false
  }
  goToHistory(){
    this.router.navigate(['driver-ride-history'])
    this.menuOpen = false
  }/*
  logOut(){
    this.router.navigate(['/home'])
    this.menuOpen = false
  }*/
  logOut() {
    this.auth.logoutLocal();          // ⬅️ очищаем токены и состояние
    this.router.navigate(['/home']);  // ⬅️ гость → на публичную страницу
    this.menuOpen = false;
  }

  goToRegisterDriver(){
    this.router.navigate(['driver-registration'])
    this.menuOpen = false
  }

  goToProfileChanges(){
    this.router.navigate(['profile-requests'])
    this.menuOpen = false
  }

  goToHome(){
    this.router.navigate(['base'])
    this.menuOpen = false
  }
}
