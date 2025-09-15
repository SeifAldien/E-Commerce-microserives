import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-products',
  template: `
    <p-table [value]="products">
      <ng-template pTemplate="header">
        <tr>
          <th>Name</th>
          <th>Price</th>
          <th>Stock</th>
          <th></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-p>
        <tr>
          <td>{{ p.name }}</td>
          <td>{{ p.price | currency }}</td>
          <td>{{ p.stock }}</td>
          <td><button pButton type="button" label="Add" (click)="addToCart(p)"></button></td>
        </tr>
      </ng-template>
    </p-table>
  `
})
export class ProductsComponent implements OnInit {
  products: any[] = [];
  constructor(private http: HttpClient) {}
  ngOnInit(): void {
    this.http.get<any[]>('/api/product-service/products').subscribe(d => this.products = d);
  }
  addToCart(p: any) {
    const item = { cartItemId: `${p.productId}`, productId: p.productId, quantity: 1 };
    this.http.post('/api/cart-service/cart', item).subscribe();
  }
}


