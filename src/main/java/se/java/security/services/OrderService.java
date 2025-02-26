package se.java.security.services;

import org.springframework.stereotype.Service;
import se.java.security.dto.OrderDTO;
import se.java.security.dto.OrderItemDTO;
import se.java.security.dto.OrderResponse;
import se.java.security.exceptions.ResourceNotFoundException;
import se.java.security.models.Order;
import se.java.security.models.Product;
import se.java.security.models.User;
import se.java.security.repository.OrderRepository;
import se.java.security.repository.ProductRepository;
import se.java.security.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // skapa en ny order
    public Order createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // tom lista
        List<Product> products = new ArrayList<>();
        Map<String, Integer> quantities = new HashMap<>();
        double totalAmount = 0.0;

        // gå igenom varenda orderrad (OrderItemDTO) i beställningen
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + itemDTO.getProductId()));

            products.add(product);
            quantities.put(itemDTO.getProductId(), itemDTO.getQuantity());
            totalAmount += product.getPrice() * itemDTO.getQuantity();
        }

        Order newOrder = new Order();
        newOrder.setCustomer(user);
        newOrder.setItems(products);
        newOrder.setQuantities(quantities);
        newOrder.setTotalAmount(totalAmount);

        return orderRepository.save(newOrder);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
       /* List<Order> orders = orderRepository.findAll();

        // konvertera order objekten till orderResponse objekt
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());*/
    }

    public List<OrderResponse> getUserOrders(String userId) {
        /*User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));*/

        if(!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        List<Order> orders = orderRepository.findByCustomerId(userId);

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private OrderResponse convertToDTO(Order order) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setUserId(order.getCustomer().getId());

        orderResponse.setOrderedProductIds(
                order.getItems().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );

        return orderResponse;
        /*OrderResponse orderResponse = new OrderResponse();

        orderResponse.setUserId(order.getCustomer().getId());

        orderResponse.setOrderedProductIds(
                order.getItems().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );
        return orderResponse;*/
    }





















}