import { Component } from '@angular/core';
import {NgIf} from "@angular/common";
import {RoleService} from "../user/role/role.service";
import {JwtService} from "../jwt/jwt.service";
import {UserDataService} from "../user/data/user-data.service";

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.css'
})
export class PanelComponent {

  constructor(protected roleService: RoleService,
              private jwtService: JwtService,
              protected userData: UserDataService) {
  }
}
