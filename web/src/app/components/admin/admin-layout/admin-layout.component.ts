import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdminPanicService } from '../../../services/admin-panic.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  templateUrl: './admin-layout.component.html',
})
export class AdminLayoutComponent implements OnInit, OnDestroy {
  private api = inject(AdminPanicService);


  hasAlert = false;

  soundEnabled = false;
  private lastSignature: string | null = null;
  private intervalId: any;

  ngOnInit(): void {
    this.poll(true);
    this.intervalId = setInterval(() => this.poll(false), 4000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) clearInterval(this.intervalId);
  }

  enableSound() {
    this.soundEnabled = true;
    new Audio('sounds/panic.mp3').play().catch(() => {});
  }

  private async poll(initial: boolean) {
    try {
      const panics = await this.api.getActivePanics().toPromise();
      const notifs = await this.api.getUnreadNotifications().toPromise();

      const pCount = (panics ?? []).length;
      const nCount = (notifs ?? []).length;

      this.hasAlert = (pCount + nCount) > 0;


      const latestP = (panics ?? [])[0]?.id ?? '';
      const latestN = (notifs ?? [])[0]?.id ?? '';
      const signature = `${latestP}|${latestN}|${pCount}|${nCount}`;

      if (!initial && this.lastSignature && signature !== this.lastSignature) {
        this.playSound();
      }
      this.lastSignature = signature;

    } catch {
      // не ломаем UI
    }
  }

  private playSound() {
    if (!this.soundEnabled) return;
    new Audio('sounds/panic.mp3').play().catch(() => {});
  }
}
