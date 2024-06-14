import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private toastr: ToastrService) {}

  showSuccess(message: string) {
      this.toastr.success(message, "Successfully Registered");
  }

  showError(message: string) {
      this.toastr.error(message, "Something went wrong");
  }
}
