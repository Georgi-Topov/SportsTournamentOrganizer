import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import { SportType } from "./sport-type"

@Injectable({
  providedIn: 'root'
})
export class SportTypeService {

  constructor(private http: HttpClient) { }

  getSportTypes(): Observable<Array<SportType>> {
    return this.http.get<Array<SportType>>("api/sport-type");
  }
}
