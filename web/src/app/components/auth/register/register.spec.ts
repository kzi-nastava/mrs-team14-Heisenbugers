import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { RegisterComponent } from './register';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

// Вытаскиваем значения из FormData в объект, чтобы удобно сравнивать
function formDataToObject(fd: FormData): Record<string, any> {
  const obj: Record<string, any> = {};
  fd.forEach((v, k) => (obj[k] = v));
  return obj;
}

fdescribe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authSpy = jasmine.createSpyObj<AuthService>('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const valid = {
    firstName: 'AAA',
    lastName: 'AAAAAA',
    email: 'AAAA@test.com',
    phone: '+88005553555',
    address: 'Novi Sad 12',
    password: 'password',
    confirmPassword: 'password',
  };

  function fillValidForm() {
    component.form.setValue(valid);
  }

  it('Creates', () => {
    expect(component).toBeTruthy();
  });

  it('Form invalid when empty  (негатив)', () => {
    expect(component.form.invalid).toBeTrue();
  });

  // --------------------
  // VALIDATION
  // --------------------

  it('firstName: required + maxLength(10) boundary (poz+neg+граничащий)', () => {
    const c = component.form.controls.firstName;

    c.setValue('');
    expect(c.hasError('required')).toBeTrue();

    c.setValue('1234567890'); // 10 ok
    expect(c.valid).toBeTrue();

    c.setValue('12345678901'); // 11 invalid
    expect(c.hasError('maxlength')).toBeTrue();
  });

  it('email: format validation (neg+poz)', () => {
    const c = component.form.controls.email;

    c.setValue('bad');
    expect(c.hasError('email')).toBeTrue();

    c.setValue('ok@test.com');
    expect(c.hasError('email')).toBeFalse();
  });

  it('phone: pattern validation (neg+poz)', () => {
    const c = component.form.controls.phone;

    c.setValue('123'); // too short for pattern
    expect(c.hasError('pattern')).toBeTrue();

    c.setValue('+8 (800) 555-3-555');
    expect(c.hasError('pattern')).toBeFalse();
  });

  it('password: minLength(6) boundary  (границчащий)', () => {
    const c = component.form.controls.password;

    c.setValue('12345');
    expect(c.hasError('minlength')).toBeTrue();

    c.setValue('123456');
    expect(c.hasError('minlength')).toBeFalse();
  });

  it('passwordMismatch validator should set form error  (neg)', () => {
    component.form.patchValue({ password: 'secret1', confirmPassword: 'secret2' });
    component.form.updateValueAndValidity();

    expect(component.form.errors?.['passwordMismatch']).toBeTrue();
  });

  // --------------------
  // SUBMIT BEHAVIOR
  // --------------------

  it('submit(): invalid form -> no service call  (poz+neg)', () => {
    authSpy.register.and.returnValue(of({} as any));

    component.submit();

    expect(authSpy.register).not.toHaveBeenCalled();
    expect(component.submitAttempted).toBeTrue();
  });

  it('submit(): valid form -> calls AuthService.register with correct FormData  (poz)', () => {
    fillValidForm();

    authSpy.register.and.callFake((fd: FormData) => {
      const obj = formDataToObject(fd);

      expect(obj['email']).toBe(valid.email);
      expect(obj['password']).toBe(valid.password);
      expect(obj['confirmPassword']).toBe(valid.confirmPassword);
      expect(obj['firstName']).toBe(valid.firstName);
      expect(obj['lastName']).toBe(valid.lastName);
      expect(obj['phone']).toBe(valid.phone);
      expect(obj['address']).toBe(valid.address);


      expect(obj['profileImage']).toBeUndefined();

      return of({} as any);
    });

    component.submit();

    expect(authSpy.register).toHaveBeenCalledTimes(1);
    expect(component.submitted).toBeTrue();
    expect(component.serverError).toBeNull();
  });

  it('submit(): includes profileImage when selectedImageFile exists  (poz)', () => {
    fillValidForm();

    const file = new File([new Blob(['x'], { type: 'image/png' })], 'avatar.png', { type: 'image/png' });
    component.selectedImageFile = file;

    authSpy.register.and.callFake((fd: FormData) => {
      expect(fd.get('profileImage')).toBe(file);
      return of({} as any);
    });

    component.submit();

    expect(authSpy.register).toHaveBeenCalledTimes(1);
    expect(component.submitted).toBeTrue();
  });



  // ============================================================
  // 3) UI
  // ============================================================


  it('Shows success message when submitted=true  (poz)', () => {
    const f = TestBed.createComponent(RegisterComponent);
    const c = f.componentInstance;

    c.submitted = true;

    f.detectChanges();

    expect((f.nativeElement as HTMLElement).textContent)
      .toContain('Activation email sent');
  });

  it('Renders serverError in template   (neg)', () => {
    const f = TestBed.createComponent(RegisterComponent);
    const c = f.componentInstance;

    c.serverError = 'Email already exists';

    f.detectChanges();

    expect((f.nativeElement as HTMLElement).textContent)
      .toContain('Email already exists');
  });


  // --------------------
  // SERVER ERRORS
  // --------------------

  it('error 409 -> serverError set, submitted=false  (neg+exept)', () => {
    fillValidForm();

    const err = new HttpErrorResponse({
      status: 409,
      error: { message: 'Email already exists' },
    });

    authSpy.register.and.returnValue(throwError(() => err));

    component.submit();

    expect(component.submitted).toBeFalse();
    expect(component.serverError).toBe('Email already exists');
  });

  it('error 400 -> serverError set  (neg)', () => {
    fillValidForm();

    const err = new HttpErrorResponse({
      status: 400,
      error: { message: 'Validation failed' },
    });

    authSpy.register.and.returnValue(throwError(() => err));

    component.submit();

    expect(component.serverError).toBe('Validation failed');
  });

  it('error without message -> default serverError  (exept)', () => {
    fillValidForm();

    const err = new HttpErrorResponse({
      status: 500,
      error: {}, // no message
    });

    authSpy.register.and.returnValue(throwError(() => err));

    component.submit();

    expect(component.serverError).toBe('Registration failed.');
  });

  // --------------------
  // onPickImage
  // --------------------
  it('onPickImage(): sets selectedImageFile and imagePreview  (poz)', (done) => {
    const blob = new Blob([''], { type: 'image/png' });
    const file = new File([blob], 'test.png', { type: 'image/png' });
    const event = { target: { files: [file] } } as unknown as Event;

    component.onPickImage(event);

    // FileReader async
    setTimeout(() => {
      expect(component.selectedImageFile).toBe(file);
      expect(component.imagePreview).toBeTruthy();
      done();
    }, 50);
  });
});
