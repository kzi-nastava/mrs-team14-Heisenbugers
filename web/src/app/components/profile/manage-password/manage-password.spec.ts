import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagePassword } from './manage-password';

describe('ManagePassword', () => {
  let component: ManagePassword;
  let fixture: ComponentFixture<ManagePassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagePassword]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagePassword);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
