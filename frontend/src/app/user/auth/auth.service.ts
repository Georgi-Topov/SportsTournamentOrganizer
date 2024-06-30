import { Injectable } from '@angular/core';
import {JwtService} from "../../jwt/jwt.service";
import {RoleService} from "../role/role.service";
import {UserDataService} from "../data/user-data.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private jwtService: JwtService,
              private roleService: RoleService,
              private userData: UserDataService) { }

  login(token : string, role : string, username: string, email: string) {
    this.jwtService.saveToken(token);
    this.roleService.saveRole(role);
    this.userData.saveName(username);
    this.userData.saveEmail(email);
  }

  logout() {
    this.jwtService.removeToken();
    this.roleService.removeRole();
    this.userData.removeEmail();
    this.userData.removeName();
  }
}
