import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import { UserService } from '../user/user.service';
import {NgIf} from "@angular/common";
import {sha256} from "js-sha256";
import {NotificationService} from "../notification/notification.service";
import {JwtService} from "../jwt/jwt.service"
import {RoleService} from "../user/role/role.service";
import {Router} from "@angular/router";
import {UserDataService} from "../user/data/user-data.service";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrl: './registration.component.css'
})

export class RegistrationComponent {
  registration: FormGroup = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    role: ['', Validators.required],
  });


  constructor(private fb: FormBuilder,
              private userService: UserService,
              private notificationService: NotificationService,
              private jwtService: JwtService,
              private roleService: RoleService,
              private router: Router,
              private userData: UserDataService) {
  }

  //TODO: saving of JWT token

  Register(): void {
    let values = this.registration.value;
    this.registration.reset();

    values.password = sha256(values.password);

    this.userService.createUser(values).subscribe({
        next: (response: any) => {

          // TODO: maybe exists better solution???
          this.jwtService.saveToken(response.token);
          this.roleService.saveRole(response.role);
          this.userData.saveName(response.username);
          this.userData.saveEmail(response.email);

          this.router.navigate([''])
            .then( () => this.notificationService.showSuccess('Form submitted successfully!') );
          },
        error: err => {
          this.notificationService.showError(err.error.message);
        }
      }
    );

  }
}
