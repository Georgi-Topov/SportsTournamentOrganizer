import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserDataService {

  readonly keyName = "name";
  readonly keyEmail = "email";

  constructor() { }

  saveName(name: string) {
    localStorage.setItem(this.keyName, name);
  }

  getName(): string | null {
    return localStorage.getItem(this.keyName);
  }

  saveEmail(email: string) {
    localStorage.setItem(this.keyEmail, email);
  }

  getEmail(email: string) : string | null {
    return localStorage.getItem(this.keyEmail);
  }
}
