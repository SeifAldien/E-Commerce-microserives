import { HttpInterceptorFn } from '@angular/common/http';

export const appInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  const isAuth = req.url.includes('/auth/');
  const target = req.url.startsWith('/api/') ? req.clone() : req.clone({ url: '/api' + (req.url.startsWith('/') ? '' : '/') + req.url });
  if (token && !isAuth) {
    return next(target.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
  }
  return next(target);
};


