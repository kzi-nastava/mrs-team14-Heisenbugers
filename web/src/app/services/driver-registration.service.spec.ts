import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DriverRegistrationService } from './driver-registration.service';

fdescribe('DriverRegistrationService', () => {
  let service: DriverRegistrationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DriverRegistrationService]
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

  it('should send a POST request with correct FormData', () => {
    const mockFormValue = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      phone: '123456',
      address: 'Test St',
      model: 'Toyota',
      type: 'Sedan',
      plateNumber: 'ABC-123',
      seats: '4',
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

    req.flush({ body: formData, status: 'success' });
  });

  it('should handle registration without an image', () => {
    const mockFormValue = {
      firstName: 'Jane',
      seats: '2'
    };

    service.registerDriver(mockFormValue, null).subscribe();

    const req = httpMock.expectOne('http://localhost:8081/api/drivers');
    const formData = req.request.body as FormData;

    expect(formData.has('data')).toBeTrue();
    expect(formData.has('image')).toBeFalse();

    req.flush({});
  });

  it('should correctly map raw form strings to numbers/booleans in the DTO', (done) => {
    const mockFormValue = {
      seats: '5',
      babiesAllowed: true
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
      done();
    };
    reader.readAsText(dataBlob);

    req.flush({});
  });
});
