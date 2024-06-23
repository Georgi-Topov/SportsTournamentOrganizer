import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from "@angular/router";
import {NgIf} from "@angular/common";
import {RoleService} from "../user/role/role.service";
import {JwtService} from "../jwt/jwt.service";
import {NotificationService} from "../notification/notification.service";
import {load} from "@angular-devkit/build-angular/src/utils/server-rendering/esm-in-memory-loader/loader-hooks";
import {UserDataService} from "../user/data/user-data.service";
import {AuthService} from "../user/auth/auth.service";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterLink,
    RouterOutlet,
    NgIf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  // protected role : string | null = null;

  constructor(public roleService: RoleService,
              private notificationService: NotificationService,
              // protected loader: LoaderComponent,
              private authService: AuthService,
              ) {
  }

  logout() {
    this.authService.logout();
    this.notificationService.showInfo("Ops!", "You're unauthorized")
  }

  // ngOnInit() {
  //   // this.loader.showLoader(true);
  //   // this.role = this.roleService.getRole();
  //   // this.loader.showLoader(false);
  // }
}
