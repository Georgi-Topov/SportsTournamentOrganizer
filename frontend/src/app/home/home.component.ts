import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from "@angular/router";
import {NgIf} from "@angular/common";
import {RoleService} from "../user/role/role.service";
import {JwtService} from "../jwt/jwt.service";
import {NotificationService} from "../notification/notification.service";

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
  constructor(public roleService: RoleService,
              private jwtService: JwtService,
              private notificationService: NotificationService) {
  }

  logout() {
    this.jwtService.removeToken();
    this.roleService.removeRole();
    this.notificationService.showInfo("Ops!", "You're unauthorized")
  }
}
