import { Component, Input } from '@angular/core';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {bootstrapPersonFill, bootstrapEnvelopeFill, bootstrapTelephoneFill, bootstrapPinMapFill} from '@ng-icons/bootstrap-icons';

@Component({
  selector: 'app-profile-card',
  imports: [
    NgIcon,
  ],
  templateUrl: './profile-card.html',
  styleUrl: './profile-card.css',
  viewProviders: [provideIcons({ bootstrapPersonFill, bootstrapEnvelopeFill, bootstrapTelephoneFill, bootstrapPinMapFill })]
})
export class ProfileCard {
  @Input() title!: string;
  @Input() value!: string;
  @Input() iconName?: string;
}
