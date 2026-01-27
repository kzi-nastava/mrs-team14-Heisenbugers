import {Component, OnDestroy, ChangeDetectorRef, NgZone, Output, EventEmitter, Input} from '@angular/core';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {bootstrapPinMapFill, bootstrapPlusSquare, bootstrapXSquare} from '@ng-icons/bootstrap-icons';
import { FormsModule } from '@angular/forms';
import {Observable, Subject, Subscription, of} from 'rxjs';
import {debounceTime, distinctUntilChanged, switchMap, catchError} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-ride-booking',
  imports: [
    NgIcon,
    FormsModule
  ],
  templateUrl: './ride-booking.component.html',
  styleUrl: './ride-booking.component.css',
  viewProviders: [provideIcons({bootstrapPinMapFill, bootstrapPlusSquare, bootstrapXSquare})]
})
export class RideBookingComponent implements OnDestroy {
  startLocation = '';
  endLocation = '';
  waypoints: { value: string }[] = [];
  passengers: { email: string }[] = [{ email: '' }];
  selectedVehicle: 'STANDARD' | 'LUXURY' | 'VAN' | null = null;
  babyAllowedSelected = false;
  petsAllowedSelected = false;
  price = ""

  private startInput$ = new Subject<string>();
  private endInput$ = new Subject<string>();
  private waypointInput$ = new Subject<{ wp: any; value: string }>();
  private subs: Subscription[] = [];

  startSuggestions: { result: any; label: string }[] = [];
  endSuggestions: { result: any; label: string }[] = [];
  waypointSuggestions = new Map<any, { result: any; label: string }[]>();
  startSuggestionsVisible = false;
  endSuggestionsVisible = false;
  waypointSuggestionsVisible = new Map<any, boolean>();

  pins: { lat: number; lng: number; snapToRoad: boolean; popup: string; iconUrl: string }[] = [];

  @Output() pinsChange = new EventEmitter<
    { lat: number; lng: number; snapToRoad: boolean; popup: string; iconUrl: string }[]
  >();

  private emitPins() {
    this.pinsChange.emit([...this.pins]);
  }

  @Input() routeSummary?: { distanceKm?: number; timeMin?: number };

  constructor(private http: HttpClient, private cd: ChangeDetectorRef, private zone: NgZone) {
    this.subs.push(
      this.startInput$
        .pipe(
          debounceTime(500),
          distinctUntilChanged(),
          switchMap((q) => q ? this.searchStreet(q).pipe(catchError(() => of([]))) : of([]))
        )
        .subscribe((res: any[]) => this.handleSearchResults('start', res))
    );

    this.subs.push(
      this.endInput$
        .pipe(
          debounceTime(500),
          distinctUntilChanged(),
          switchMap((q) => q ? this.searchStreet(q).pipe(catchError(() => of([]))) : of([]))
        )
        .subscribe((res: any[]) => this.handleSearchResults('end', res))
    );

    this.subs.push(
      this.waypointInput$
        .pipe(
          debounceTime(500),
          distinctUntilChanged(
            (prev, curr) => prev.value === curr.value
          ),
          switchMap(({ wp, value }) => value ? this.searchStreet(value).pipe(catchError(() => of([])),
            switchMap((res: any[]) => of({ wp, res }))
          ) : of({ wp, res: [] }))
        )
        .subscribe(({ wp, res }: any) => this.handleWaypointResults(wp, res))
    );
  }


  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  addWaypoint() {
    this.waypoints.push({ value: '' });
  }

  removeWaypoint(wp: { value: string }) {
    const idx = this.waypoints.indexOf(wp);
    if (idx >= 0) this.waypoints.splice(idx, 1);
    // also remove any suggestions for removed waypoint
    this.waypointSuggestions.delete(wp);
  }

  reset() {
    this.startLocation = '';
    this.endLocation = '';
    this.waypoints = [];
    this.passengers = [{ email: '' }];
    this.startSuggestions = [];
    this.endSuggestions = [];
    this.waypointSuggestions.clear();
    this.pins = [];
  }

  addPassenger() {
    this.passengers.push({ email: '' });
  }

  removePassenger(p: { email: string }) {
    const idx = this.passengers.indexOf(p);
    if (idx >= 0) this.passengers.splice(idx, 1);
  }

  get primaryPassengerEmail(): string {
    if (!this.passengers[0]) this.passengers[0] = { email: '' };
    return this.passengers[0].email;
  }

