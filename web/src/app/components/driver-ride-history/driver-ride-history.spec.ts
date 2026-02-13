import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RideHistoryComponent } from './driver-ride-history';
import { of, throwError } from 'rxjs';
import { RateService } from '../../services/rate.service';

/*
describe('RideHistory', () => {
  let component: RideHistoryComponent;
  let fixture: ComponentFixture<RideHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(RideHistoryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
*/
  describe('RideHistory', () => {
    let component: RideHistoryComponent;
    let fixture: ComponentFixture<RideHistoryComponent>;
    let mockRateService: { sendRate: jasmine.Spy };

    beforeEach(async () => {
      mockRateService = { sendRate: jasmine.createSpy('sendRate') };

      await TestBed.configureTestingModule({
        imports: [RideHistoryComponent],
        providers: [{ provide: RateService, useValue: mockRateService }]
      }).compileComponents();

      fixture = TestBed.createComponent(RideHistoryComponent);
      component = fixture.componentInstance;
      await fixture.whenStable();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('calls sendRate with mapped payload, closes modal and shows success toast on success', () => {
      mockRateService.sendRate.and.returnValue(of({}));
      spyOn(component, 'showToast');
      spyOn(component, 'closeRateModal');

      component.ratingRide = { rideId: 'ride-123' } as any;
      const formData = { driverRate: 4, vehicleRate: 5, comment: 'Nice' };

      component.submitRateForm(formData);

      expect(mockRateService.sendRate).toHaveBeenCalledWith('ride-123', {
        driverScore: 4,
        vehicleScore: 5,
        comment: 'Nice'
      });
      expect(component.closeRateModal).toHaveBeenCalled();
      expect(component.showToast).toHaveBeenCalledWith('Rating recorded successfully!');
    });

    it('shows "You have already rated this ride" when sendRate errors with 409', () => {
      mockRateService.sendRate.and.returnValue(throwError(() => ({ status: 409 })));
      spyOn(component, 'showToast');
      spyOn(component, 'closeRateModal');

      component.ratingRide = { rideId: 'ride-409' } as any;
      const formData = { driverRate: 1, vehicleRate: 1, comment: 'x' };

      component.submitRateForm(formData);

      expect(component.closeRateModal).toHaveBeenCalled();
      expect(component.showToast).toHaveBeenCalledWith('You have already rated this ride');
      expect(mockRateService.sendRate).toHaveBeenCalled();
    });

    it('shows generic failure message on non-409 error', () => {
      mockRateService.sendRate.and.returnValue(throwError(() => ({ status: 500 })));
      spyOn(component, 'showToast');
      spyOn(component, 'closeRateModal');

      component.ratingRide = { rideId: 'ride-500' } as any;
      const formData = { driverRate: 2, vehicleRate: 3, comment: 'bad' };

      component.submitRateForm(formData);

      expect(component.closeRateModal).toHaveBeenCalled();
      expect(component.showToast).toHaveBeenCalledWith('Failed to record rating');
      expect(mockRateService.sendRate).toHaveBeenCalled();
    });
  });

