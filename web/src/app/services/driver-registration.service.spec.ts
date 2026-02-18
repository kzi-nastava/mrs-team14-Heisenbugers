import { TestBed } from '@angular/core/testing';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import { DriverRegistrationService } from './driver-registration.service';
import {provideHttpClient} from '@angular/common/http';

fdescribe('DriverRegistrationService', () => {
  let service: DriverRegistrationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DriverRegistrationService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(DriverRegistrationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request with correct FormData', (done) => {
    const mockFormValue = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '0643145432',
      address: 'Address 1',
      model: 'Toyota',
      type: 'STANDARD',
      plateNumber: 'NS-142-DX',
      seats: '5',
      babiesAllowed: true,
      petsAllowed: false
    };

    const mockImageBase64 = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==';

    service.registerDriver(mockFormValue, mockImageBase64).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne('http://localhost:8081/api/drivers');
    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBeTrue();

    const formData = req.request.body as FormData;

    expect(formData.has('data')).toBeTrue();
    expect(formData.has('image')).toBeTrue();

    const dataBlob = formData.get('data') as Blob;
    const reader = new FileReader();

    reader.onload = () => {
      const result = JSON.parse(reader.result as string);
      expect(result.firstName).toBe("John");
      expect(result.lastName).toBe("Doe");
      expect(result.email).toBe("john@example.com");
      expect(result.phone).toBe("0643145432");
      expect(result.address).toBe("Address 1");
      expect(result.vehicle.licensePlate).toBe("NS-142-DX");
      expect(result.vehicle.vehicleModel).toBe("Toyota");
      expect(result.vehicle.vehicleType).toBe("STANDARD");
      expect(result.vehicle.seatCount).toBe(5);
      expect(result.vehicle.babyTransport).toBe(true);
      expect(result.vehicle.petTransport).toBe(false);
      done();
    };
    reader.readAsText(dataBlob);

    req.flush({ body: formData, status: 'success' });
  });

  it('should handle registration without an image', () => {
    const mockFormValue = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '0643145432',
      address: 'Address 1',
      model: 'Toyota',
      type: 'STANDARD',
      plateNumber: 'NS-142-DX',
      seats: '5',
      babiesAllowed: true,
      petsAllowed: false
    };

    service.registerDriver(mockFormValue, null).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne('http://localhost:8081/api/drivers');
    const formData = req.request.body as FormData;

    expect(formData.has('data')).toBeTrue();
    expect(formData.has('image')).toBeFalse();

    req.flush({ body: formData, status: 'success' });
  });

  it('should correctly map raw form strings to numbers or booleans in the DTO', (done) => {
    const mockFormValue = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '0643145432',
      address: 'Address 1',
      model: 'Toyota',
      type: 'STANDARD',
      plateNumber: 'NS-142-DX',
      seats: '5',
      babiesAllowed: true,
      petsAllowed: false
    };

    service.registerDriver(mockFormValue, null).subscribe();

    const req = httpMock.expectOne('http://localhost:8081/api/drivers');
    const formData = req.request.body as FormData;

    const dataBlob = formData.get('data') as Blob;
    const reader = new FileReader();

    reader.onload = () => {
      const result = JSON.parse(reader.result as string);
      expect(result.vehicle.seatCount).toBe(5);
      expect(result.vehicle.babyTransport).toBe(true);
      expect(result.vehicle.petTransport).toBe(false);
      done();
    };
    reader.readAsText(dataBlob);

    req.flush({});
  });

  it('should emit error when registration fails due to existing email', (done) => {
    const mockFormValue = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '0643145432',
      address: 'Address 1',
      model: 'Toyota',
      type: 'STANDARD',
      plateNumber: 'NS-142-DX',
      seats: '5',
      babiesAllowed: true,
      petsAllowed: false
    };

    service.registerDriver(mockFormValue, null).subscribe({
        next: () => fail('Expected request to error with 409, but it succeeded'),
        error: (err) => {
          expect(err).toBeTruthy();
          expect(err.status).toBe(409);
          expect(err.statusText).toBe('Conflict');
          done();
        }
      });

    const req = httpMock.expectOne('http://localhost:8081/api/drivers');

    const formData = req.request.body as FormData;

    expect(formData.has('data')).toBeTrue();
    expect(formData.has('image')).toBeFalse();

    req.flush({ message: 'Email already exists' }, { status: 409, statusText: 'Conflict'});
  });
});
