import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DuringRide } from './during-ride.component';

describe('DuringRide', () => {
  let component: DuringRide;
  let fixture: ComponentFixture<DuringRide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DuringRide]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DuringRide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
