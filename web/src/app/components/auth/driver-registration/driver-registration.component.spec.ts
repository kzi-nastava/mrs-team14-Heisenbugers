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

  it('should validate phone pattern and length correctly', () => {
    const phone = component.form.controls.phone;
    phone.setValue('12345');
    expect(phone.hasError('pattern')).toBeTrue();

    phone.setValue('abc-def-ghij');
    expect(phone.hasError('pattern')).toBeTrue();

    phone.setValue('123456');
    expect(phone.hasError('pattern')).toBeFalse();

    phone.setValue('064-313-2342');
    expect(phone.hasError('pattern')).toBeFalse();
  });

  it('should validate firstName is required and enforce max length of 10', () => {
    const first = component.form.controls.firstName;
    first.setValue('');
    expect(first.hasError('required')).toBeTrue();

    first.setValue('A'.repeat(11));
    expect(first.hasError('maxlength')).toBeTrue();

    first.setValue('A'.repeat(10));
    expect(first.hasError('maxlength')).toBeFalse();
    expect(first.valid).toBeTrue();
  });

  it('should validate lastName is required and enforce max length of 40', () => {
    const last = component.form.controls.lastName;
    last.setValue('');
    expect(last.hasError('required')).toBeTrue();

    last.setValue('A'.repeat(41));
    expect(last.hasError('maxlength')).toBeTrue();

    last.setValue('A'.repeat(40));
    expect(last.hasError('maxlength')).toBeFalse();
    expect(last.valid).toBeTrue();
  });

  it('should call the service when form is valid and submitted', () => {
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '0643132342',
      address: 'Address 1',
      model: 'Tesla Model 3',
      type: 'STANDARD',
      plateNumber: 'NS-254-AB',
      seats: '5'
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

  it('should handle service errors', () => {
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '0643132342',
      address: '',
      model: 'Tesla Model 3',
      type: 'STANDARD',
      plateNumber: 'NS-254-AB',
      seats: '5'
    });

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

  it('should return early when no file is picked and not update imagePreview', () => {
    const event = { target: { files: [] } } as unknown as Event;
    component.imagePreview = null;

    component.onPickImage(event);

    expect(component.imagePreview).toBeNull(); });
});
