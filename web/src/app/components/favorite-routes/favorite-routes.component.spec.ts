import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteRoutesComponent } from './favorite-routes.component';

describe('FavoriteRoutesComponent', () => {
  let component: FavoriteRoutesComponent;
  let fixture: ComponentFixture<FavoriteRoutesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteRoutesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteRoutesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
