import { Component } from "@angular/core";
import { AdminRidesComponent } from "../admin-rides/admin-rides.component";
import { AdminPanicComponent } from "../admin-panic/admin-panic.component";
import { AdminChats } from "../admin-chats/admin-chats.component";
import { AdminPrice } from "../admin-price/admin-price";
import { RideAnalyticsComponent } from "../../ride-analytics/ride-analytics.component";
import { BlockUsersComponent } from "../../block-users/block-users.component";
import { DriverRegistrationComponent } from "../../auth/driver-registration/driver-registration.component";
import { ProfileRequestsComponent } from "../../profile-requests/profile-requests.component";
import { AdminAllRidesComponent } from "../admin-all-rides/admin-all-rides.component";
import { ActivatedRoute } from "@angular/router";

@Component({
    selector: 'app-admin-dashboard',
    templateUrl: './admin-dashboard.component.html',
    imports: [AdminRidesComponent, AdminPanicComponent, AdminChats, AdminPrice, RideAnalyticsComponent, BlockUsersComponent, DriverRegistrationComponent, ProfileRequestsComponent, AdminAllRidesComponent],
})
export class AdminDashboardComponent {
    selectedTab: string = 'Rides';

    constructor(private route: ActivatedRoute) {
        
    }

    setProfileTab(tab: string) {
    this.selectedTab = tab;
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const tab = params['tab'];
      if (tab) {
        this.selectedTab = tab;
      }
    });
  }
}