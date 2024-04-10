package com.shopping.Ecommerce.service;

import com.shopping.Ecommerce.entity.Cart;
import com.shopping.Ecommerce.entity.CartItem;
import com.shopping.Ecommerce.entity.Customer;
import com.shopping.Ecommerce.entity.Product;
import com.shopping.Ecommerce.exception.ExistsException;
import com.shopping.Ecommerce.exception.ServiceResponse;
import com.shopping.Ecommerce.repository.CartRepository;
import com.shopping.Ecommerce.repository.CustomerRepository;
import com.shopping.Ecommerce.repository.ProductRepository;
import com.shopping.Ecommerce.request.CartRequest;
import com.shopping.Ecommerce.response.CartItemCreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;


    private Customer getUserFromToken(String token) {
        Customer customer = customerRepository.findByToken(token);
        if (customer != null) {
            return customer;
        }
        return null;
    }


    public ServiceResponse<CartItemCreateResponse> createCartItem(HttpServletRequest req, CartRequest request){
        String token = req.getHeader("Authorization");
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, "Invalid token or user not found.", HttpStatus.UNAUTHORIZED);
        }

        int id = customer.getId();
        int productId = request.getProductId();
        int quantity = request.getQuantity();

        if (productId <= 0 || quantity <= 0) {
            return new ServiceResponse<>(null, "Invalid product ID or quantity.", HttpStatus.BAD_REQUEST);
        }

        Optional<Product> existingProduct = productRepository.findById(productId);

        if (existingProduct.isEmpty()) {
            return new ServiceResponse<>(null, "Product with ID " + productId + " not found.", HttpStatus.NOT_FOUND);
        }

        Cart cart = customer.getCart();
        if (cart == null) {
            // Create a new cart for the customer if it doesn't exist
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Product already exists, update the quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            BigDecimal itemPrice = existingProduct.get().getPrice();
            cartItem.setTotalPrice(itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(existingProduct.get());
            newCartItem.setQuantity(quantity);
            BigDecimal itemPrice = existingProduct.get().getPrice();
            newCartItem.setTotalPrice(itemPrice.multiply(BigDecimal.valueOf(quantity)));
            newCartItem.setCart(cart);
            cart.getCartItems().add(newCartItem);
        }

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);


        cartRepository.save(cart);

        CartItemCreateResponse response = new CartItemCreateResponse();
        response.setCartId(cart.getId());
        response.setProductName(existingProduct.get().getName());
        response.setQuantity(quantity);
        response.setAmount(existingProduct.get().getPrice().multiply(BigDecimal.valueOf(quantity)));
        response.setProductId(productId);
        return new ServiceResponse<>(response, "Cart item added successfully.", HttpStatus.OK);



    }
}