  set primaryPassengerEmail(value: string) {
    if (!this.passengers[0]) this.passengers[0] = { email: '' };
    this.passengers[0].email = value;
  }

  get extraPassengers(): { email: string }[] {
    return this.passengers.slice(1);
  }

  selectVehicle(type: 'STANDARD' | 'LUXURY' | 'VAN') {
    this.selectedVehicle = type;
    this.calculatePrice();
    this.cd.detectChanges();
  }

  toggleBabyAllowed() {
    this.babyAllowedSelected = !this.babyAllowedSelected;
  }

  togglePetsAllowed() {
    this.petsAllowedSelected = !this.petsAllowedSelected;
  }

  onStartInput(value: string) {
    this.startLocation = value;
    this.startInput$.next(value);
  }

  onEndInput(value: string) {
    this.endLocation = value;
    this.endInput$.next(value);
  }

  onWaypointInput(wp: any, value: string) {
    wp.value = value;
    this.waypointInput$.next({ wp, value });
  }

  private handleSearchResults(type: 'start' | 'end', res: any[]) {
    console.log(res);
    const list = Array.isArray(res) ? res.slice(0, 5) : [];
    const mapped = list.map(r => ({ result: r, label: r.display_name }));

    this.zone.run(() => {
      if (mapped.length > 1) {
        if (type === 'start') {
          this.startSuggestions = mapped;
          this.startSuggestionsVisible = true;
        } else {
          this.endSuggestions = mapped;
          this.endSuggestionsVisible = true;
        }
      } else if (mapped.length === 1) {

        const resItem = mapped[0].result;
        const lat = parseFloat(resItem.lat);
        const lng = parseFloat(resItem.lon || resItem.lng || resItem.longitude);
        if (type === 'start') {
          this.startSuggestions = [];
          this.startSuggestionsVisible = false;
          if (Number.isFinite(lat) && Number.isFinite(lng)) {
            this.pins.push({ lat, lng, snapToRoad: true, popup: 'Start', iconUrl: 'icons/pin.svg' });
            this.emitPins();
          } else {
            console.warn('Invalid coords for start:', resItem, lat, lng);
          }
         } else {
           this.endSuggestions = [];
           this.endSuggestionsVisible = false;
           if (Number.isFinite(lat) && Number.isFinite(lng)) {
            this.pins.push({ lat, lng, snapToRoad: true, popup: 'End', iconUrl: 'icons/pin.svg' });
            this.emitPins();
          } else {
            console.warn('Invalid coords for end:', resItem, lat, lng);
          }
         }
      } else {
        if (type === 'start') {
          this.startSuggestions = [];
          this.startSuggestionsVisible = false;
        } else {
          this.endSuggestions = [];
          this.endSuggestionsVisible = false;
        }
      }
      try { this.cd.detectChanges(); } catch (e) {}
    });
  }

  private handleWaypointResults(wp: any, res: any[]) {
    const list = Array.isArray(res) ? res.slice(0, 5) : [];
    const mapped = list.map(r => ({ result: r, label: r.display_name }));
    this.zone.run(() => {
      if (mapped.length > 1) {
        this.waypointSuggestions.set(wp, mapped);
        this.waypointSuggestionsVisible.set(wp, true);
      } else {
        this.waypointSuggestions.delete(wp);
        this.waypointSuggestionsVisible.set(wp, false);
      }
      try { this.cd.detectChanges(); } catch (e) {}
    });
  }

  onStartFocus() {
    if (this.startSuggestions && this.startSuggestions.length > 0) {
      this.startSuggestionsVisible = true;
      try { this.cd.detectChanges(); } catch (e) {}
    }
  }

  onEndFocus() {
    if (this.endSuggestions && this.endSuggestions.length > 0) {
      this.endSuggestionsVisible = true;
      try { this.cd.detectChanges(); } catch (e) {}
    }
  }

  onWaypointFocus(wp: any) {
    const arr = this.waypointSuggestions.get(wp);
    if (Array.isArray(arr) && arr.length > 0) {
      this.waypointSuggestionsVisible.set(wp, true);
      try { this.cd.detectChanges(); } catch (e) {}
    }
  }

  isWaypointSuggestionsVisible(wp: any): boolean {
    return !!this.waypointSuggestionsVisible.get(wp);
  }

