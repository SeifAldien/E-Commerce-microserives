package com.example.product.web;

import com.example.product.model.Product;
import com.example.product.repo.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    public record ProductRequest(@NotBlank String name, String description, @Min(0) double price, @Min(0) int stock) {}

    @GetMapping
    public List<Product> list() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product create(@Valid @RequestBody ProductRequest request) {
        Product p = new Product();
        p.setName(request.name());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setStock(request.stock());
        return repository.save(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return repository.findById(id).map(p -> {
            p.setName(request.name());
            p.setDescription(request.description());
            p.setPrice(request.price());
            p.setStock(request.stock());
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


