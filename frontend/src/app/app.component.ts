import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="p-m-3">
      <h2>Simple E-Commerce</h2>
      <nav class="p-mb-3">
        <a routerLink="/products" class="p-button p-button-text">Products</a>
        <a routerLink="/cart" class="p-button p-button-text">Cart</a>
        <a routerLink="/login" class="p-button p-button-text">Login</a>
      </nav>
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {}


