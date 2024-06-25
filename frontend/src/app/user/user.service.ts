import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "./user";
import {JwtService} from "../jwt/jwt.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient,
              private jwtService: JwtService) {}

  private createHeader() {
    return new Headers({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.jwtService.getToken()}`,
    });

  }

  public createUser(user: any): Observable<any> {
    return this.http.post('/api/users/register', user);
  }

  public authUser(user: any): Observable<any> {
    return this.http.post('/api/users/auth', user);
  }

  public getById(id : number): Observable<User> {
    return this.http.get<User>(`/api/users/${id}`);
  }
}
