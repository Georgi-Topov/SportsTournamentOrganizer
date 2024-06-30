import { Component } from '@angular/core';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.css'
})
export class LoaderComponent {
  private loading: boolean = false;

  showLoader(loading: boolean): void {
    this.loading = loading;
  }

  isLoading() : boolean {
    return this.loading;
  }
}
