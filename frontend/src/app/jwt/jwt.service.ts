import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class JwtService {

   readonly key  = "jwtToken";

  constructor() { }

  saveToken(token: string) {
    localStorage.setItem(this.key, token);
  }

  getToken() : string | null {
    return localStorage.getItem(this.key);
  }

  removeToken() {
    localStorage.removeItem(this.key);
  }
}
