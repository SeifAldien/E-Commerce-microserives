import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-cart',
  template: `
    <p-card header="Cart">
      <div *ngFor="let i of items" class="p-mb-2">
        {{ i.productId }} x {{ i.quantity }}
      </div>
      <button pButton label="Place Order" (click)="placeOrder()"></button>
    </p-card>
  `
})
export class CartComponent implements OnInit {
  items: any[] = [];
  constructor(private http: HttpClient) {}
  ngOnInit(): void {
    this.load();
  }
  load() {
    this.http.get<any[]>('/api/cart-service/cart').subscribe(d => this.items = d);
  }
  placeOrder() {
    const total = 0; // simplified
    const order = { items: this.items.map(i => ({ productId: i.productId, quantity: i.quantity, price: 0 })), totalAmount: total };
    this.http.post('/api/order-service/orders', order).subscribe(() => {
      this.http.post('/api/cart-service/cart/hold', {}).subscribe();
    });
  }
}


