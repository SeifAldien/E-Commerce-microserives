import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { appInterceptor } from './app/services/jwt.interceptor';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([appInterceptor])),
    provideRouter(routes)
  ]
}).catch(err => console.error(err));


