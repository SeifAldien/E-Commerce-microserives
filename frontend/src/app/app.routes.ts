import { Routes } from '@angular/router';
import { ProductsComponent } from './pages/products.component';
import { CartComponent } from './pages/cart.component';
import { LoginComponent } from './pages/login.component';

export const routes: Routes = [
  { path: '', redirectTo: 'products', pathMatch: 'full' },
  { path: 'products', component: ProductsComponent },
  { path: 'cart', component: CartComponent },
  { path: 'login', component: LoginComponent }
];


