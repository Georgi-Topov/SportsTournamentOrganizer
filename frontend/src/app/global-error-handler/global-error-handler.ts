import {ErrorHandler, Injector, NgZone} from "@angular/core";
import {AuthService} from "../user/auth/auth.service";
import {Router} from "@angular/router";
import {NotificationService} from "../notification/notification.service";
import {UnauthorizedError} from "../error/unauthorized-error";

export class GlobalErrorHandler implements ErrorHandler {

  constructor(private injector: Injector,
              private ngZone: NgZone,) {}

  handleError(error: Error): any {
    const authService = this.injector.get(AuthService);
    const router = this.injector.get(Router);
    const notificationService = this.injector.get(NotificationService);
    if (error instanceof UnauthorizedError) {
      this.ngZone.run(() => {
        authService.logout();
        router.navigateByUrl('').then(() => {
          notificationService.showErrorFull("Unauthorized", "You need to authorize again");
        });
      });
    } else {
      return error;
    }
  }
}
