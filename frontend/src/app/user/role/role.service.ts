import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  readonly key = "role";

  constructor() { }

  saveRole(role: string) {
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
}
