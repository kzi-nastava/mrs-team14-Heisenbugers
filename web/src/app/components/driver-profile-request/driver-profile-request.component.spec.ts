import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverProfileRequestComponent } from './driver-profile-request.component';

describe('DriverProfileRequestComponent', () => {
  let component: DriverProfileRequestComponent;
  let fixture: ComponentFixture<DriverProfileRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverProfileRequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverProfileRequestComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
