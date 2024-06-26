import { Routes } from '@angular/router';
import {RegistrationComponent} from "./registration/registration.component";
import {HomeComponent} from "./home/home.component";
import {LoginComponent} from "./login/login.component";
import {PanelComponent} from "./panel/panel.component";

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegistrationComponent },
  { path: 'auth', component: LoginComponent},
  { path: 'panel', component: PanelComponent },
];
