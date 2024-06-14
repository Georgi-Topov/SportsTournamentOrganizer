import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "./user";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {}

  public createUser(user: any): Observable<User> {
    return this.http.post<User>('/api/users/register', user);
  }

  public authUser(user: any): Observable<any> {
    return this.http.post('/api/users/auth', user);
  }
}
