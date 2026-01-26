import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeContainer } from './home-container.component';

describe('HomeContainer', () => {
  let component: HomeContainer;
  let fixture: ComponentFixture<HomeContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeContainer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeContainer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
