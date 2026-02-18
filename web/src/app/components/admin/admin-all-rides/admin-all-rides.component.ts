import { ChangeDetectorRef, Component } from "@angular/core";
import { AdminRide } from "../../../models/admin-ride.model";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { DatePipe } from "@angular/common";
import { bootstrapChevronRight, bootstrapClock, bootstrapFlag, bootstrapGeoAlt, bootstrapPerson } from "@ng-icons/bootstrap-icons";
import { FormsModule } from "@angular/forms";
import { AdminRideInfo } from "./ride-info/ride-info.component";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-admin-all-rides',
  templateUrl: './admin-all-rides.component.html',
  imports: [NgIcon, DatePipe, FormsModule, AdminRideInfo],
  viewProviders: [provideIcons({bootstrapPerson, bootstrapGeoAlt, bootstrapClock, bootstrapFlag, bootstrapChevronRight})]
})
export class AdminAllRidesComponent {
    searchTerm: string = "";
    private baseUrl = 'http://localhost:8081/api';
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
            },
            vehicleLatitude: 44.7886,
            vehicleLongitude: 20.4689
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
            },
            vehicleLatitude: 44.7866,
            vehicleLongitude: 20.4489
        }
    ];

    filteredList: AdminRide[] = [...this.list];
    selectedRide!: AdminRide;
    modalOpen = false;

    constructor(private cdr: ChangeDetectorRef, private http: HttpClient) {
        
    }

    ngOnInit(): void {
        this.loadRides();
    }
    loadRides() {
        this.http.get<AdminRide[]>(`${this.baseUrl}/admin/rides/all`).subscribe({
            next: (data) => {
                this.list = data ?? [];
                this.applyFilter();
                this.cdr.markForCheck();
            },
            error: (e) => {
                console.error('Failed to load rides', e);
            }
        });
    }
    

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

   openRideModal(u: AdminRide) {
    this.selectedRide = u;
    this.modalOpen = true;
    console.log('Selected ride:', this.selectedRide);
   }

}