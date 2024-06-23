import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  readonly key = "role";
  readonly admin = "admin";
  readonly manager = "manager";
  readonly user  = "user";

  constructor() { }

  saveRole(role: string) {
    console.log(role);
    localStorage.setItem(this.key, role);
  }

  getRole() : string | null {
    return localStorage.getItem(this.key);
  }

  hasRole() : boolean {
    return typeof localStorage !== 'undefined' && localStorage.getItem(this.key) !== null;
  }

  removeRole() {
    localStorage.removeItem(this.key);
  }

  isManager() : boolean {
    return this.hasRole() && this.getRole() == this.manager;
  }

  isAdmin() : boolean {
    return this.hasRole() && this.getRole() == this.admin;
  }

  isUser() : boolean {
    return this.hasRole() && this.getRole() == this.user;
  }
}
