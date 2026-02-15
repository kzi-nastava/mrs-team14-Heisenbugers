import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DriverRegistrationComponent } from './driver-registration.component';
import {DriverRegistrationService} from '../../../services/driver-registration.service';
import {NgIconComponent, provideIcons} from '@ng-icons/core';
import { bootstrapCameraFill } from '@ng-icons/bootstrap-icons';
import {ReactiveFormsModule} from '@angular/forms';
import {of, throwError} from 'rxjs';

fdescribe('DriverRegistrationComponent', () => {
  let component: DriverRegistrationComponent;
  let fixture: ComponentFixture<DriverRegistrationComponent>;
  let mockService: jasmine.SpyObj<DriverRegistrationService>;

  beforeEach(async () => {
    mockService = jasmine.createSpyObj('DriverRegistrationService', ['registerDriver']);

    await TestBed.configureTestingModule({
      imports: [DriverRegistrationComponent,
        ReactiveFormsModule,
        NgIconComponent
      ],
      providers: [
        { provide: DriverRegistrationService, useValue: mockService },
        provideIcons({bootstrapCameraFill}),
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with an invalid form', () => {
    expect(component.form.valid).toBeFalse();
  });

  it('should validate email format', () => {
    const email = component.form.controls.email;
    email.setValue('invalid-email');
    expect(email.hasError('email')).toBeTrue();

    email.setValue('test@example.com');
    expect(email.hasError('email')).toBeFalse();
  });

  it('should call the service when form is valid and submitted', () => {
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '123456789',
      address: '123 Main St',
      model: 'Tesla Model 3',
      type: 'STANDARD',
      plateNumber: 'ABC-123',
      seats: '4'
    });

    mockService.registerDriver.and.returnValue(of({}));

    component.submit();

    expect(mockService.registerDriver).toHaveBeenCalled();
    expect(component.submitted).toBeTrue();
  });

  it('should not call the service if the form is invalid', () => {
    component.submit();
    expect(mockService.registerDriver).not.toHaveBeenCalled();
    expect(component.submitAttempted).toBeTrue();
  });

  it('should handle service errors gracefully', () => {
    component.form.patchValue({ firstName: 'John' /* ... other fields */ });

    spyOnProperty(component.form, 'invalid').and.returnValue(false);

    mockService.registerDriver.and.returnValue(throwError(() => ({ status: 400 })));

    component.submit();

    expect(component.submitted).toBeFalse();
  });

  it('should update imagePreview when a file is picked', (done) => {
    const blob = new Blob([''], { type: 'image/png' });
    const file = new File([blob], 'test.png');
    const event = { target: { files: [file] } } as unknown as Event;

    component.onPickImage(event);

    setTimeout(() => {
      expect(component.imagePreview).toBeTruthy();
      done();
    }, 100);
  });
});
