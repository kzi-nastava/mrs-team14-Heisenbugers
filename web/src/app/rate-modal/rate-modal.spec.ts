import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RateModal } from './rate-modal';

describe('RateModal', () => {
  let component: RateModal;
  let fixture: ComponentFixture<RateModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RateModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RateModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
