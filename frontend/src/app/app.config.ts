import {ApplicationConfig, ErrorHandler, InjectionToken, Injector, NgZone} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors} from "@angular/common/http";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {provideAnimations} from "@angular/platform-browser/animations";
import {provideToastr} from "ngx-toastr";
import {LoaderComponent} from "./loader/loader.component";
import {authInterceptor} from "./interceptor/auth.interceptor";
import {GlobalErrorHandler} from "./global-error-handler/global-error-handler";

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes), provideClientHydration(),  provideHttpClient(withFetch()), provideAnimationsAsync(),
    provideAnimations(), provideToastr({
      positionClass: "toast-bottom-right",
      preventDuplicates: true,
      progressBar: true,
      closeButton: true,
    }),
    provideHttpClient(withInterceptors([authInterceptor])),
    { provide: ErrorHandler, useClass: GlobalErrorHandler, deps: [Injector, NgZone] },
  ]
};
