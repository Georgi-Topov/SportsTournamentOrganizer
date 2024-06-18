import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgIf} from "@angular/common";
import {UserService} from "../user/user.service";
import {NotificationService} from "../notification/notification.service";
import {sha256} from "js-sha256";
import {JwtService} from "../jwt/jwt.service";
import {RoleService} from "../user/role/role.service";
import {Router} from "@angular/router";
import {UserDataService} from "../user/data/user-data.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  login: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(private fb: FormBuilder,
              private userService: UserService,
              private notificationService: NotificationService,
              private jwtService: JwtService,
              private roleService: RoleService,
              private router: Router,
              private userData: UserDataService,) {
  }

  logIn() {
    let val = this.login.value;
    val.password = sha256(val.password);

    console.log(val);

    this.userService.authUser(this.login.value).subscribe({
      // TODO: class for response
      next: (response: any) => {
        console.log(response);
        console.log(response.username);
        this.jwtService.saveToken(response.token);
        this.roleService.saveRole(response.role);
        this.userData.saveName(response.username);
        this.userData.saveEmail(response.email);

        this.router.navigate([''])
          .then( () => this.notificationService.showSuccess("You're authorized!") );
      },
      error: err => {
        console.log(JSON.stringify(err));
        this.notificationService.showError(JSON.stringify(err));
      },
  });
  }
}
