import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {RoleService} from "../user/role/role.service";
import {JwtService} from "../jwt/jwt.service";
import {UserDataService} from "../user/data/user-data.service";
import {LoaderComponent} from "../loader/loader.component";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NotificationService} from "../notification/notification.service";
import {User} from "../user/user";
import {UserService} from "../user/user.service";
import {UserDetailsComponent} from "../user/user-details/user-details.component";
import {Router} from "@angular/router";
import {StatusCodes} from "http-status-codes";
import {AuthService} from "../user/auth/auth.service";
import {UnauthorizedError} from "../error/unauthorized-error";
import {SportTypeService} from "../sport-type/sport-type.service";
import {response} from "express";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {SportType} from "../sport-type/sport-type";
import {map} from "rxjs";
import {TeamService} from "../team/team.service";

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    UserDetailsComponent
  ],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.css'
})
export class PanelComponent implements OnInit {

  userId: string = "";
  private pattern : RegExp = /^[0-9]+$/;
  protected user: User = {};
  private sportTypes: string[] = [];

  teamCreation: FormGroup = this.fb.group({
    name: ['', Validators.required],
    sportType: ['', Validators.required],
  });

  constructor(protected roleService: RoleService,
              protected userData: UserDataService,
              private notification : NotificationService,
              private userService: UserService,
              private chaneDet: ChangeDetectorRef,
              private fb: FormBuilder,
              private sportTypeService: SportTypeService,
              private teamService: TeamService,
              ) {
  }

  ngOnInit() {
    this.sportTypeService.getSportTypes().subscribe({
      next: (values:Array<SportType>)  => {
        this.sportTypes = values.map(value => value.sportType);
      },
      error: (error:any) => {
        this.notification.showError("Error");
      }
    });
  }

  getSports() : string[] {
    return this.sportTypes;
  }

  addTeam(): void {
    let team = this.teamCreation.value;
    this.teamService.addTeam(team);
  }

  getUserById(): void {
    if (this.pattern.test(this.userId)) {
      this.userService.getById(Number.parseInt(this.userId)).subscribe({
        next: (response: User) => {
          this.user = response;
          this.notification.showSuccess("Success");
          this.chaneDet.detectChanges();
        },
        error: (response) => {
          if (response.status == StatusCodes.FORBIDDEN) {
            throw new UnauthorizedError();
          } else {
            this.notification.showError("Error");
          }
        },
      });
    } else {
      this.notification.showError("Not correct input of user's id");
      this.userId = '';
    }
  }

}
