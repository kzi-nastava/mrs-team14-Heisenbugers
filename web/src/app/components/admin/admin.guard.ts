import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  private auth = inject(AuthService);
  private router = inject(Router);


  canActivate(): boolean {

    const role = this.auth.getRole();
    console.log(role);
    const ok = role === 'ADMIN';


    if (!ok) {
      this.router.navigate(['/home']);
      return false;
    }
    console.log('canActivate');
    return true;
  }


}
