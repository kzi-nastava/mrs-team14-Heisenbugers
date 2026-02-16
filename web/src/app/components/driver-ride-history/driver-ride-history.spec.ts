import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RideHistoryComponent } from './driver-ride-history';

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

  it('submitRateForm - jasmine success: calls sendRate and shows success toast and closes modal', () => {
  (component as any).ratingRide = { rideId: 'ride-jasmine-123' } as any;

  const showToastSpy = spyOn(component as any, 'showToast');
  const closeSpy = spyOn(component as any, 'closeRateModal');

  const sendRateSpy = jasmine.createSpy('sendRate').and.returnValue({
    subscribe: (callbacks: any) => { callbacks.next && callbacks.next(); }
  });

  (component as any).rateService = { sendRate: sendRateSpy } as any;

  const formData = { driverRate: 4, vehicleRate: 3, comment: 'great' };
  component.submitRateForm(formData);

  expect(sendRateSpy).toHaveBeenCalledWith('ride-jasmine-123', {
    driverScore: 4,
    vehicleScore: 3,
    comment: 'great'
  });
  expect(showToastSpy).toHaveBeenCalledWith('Rating recorded successfully!');
  expect(closeSpy).toHaveBeenCalled();
});

it('submitRateForm - jasmine 409 error: shows "You have already rated this ride" and closes modal', () => {
  (component as any).ratingRide = { rideId: 'ride-jasmine-409' } as any;

  const showToastSpy = spyOn(component as any, 'showToast');
  const closeSpy = spyOn(component as any, 'closeRateModal');

  const sendRateSpy = jasmine.createSpy('sendRate').and.returnValue({
    subscribe: (callbacks: any) => { callbacks.error && callbacks.error({ status: 409 }); }
  });

  (component as any).rateService = { sendRate: sendRateSpy } as any;

  const formData = { driverRate: 5, vehicleRate: 5, comment: 'ok' };
  component.submitRateForm(formData);

  expect(sendRateSpy).toHaveBeenCalledWith('ride-jasmine-409', {
    driverScore: 5,
    vehicleScore: 5,
    comment: 'ok'
  });
  expect(showToastSpy).toHaveBeenCalledWith('You have already rated this ride');
  expect(closeSpy).toHaveBeenCalled();
});

it('submitRateForm - jasmine non-409 error: shows generic failure message and closes modal', () => {
  (component as any).ratingRide = { rideId: 'ride-jasmine-err' } as any;

  const showToastSpy = spyOn(component as any, 'showToast');
  const closeSpy = spyOn(component as any, 'closeRateModal');

  const sendRateSpy = jasmine.createSpy('sendRate').and.returnValue({
    subscribe: (callbacks: any) => { callbacks.error && callbacks.error({ status: 500 }); }
  });

  (component as any).rateService = { sendRate: sendRateSpy } as any;

  const formData = { driverRate: 2, vehicleRate: 2, comment: 'bad' };
  component.submitRateForm(formData);

  expect(sendRateSpy).toHaveBeenCalledWith('ride-jasmine-err', {
    driverScore: 2,
    vehicleScore: 2,
    comment: 'bad'
  });
  expect(showToastSpy).toHaveBeenCalledWith('Failed to record rating');
  expect(closeSpy).toHaveBeenCalled();
});
});


