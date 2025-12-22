// header.component.ts
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from "@angular/router";

@Component({
  selector: 'base-app-header',
  templateUrl: './base-header.component.html',
  styleUrls: [],
  imports: [RouterLink, RouterLinkActive]
})
export class BaseHeaderComponent {
  // Component logic here
}