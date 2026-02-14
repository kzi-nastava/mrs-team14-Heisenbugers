import { HttpClient } from "@angular/common/http";
import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgZone } from "@angular/core";

@Component({
    selector: 'app-admin-price',
    templateUrl: './admin-price.html',
    imports: [FormsModule],
    standalone: true,
})
export class AdminPrice {
    oldStandardPrice = 0;
    oldLuxuryPrice = 0;
    oldVanPrice = 0;
    standardPrice = 0;
    luxuryPrice = 0;
    vanPrice = 0;
    toastVisible: boolean = false;
    toastMessage: string = '';

    constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private zone: NgZone) {
        
    }


    loadPrices() {
        this.http.get<{vehicleType: string, startingPrice: number}[]>('http://localhost:8081/api/admin/prices').subscribe({
            next: (data) => {
                data.forEach(p => {
                    if (p.vehicleType === 'STANDARD') this.oldStandardPrice = this.standardPrice = p.startingPrice;
                    else if (p.vehicleType === 'LUXURY') this.oldLuxuryPrice = this.luxuryPrice = p.startingPrice;
                    else if (p.vehicleType === 'VAN') this.oldVanPrice = this.vanPrice = p.startingPrice;
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

  

    onSave() {
        const prices: {vehicleType: string, startingPrice: number}[] = [];

        if (this.standardPrice !== this.oldStandardPrice) {
            prices.push({ vehicleType: 'STANDARD', startingPrice: this.standardPrice });
        }
        if (this.luxuryPrice !== this.oldLuxuryPrice) {
            prices.push({ vehicleType: 'LUXURY', startingPrice: this.luxuryPrice });
        }
        if (this.vanPrice !== this.oldVanPrice) {
            prices.push({ vehicleType: 'VAN', startingPrice: this.vanPrice });
        }

        if (prices.length === 0) {
            alert('No changes to save');
            return;
        }

        this.http.post('http://localhost:8081/api/admin/prices', prices).subscribe({
            next: () => {
                console.log('Prices saved successfully');
                alert('Prices saved successfully');
                // update old prices to current ones
                this.oldStandardPrice = this.standardPrice;
                this.oldLuxuryPrice = this.luxuryPrice;
                this.oldVanPrice = this.vanPrice;
            },
            error: (e) => {
                console.error('Failed to save prices', e);
                alert('Failed to save prices');
            }
        });
    }
    
}