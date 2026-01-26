import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverDriving } from './driver-driving';

describe('DriverDriving', () => {
  let component: DriverDriving;
  let fixture: ComponentFixture<DriverDriving>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverDriving]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverDriving);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
