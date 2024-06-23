import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private toastr: ToastrService) {}

  showSuccess(message: string) {
      this.toastr.success(message, "Success");
  }

  showError(message: string) {
      this.toastr.error(message, "Something went wrong");
  }

  showErrorFull(title: string, message: string) {
    this.toastr.error(message, title);
  }


  showInfo(title: string, message: string) {
    this.toastr.info(message, title);
  }
}
