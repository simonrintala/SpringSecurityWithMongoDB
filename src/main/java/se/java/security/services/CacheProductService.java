package se.java.security.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.java.security.exceptions.ResourceNotFoundException;
import se.java.security.models.Product;
import se.java.security.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CacheProductService {
    private final ProductRepository productRepository;

    public CacheProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        System.out.println("🔍 [DATABASE] Validating product data");

        if (product.getName() == null || product.getName().isEmpty()) {
            System.out.println("❌ [DATABASE] Validation failed: Product name cannot be empty");
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            System.out.println("❌ [DATABASE] Validation failed: Price must be greater than 0");
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        System.out.println("🔍 [DATABASE] Saving new product: " + product.getName());
        long dbStartTime = System.nanoTime();
        Product savedProduct = productRepository.save(product);
        long dbEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Saved product with ID: " + savedProduct.getId() +
                " in " + (dbEndTime - dbStartTime)/1000000 + "ms");
        System.out.println("🔄 [CACHE] Evicted all entries from 'products' cache");

        return savedProduct;
    }

    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        System.out.println("🔍 [DATABASE] Fetching all products");
        long dbStartTime = System.nanoTime();
        List<Product> products = productRepository.findAll();
        long dbEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Found " + products.size() + " products in " +
                (dbEndTime - dbStartTime)/1000000 + "ms");
        System.out.println("📥 [CACHE] Storing result in 'products' cache");

        return products;
    }

    @Cacheable(value = "productById", key = "#id")
    public Optional<Product> getProductById(String id) {
        System.out.println("🔍 [DATABASE] Fetching product with ID: " + id);
        long dbStartTime = System.nanoTime();
        Optional<Product> product = productRepository.findById(id);
        long dbEndTime = System.nanoTime();

        if (product.isPresent()) {
            System.out.println("✅ [DATABASE] Found product: " + product.get().getName() +
                    " in " + (dbEndTime - dbStartTime)/1000000 + "ms");
        } else {
            System.out.println("❌ [DATABASE] Product not found with ID: " + id +
                    " in " + (dbEndTime - dbStartTime)/1000000 + "ms");
        }

        System.out.println("📥 [CACHE] Storing result in 'productById' cache with key: " + id);

        return product;
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product updateProduct(String id, Product product) {
        System.out.println("🔍 [DATABASE] Checking if product exists with ID: " + id);
        long findStartTime = System.nanoTime();
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("❌ [DATABASE] Product not found with ID: " + id);
                    return new ResourceNotFoundException("Product not found with id " + id);
                });
        long findEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Found existing product in " +
                (findEndTime - findStartTime)/1000000 + "ms");

        System.out.println("🔄 [DATABASE] Updating product properties");
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());

        System.out.println("🔍 [DATABASE] Saving updated product");
        long saveStartTime = System.nanoTime();
        Product updatedProduct = productRepository.save(existingProduct);
        long saveEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Saved updated product in " +
                (saveEndTime - saveStartTime)/1000000 + "ms");
        System.out.println("🔄 [CACHE] Evicted entries from 'products' and 'productById' caches");

        return updatedProduct;
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public Product patchProduct(String id, Product product) {
        System.out.println("🔍 [DATABASE] Checking if product exists with ID: " + id);
        long findStartTime = System.nanoTime();
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("❌ [DATABASE] Product not found with ID: " + id);
                    return new ResourceNotFoundException("Product not found with id " + id);
                });
        long findEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Found existing product in " +
                (findEndTime - findStartTime)/1000000 + "ms");

        System.out.println("🔄 [DATABASE] Patching product properties");

        if (product.getName() != null) {
            System.out.println("🔄 [DATABASE] Updating name to: " + product.getName());
            existingProduct.setName(product.getName());
        }

        if (product.getPrice() != null) {
            System.out.println("🔄 [DATABASE] Updating price to: " + product.getPrice());
            existingProduct.setPrice(product.getPrice());
        }

        if (product.getDescription() != null) {
            System.out.println("🔄 [DATABASE] Updating description");
            existingProduct.setDescription(product.getDescription());
        }

        System.out.println("🔍 [DATABASE] Saving patched product");
        long saveStartTime = System.nanoTime();
        Product patchedProduct = productRepository.save(existingProduct);
        long saveEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Saved patched product in " +
                (saveEndTime - saveStartTime)/1000000 + "ms");
        System.out.println("🔄 [CACHE] Evicted entries from 'products' and 'productById' caches");

        return patchedProduct;
    }

    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public void deleteProduct(String id) {
        System.out.println("🔍 [DATABASE] Checking if product exists with ID: " + id);
        long findStartTime = System.nanoTime();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("❌ [DATABASE] Product not found with ID: " + id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        long findEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Found product in " +
                (findEndTime - findStartTime)/1000000 + "ms");

        System.out.println("🗑️ [DATABASE] Deleting product: " + product.getName());
        long deleteStartTime = System.nanoTime();
        productRepository.delete(product);
        long deleteEndTime = System.nanoTime();

        System.out.println("✅ [DATABASE] Deleted product in " +
                (deleteEndTime - deleteStartTime)/1000000 + "ms");
        System.out.println("🔄 [CACHE] Evicted entries from 'products' and 'productById' caches");
    }

}