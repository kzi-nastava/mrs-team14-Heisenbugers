import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgIcon, provideIcons } from "@ng-icons/core";
import { bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX, bootstrapArrowRight } from '@ng-icons/bootstrap-icons';
import { RideRateInfo } from '../../models/ride.model';

@Component({
    selector: 'app-rate-modal',
    imports: [NgIcon, FormsModule],
    templateUrl: './rate-modal.component.html',
    viewProviders: [provideIcons({ bootstrapExclamationCircleFill, bootstrapChatDots, bootstrapFeather, bootstrapStar, bootstrapStarFill, bootstrapX, bootstrapArrowRight })],
})
export class RateModal {

    driverRate: number = 0;
    vehicleRate: number = 0;
    @Input() ride?: RideRateInfo;
    @Input() isOpen: boolean = false;
    
    @Output() onSubmit = new EventEmitter<any>();

    @Output() onClose = new EventEmitter<void>();

    constructor(private cdr: ChangeDetectorRef) {
        
    }

    closeRateModal() {
        this.onClose.emit();
    }

    submitRateForm(form: NgForm) {
        if(form.valid && this.driverRate > 0 && this.vehicleRate > 0) {
            console.log(form.value, this.driverRate, this.vehicleRate);
            this.onSubmit.emit({
                "comment": form.value.comment,
                "driverRate": this.driverRate,
                "vehicleRate": this.vehicleRate
            })
            if (this.ride != null) {
                this.ride.rated = true;
                this.closeRateModal();
            }
            
        }
    }

    getDriverRateArray(): boolean[] {
        return Array(this.driverRate).fill(true).concat(Array(5 - this.driverRate).fill(false));
    }

    setDriverRate(value: number) {
        console.log(this.ride)
        this.driverRate = value;
    }

    getVehicleRateArray(): boolean[] {
        return Array(this.vehicleRate).fill(true).concat(Array(5 - this.vehicleRate).fill(false));
    }

    setVehicleRate(value: number) {
        this.vehicleRate = value;
    }

    getRideDurationMinutes(ride: any): string {
        if(!ride?.startTime || !ride?.endTime) return '';
        const start = ride.startTime.getTime();
        const end = ride.endTime.getTime();

        if (isNaN(start) || isNaN(end)) return '';

        const minutes = Math.floor((end - start) / 60000); // only minutes
        return String(minutes);
    }

  
}
