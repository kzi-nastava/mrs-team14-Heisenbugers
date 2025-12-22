import { Component } from "@angular/core";
import { BaseHeaderComponent } from "./components/header/base/base-header.component";
import { RouterOutlet } from "@angular/router";
import { LoggedInHeaderComponent } from "./components/header/logged-in/logged-in-header.component";

// Layout with header
@Component({
  selector: 'app-base-layout',
  template: `
    <base-app-header></base-app-header>
    <router-outlet></router-outlet>
  `,
  imports: [BaseHeaderComponent, RouterOutlet]
})
export class BaseLayoutComponent {}

@Component({
  selector: 'app-noh-layout',
  template: `
    <router-outlet></router-outlet>
  `,
  imports: [RouterOutlet]
})
export class HeaderlessLayoutComponent {}

@Component({
  selector: 'app-logged-layout',
  template: `
  <div class="flex flex-col h-screen">
  <logged-in-header></logged-in-header>
  
  <!-- Router outlet container - THIS IS KEY -->
  <div class="flex-1 min-h-0">
    <router-outlet></router-outlet>
  </div>
</div>
  `,
  imports: [LoggedInHeaderComponent, RouterOutlet]
})
export class LoggedLayoutComponent {}