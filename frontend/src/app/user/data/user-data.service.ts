import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserDataService {

  readonly keyName = "name";
  readonly keyEmail = "email";

  constructor() { }

  saveName(name: string) {
    console.log(name);
    localStorage.setItem(this.keyName, name);
  }

  getName(): string | null {
    return localStorage.getItem(this.keyName);
  }

  removeName() {
    localStorage.removeItem(this.keyName);
  }

  saveEmail(email: string) {
    localStorage.setItem(this.keyEmail, email);
  }

  getEmail(email: string) : string | null {
    return localStorage.getItem(this.keyEmail);
  }

  removeEmail() {
    localStorage.removeItem(this.keyEmail);
  }
}
