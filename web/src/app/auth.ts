import { HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    let token = localStorage.getItem('accessToken');
    let tokenType = "Bearer";

    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `${tokenType} ${token}`
        }
      });
    }

    return next.handle(req);
  }
}
