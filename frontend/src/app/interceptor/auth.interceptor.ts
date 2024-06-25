import { HttpInterceptorFn } from '@angular/common/http';
import {catchError} from "rxjs/operators";
import {throwError} from "rxjs";
import {inject} from "@angular/core";
import {JwtService} from "../jwt/jwt.service";

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const jwtService = inject(JwtService);
  let token = jwtService.getToken();
  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
  return next(request);
};
