import { ChangeDetectorRef } from '@angular/core';
import { RateModal } from './rate-modal.component';
import { TestBed, ComponentFixture } from '@angular/core/testing';

fdescribe('RateModal', () => {
  const mockCdr = ({
    markForCheck: () => {},
    detectChanges: () => {}
  } as unknown) as ChangeDetectorRef;

  let fixture: ComponentFixture<RateModal>;
  let component: RateModal;

  beforeEach(async () => {
  await TestBed.configureTestingModule({
    imports: [RateModal]
  })
  .compileComponents();

  fixture = TestBed.createComponent(RateModal);
  component = fixture.componentInstance;

  component.ride = {
      startAddress: '123 Main St',
      endAddress: '456 Elm St',
      price: 100,
      rated: false,
      startTime: new Date(),
      endTime: new Date(),
    };

  fixture.detectChanges();
  await fixture.whenStable();
});

  it('initializes with default values', () => {
    const comp = component
    expect(comp.driverRate).toBe(0);
    expect(comp.vehicleRate).toBe(0);
    expect(comp.isOpen).toBe(false);
  });

  it('sets driver rate and returns correct driver rate array', () => {
    const comp = component
    comp.setDriverRate(3);
    expect(comp.driverRate).toBe(3);
    expect(comp.getDriverRateArray()).toEqual([true, true, true, false, false]);
  });

  it('sets vehicle rate and returns correct vehicle rate array', () => {
    const comp = component
    comp.setVehicleRate(4);
    expect(comp.vehicleRate).toBe(4);
    expect(comp.getVehicleRateArray()).toEqual([true, true, true, true, false]);
  });

  it('closeRateModal emits onClose', () => {
    const comp = component
    spyOn(comp.onClose, 'emit');
    comp.closeRateModal();
    expect(comp.onClose.emit).toHaveBeenCalled();
  });

  it('submitRateForm emits onSubmit and sets ride.rated when valid and rates > 0', () => {
    const comp = component
    comp.ride = { rated: false, startTime: new Date(), endTime: new Date() } as any;
    comp.driverRate = 5;
    comp.vehicleRate = 4;

    spyOn(comp.onSubmit, 'emit');
    spyOn(comp.onClose, 'emit');

    const mockForm = { valid: true, value: { comment: 'Great ride' } } as any;
    comp.submitRateForm(mockForm);

    expect(comp.onSubmit.emit).toHaveBeenCalledWith({
      comment: 'Great ride',
      driverRate: 5,
      vehicleRate: 4
    });
    expect(comp.ride?.rated).toBe(true);
    expect(comp.onClose.emit).toHaveBeenCalled();
  });

  it('submitRateForm does not emit when form invalid or rates are zero', () => {
    const comp = component
    comp.ride = { rated: false, startTime: new Date(), endTime: new Date() } as any;

    spyOn(comp.onSubmit, 'emit');

    comp.driverRate = 3;
    comp.vehicleRate = 3;
    comp.submitRateForm({ valid: false, value: { comment: 'x' } } as any);
    expect(comp.onSubmit.emit).not.toHaveBeenCalled();

    comp.driverRate = 0;
    comp.vehicleRate = 0;
    comp.submitRateForm({ valid: true, value: { comment: 'y' } } as any);
    expect(comp.onSubmit.emit).not.toHaveBeenCalled();
  });

  it('getRideDurationMinutes returns minutes string and handles invalid dates', () => {
    const comp = component

    const start = new Date('2020-01-01T00:00:00Z');
    const end = new Date('2020-01-01T00:02:30Z');
    const rideGood = { startTime: start, endTime: end } as any;
    expect(comp.getRideDurationMinutes(rideGood)).toBe('2');

    const rideBad = { startTime: new Date('invalid'), endTime: end } as any;
    expect(comp.getRideDurationMinutes(rideBad)).toBe('');
  });
});