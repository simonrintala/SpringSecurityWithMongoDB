package se.java.security.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.java.security.models.Product;
import se.java.security.services.CacheProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cache")
public class CacheProductController {

    private final CacheProductService productService;

    public CacheProductController(CacheProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        System.out.println("\n===== DEMO: createProduct() =====");
        System.out.println("📝 Creating product: " + product.getName());

        long startTime = System.nanoTime();
        Product createdProduct = productService.createProduct(product);
        long endTime = System.nanoTime();

        System.out.println("⏱️ Response time: " + (endTime - startTime)/1000000 + "ms");
        System.out.println("✅ Created product with ID: " + createdProduct.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                .body(createdProduct);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        System.out.println("\n===== DEMO: getAllProducts() =====");
        System.out.println("📋 Fetching all products");

        long startTime = System.nanoTime();
        List<Product> products = productService.getAllProducts();
        long endTime = System.nanoTime();

        System.out.println("⏱️ Response time: " + (endTime - startTime)/1000000 + "ms");
        System.out.println("📦 Returned " + products.size() + " products");

        return ResponseEntity.ok()
                .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                .body(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        System.out.println("\n===== DEMO: getProductById() =====");
        System.out.println("🔍 Fetching product with ID: " + id);

        long startTime = System.nanoTime();
        Optional<Product> product = productService.getProductById(id);
        long endTime = System.nanoTime();

        System.out.println("⏱️ Response time: " + (endTime - startTime)/1000000 + "ms");

        if (product.isPresent()) {
            System.out.println("✅ Found product: " + product.get().getName());
            return ResponseEntity.ok()
                    .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                    .body(product.get());
        } else {
            System.out.println("❌ Product not found with ID: " + id);
            return ResponseEntity.notFound()
                    .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                    .build();
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        System.out.println("\n===== DEMO: updateProduct() =====");
        System.out.println("🔄 Updating product with ID: " + id);

        long startTime = System.nanoTime();
        Product updatedProduct = productService.updateProduct(id, product);
        long endTime = System.nanoTime();

        System.out.println("⏱️ Response time: " + (endTime - startTime)/1000000 + "ms");
        System.out.println("✅ Updated product: " + updatedProduct.getName());

        return ResponseEntity.ok()
                .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                .body(updatedProduct);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable String id, @RequestBody Product product) {
        System.out.println("\n===== DEMO: patchProduct() =====");
        System.out.println("🩹 Patching product with ID: " + id);

        long startTime = System.nanoTime();
        Product patchedProduct = productService.patchProduct(id, product);
        long endTime = System.nanoTime();

        System.out.println("⏱️ Response time: " + (endTime - startTime)/1000000 + "ms");
        System.out.println("✅ Patched product: " + patchedProduct.getName());

        return ResponseEntity.ok()
                .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                .body(patchedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        System.out.println("\n===== DEMO: deleteProduct() =====");
        System.out.println("🗑️ Deleting product with ID: " + id);

        long startTime = System.nanoTime();
        productService.deleteProduct(id);
        long endTime = System.nanoTime();

        System.out.println("⏱️ Response time: " + (endTime - startTime)/1000000 + "ms");
        System.out.println("✅ Product deleted successfully");

        return ResponseEntity.noContent()
                .header("X-Response-Time", String.valueOf((endTime - startTime)/1000000))
                .build();
    }
}