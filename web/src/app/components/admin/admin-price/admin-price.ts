import { HttpClient } from "@angular/common/http";
import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";

@Component({
    selector: 'app-admin-price',
    templateUrl: './admin-price.html',
    imports: [FormsModule],
})
export class AdminPrice {
    standardPrice = 0;
    luxuryPrice = 0;
    vanPrice = 0;

    constructor(private cdr: ChangeDetectorRef, private http: HttpClient) {
        
    }


    loadPrices() {
        this.http.get<{vehicleType: string, startingPrice: number}[]>('http://localhost:8081/api/admin/prices').subscribe({
            next: (data) => {
                data.forEach(p => {
                    if (p.vehicleType === 'STANDARD') this.standardPrice = p.startingPrice;
                    else if (p.vehicleType === 'LUXURY') this.luxuryPrice = p.startingPrice;
                    else if (p.vehicleType === 'VAN') this.vanPrice = p.startingPrice;
                });
                this.cdr.markForCheck();
            },
            error: (e) => {
                console.error('Failed to load prices', e);
            }
        });
    }

     ngOnInit(): void {
        this.loadPrices();
     }
    
}