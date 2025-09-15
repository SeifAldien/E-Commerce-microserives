import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  template: `
    <p-card header="Login">
      <div class="p-fluid">
        <div class="p-field">
          <label>Username</label>
          <input type="text" pInputText [(ngModel)]="username" />
        </div>
        <div class="p-field">
          <label>Password</label>
          <input type="password" pInputText [(ngModel)]="password" />
        </div>
        <button pButton label="Login" (click)="login()"></button>
      </div>
    </p-card>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  constructor(private http: HttpClient, private router: Router) {}
  login() {
    this.http.post<any>('/auth-service/auth/login', { username: this.username, password: this.password }).subscribe(r => {
      localStorage.setItem('token', r.token);
      this.router.navigateByUrl('/');
    });
  }
}


