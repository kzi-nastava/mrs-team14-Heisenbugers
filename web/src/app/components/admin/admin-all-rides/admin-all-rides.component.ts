import { Component } from "@angular/core";
import { AdminRide } from "../../../models/admin-ride.model";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { DatePipe } from "@angular/common";
import { bootstrapChevronRight, bootstrapClock, bootstrapFlag, bootstrapGeoAlt, bootstrapPerson } from "@ng-icons/bootstrap-icons";
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-admin-all-rides',
  templateUrl: './admin-all-rides.component.html',
  imports: [NgIcon, DatePipe, FormsModule],
  viewProviders: [provideIcons({bootstrapPerson, bootstrapGeoAlt, bootstrapClock, bootstrapFlag, bootstrapChevronRight})]
})
export class AdminAllRidesComponent {
    searchTerm: any;
    list: AdminRide[] = [
        {
            ride: {
                rideId: 'ride-123',
                status: 'COMPLETED',
                startedAt: '2025-12-19T08:12:00',
                endedAt: '2025-12-19T10:12:00',
                startAddress: 'ул.Атамана Головатого 2а',
                destinationAddress: 'ул.Красная 113',
                canceled: false,
                canceledBy: null,
                price: 350,
                panicTriggered: false,
            },
            driver: {
                firstName: 'Vozac',
                lastName: 'Vozacovic',
            }
        },
        {
            ride: {
                rideId: 'ride-456',
                status: 'CANCELED',
                startedAt: '2025-12-20T14:00:00',
                endedAt: null,
                startAddress: 'ул.Ленина 50',
                destinationAddress: 'ул.Пушкина 20',
                canceled: true,
                canceledBy: 'PASSENGER',
                price: 0,
                panicTriggered: false,
            },
            driver: {
                firstName: 'Marko',
                lastName: 'Markovic',
            }
        }
    ];

    filteredList: AdminRide[] = [...this.list];
    

    applyFilter() {
        const term = this.searchTerm.toLowerCase();

        this.filteredList = this.list.filter(u =>
            `${u.driver.firstName} ${u.driver.lastName}
            ${u.ride.startAddress}
            ${u.ride.destinationAddress}
            ${u.ride.startedAt}
            ${u.ride.endedAt ?? ''}`
            .toLowerCase()
            .includes(term)
        );
   }
}