import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {RoleService} from "../user/role/role.service";
import {JwtService} from "../jwt/jwt.service";
import {UserDataService} from "../user/data/user-data.service";
import {LoaderComponent} from "../loader/loader.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NotificationService} from "../notification/notification.service";
import {User} from "../user/user";
import {UserService} from "../user/user.service";
import {UserDetailsComponent} from "../user/user-details/user-details.component";
import {Router} from "@angular/router";
import {StatusCodes} from "http-status-codes";
import {AuthService} from "../user/auth/auth.service";
import {UnauthorizedError} from "../error/unauthorized-error";

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
export class PanelComponent {

  userId: string = "";
  private pattern : RegExp = /^[0-9]+$/;
  protected user: User = {};

  constructor(protected roleService: RoleService,
              private jwtService: JwtService,
              protected userData: UserDataService,
              private notification : NotificationService,
              private userService: UserService,
              private chaneDet: ChangeDetectorRef,
              private router: Router,
              private auth: AuthService,
              ) {
  }

  getUserById(): void {
    if (this.pattern.test(this.userId)) {
      console.log(this.userId);
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
