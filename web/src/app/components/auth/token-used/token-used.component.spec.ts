import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TokenUsedComponent } from './token-used.component';

describe('TokenUsedComponent', () => {
  let component: TokenUsedComponent;
  let fixture: ComponentFixture<TokenUsedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TokenUsedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TokenUsedComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
