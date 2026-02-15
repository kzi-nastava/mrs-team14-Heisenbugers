import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockUsersComponent } from './block-users.component';

describe('BlockUsersComponent', () => {
  let component: BlockUsersComponent;
  let fixture: ComponentFixture<BlockUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlockUsersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockUsersComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