  private selectSuggestionImpl(type: 'start' | 'end' | 'waypoint', suggestion: any, wp?: any) {
    const res = suggestion.result || suggestion;
    //const label = this.formatStreetLabel(res) || (res.display_name ? res.display_name.split(',')[0] : '');
    const lat = parseFloat(res.lat);
    const lng = parseFloat(res.lon || res.lng || res.longitude);

    this.zone.run(() => {
      if (type === 'start') {
        this.startSuggestions = [];
        this.startSuggestionsVisible = false;
        this.pins.push({ lat, lng, snapToRoad: true, popup: 'Start', iconUrl: "icons/pin.svg" });
        this.emitPins();
      } else if (type === 'end') {
        this.endSuggestions = [];
        this.endSuggestionsVisible = false;
        this.pins.push({ lat, lng, snapToRoad: true, popup: 'End', iconUrl: "icons/pin.svg" });
        this.emitPins();
      } else if (type === 'waypoint' && wp) {
        this.waypointSuggestions.delete(wp);
        this.waypointSuggestionsVisible.set(wp, false);
        const idx = this.waypoints.indexOf(wp);
        const popup = idx >= 0 ? `Waypoint ${idx + 1}` : 'Waypoint';
        this.pins.push({ lat, lng, snapToRoad: true, popup, iconUrl: "icons/pin.svg" });
        this.emitPins();
      }
      try { this.cd.detectChanges(); } catch (e) {}
    });
  }

  public selectSuggestion(type: 'start' | 'end' | 'waypoint', suggestion: any, wp?: any) {
    this.selectSuggestionImpl(type, suggestion, wp);
  }

  public hasWaypointSuggestions(wp: any): boolean {
    const arr = this.waypointSuggestions.get(wp);
    return Array.isArray(arr) && arr.length > 0;
  }

  public formatMinutesToHours(minutes?: number | null): string {
    if (minutes == null || isNaN(minutes as any)) return '';
    const m = Math.round(minutes);
    const h = Math.floor(m / 60);
    const rem = m % 60;
    if (h > 0) return `${h} H ${rem} MIN`;
    return `${rem} MIN`;
  }

  searchStreet(street: string): Observable<any> {
    return this.http.get('http://localhost:8081/api/geocode/search', {
      params: { q: street }
    });
  }

  calculatePrice(): string {
    if (!this.routeSummary || !this.routeSummary.distanceKm) return "";
    const distance = Number(this.routeSummary.distanceKm);
    if(this.selectedVehicle == "STANDARD"){
      this.price = (distance * 120 + 100).toFixed(2);
    } else if(this.selectedVehicle == "LUXURY"){
      this.price = (distance * 120 + 450).toFixed(2);
    } else if(this.selectedVehicle == "VAN"){
      this.price = (distance * 120 + 200).toFixed(2);
    } else {
      this.price = "";
    }
    return this.price;
  }

  orderRide(): void {
    if (!this.routeSummary?.distanceKm || !this.routeSummary?.timeMin) {
      alert('Route is not calculated yet.');
      return;
    }

    if (!this.selectedVehicle) {
      alert('Please select a vehicle type.');
      return;
    }

    if (this.pins.length < 2) {
      alert('Start and destination are required.');
      return;
    }

    const startPin = this.pins.find(p => p.popup === 'Start');
    const endPin = this.pins.find(p => p.popup === 'End');

    if (!startPin || !endPin) {
      alert('Start or end location missing.');
      return;
    }

    const waypointPins = this.pins.filter(p => p.popup.startsWith('Waypoint'));

    const payload = {
      route: {
        start: {
          latitude: startPin.lat,
          longitude: startPin.lng,
          address: this.startLocation
        },
        destination: {
          latitude: endPin.lat,
          longitude: endPin.lng,
          address: this.endLocation
        },
        stops: waypointPins.map((p, i) => ({
          latitude: p.lat,
          longitude: p.lng,
          address: this.waypoints[i]?.value ?? ''
        })),
        distanceKm: this.routeSummary.distanceKm,
        estimatedTimeMin: this.routeSummary.timeMin,
      },
      passengersEmails: this.passengers
        .map(p => p.email)
        .filter(e => e && e.trim().length > 0),
      vehicleType: this.selectedVehicle,
      babyTransport: this.babyAllowedSelected,
      petTransport: this.petsAllowedSelected
    };

    this.http.post('http://localhost:8081/api/rides', payload).subscribe({
      next: (res: any) => {
        console.log('Ride created:', res);
        alert('Ride successfully created!');
        this.reset();
      },
      error: (err) => {
        console.error('Ride creation failed', err);
        alert(err?.error?.message ?? 'Failed to create ride.');
      }
    });
  }
}
