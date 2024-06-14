import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import { UserService } from '../user/user.service';
import {NgIf} from "@angular/common";
import {User} from "../user/user";
import {sha256} from "js-sha256";
import {NotificationService} from "../notification/notification.service";

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
              private notificationService: NotificationService) {
  }

  //TODO: saving of JWT token

  Register(): void {
    let values = this.registration.value;
    this.registration.reset();

    values.password = sha256(values.password);

    this.userService.createUser(values).subscribe({
        next: (response: User) => {
          this.notificationService.showSuccess('Form submitted successfully!');
        },
        error: err => {
          this.notificationService.showError(err.error.message);
        },
        complete: () => {
          console.log("Done");
        }
      }
    );

  }
}
