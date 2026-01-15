import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRegistrationComponent } from './driver-registration.component';

describe('DriverRegistrationComponent', () => {
  let component: DriverRegistrationComponent;
  let fixture: ComponentFixture<DriverRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverRegistrationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRegistrationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
