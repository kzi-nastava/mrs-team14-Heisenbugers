import { Component } from "@angular/core";
import { BaseHeaderComponent } from "./components/header/base/base-header.component";
import { RouterOutlet } from "@angular/router";
import { LoggedInHeaderComponent } from "./components/header/logged-in/logged-in-header.component";
import { AuthService } from "./components/auth/auth.service";

// Layout with base header
@Component({
  selector: 'app-base-layout',
  template: `
    <div class="flex flex-col h-screen">
  <base-app-header></base-app-header>

  <div class="flex-1 min-h-0">
    <router-outlet></router-outlet>
  </div>
</div>
  `,
  imports: [BaseHeaderComponent, RouterOutlet]
})
export class BaseLayoutComponent {}

// Layout without any header
@Component({
  selector: 'app-noh-layout',
  template: `
    <router-outlet></router-outlet>
  `,
  imports: [RouterOutlet]
})
export class HeaderlessLayoutComponent {}

// Layout with logged-in header
@Component({
  selector: 'app-logged-layout',
  template: `
  <div class="flex flex-col h-screen">
  <logged-in-header></logged-in-header>

  <div class="flex-1 min-h-0">
    <router-outlet></router-outlet>
  </div>
</div>
  `,
  imports: [LoggedInHeaderComponent, RouterOutlet]
})
export class LoggedLayoutComponent {}


// Unified layout with conditional header
@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    BaseHeaderComponent,
    LoggedInHeaderComponent
  ],
  template: `
    <div class="flex flex-col h-screen">

      @if (auth.isLoggedIn()) {
        <logged-in-header></logged-in-header>
      } @else {
        <base-app-header></base-app-header>
      }

      <div class="flex-1 min-h-0">
        <router-outlet></router-outlet>
      </div>

    </div>
  `
})
export class AppLayoutComponent {
  constructor(public auth: AuthService) {}
}
