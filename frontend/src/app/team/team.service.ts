import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor() { }

  addTeam(team: any) {
    console.log(team);
  }
}
