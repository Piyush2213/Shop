package com.shopping.ecommerce.service;

import com.shopping.ecommerce.entity.Cart;
import com.shopping.ecommerce.entity.CartItem;
import com.shopping.ecommerce.entity.Customer;
import com.shopping.ecommerce.entity.Product;
import com.shopping.ecommerce.repository.CartRepository;
import com.shopping.ecommerce.response.CartItemListResponse;
import com.shopping.ecommerce.response.CartItemResponse;
import com.shopping.ecommerce.response.ServiceResponse;
import com.shopping.ecommerce.repository.CartItemRepository;
import com.shopping.ecommerce.repository.CustomerRepository;
import com.shopping.ecommerce.repository.ProductRepository;
import com.shopping.ecommerce.request.CartRequest;
import com.shopping.ecommerce.response.CartItemCreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid token or user not found.";

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartService(CustomerRepository customerRepository, ProductRepository productRepository,
                       CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    public ServiceResponse<CartItemCreateResponse> createCartItem(HttpServletRequest req, CartRequest request){
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN_MESSAGE, HttpStatus.UNAUTHORIZED);
        }

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
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst();

        if (existingCartItem.isPresent()) {
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

    public ServiceResponse<CartItemListResponse> getAllCartItems(HttpServletRequest req) {
        String token = req.getHeader(AUTHORIZATION_HEADER);

        Customer customer = getUserFromToken(token);
        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN_MESSAGE, HttpStatus.UNAUTHORIZED);
        }

        List<CartItem> cartItems = new ArrayList<>();
        Cart cart = customer.getCart();
        if (cart != null) {
            cartItems = cart.getCartItems();
        }

        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product != null) {
                calculateAmount(cartItem);
                CartItemResponse response = new CartItemResponse();
                response.setId(cartItem.getId());
                response.setProductId(product.getId());
                response.setImageURL(product.getImageURL());
                response.setProductName(product.getName());
                response.setQuantity(cartItem.getQuantity());
                response.setAmount(cartItem.getTotalPrice());
                cartItemResponses.add(response);
                totalAmount = totalAmount.add(cartItem.getTotalPrice());
            }
        }

        CartItemListResponse cartItemListResponse = new CartItemListResponse(cartItemResponses, totalAmount);

        return new ServiceResponse<>(cartItemListResponse, "Response Generated", HttpStatus.OK);
    }

    public ServiceResponse<CartItemResponse> getCartItems(HttpServletRequest req, Integer itemId){
        String token = req.getHeader(AUTHORIZATION_HEADER);

        Customer customer = getUserFromToken(token);
        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN_MESSAGE, HttpStatus.UNAUTHORIZED );
        }

        List<CartItem> cartItems = customer.getCart().getCartItems();

        Optional<CartItem> foundItem = cartItems.stream().filter(item -> item.getId() == itemId).findFirst();
        if (foundItem.isPresent()) {
            CartItem cartItem = foundItem.get();
            CartItemResponse response = new CartItemResponse();
            response.setId(cartItem.getId());
            response.setProductName(cartItem.getProduct().getName());
            response.setImageURL(cartItem.getProduct().getImageURL());
            response.setQuantity(cartItem.getQuantity());
            response.setAmount(cartItem.getTotalPrice());
            response.setProductId(cartItem.getProduct().getId());
            return new ServiceResponse<>(response, "CartItem found.", HttpStatus.FOUND);
        } else {
            return new ServiceResponse<>(null, "CartItem not found.", HttpStatus.NOT_FOUND);
        }
    }

    public ServiceResponse<CartItemResponse> updateCartItem(HttpServletRequest req, Integer itemId, CartItem updatedCartItem){
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);
        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN_MESSAGE, HttpStatus.UNAUTHORIZED );
        }

        Cart cart = customer.getCart();
        if (cart == null) {
            return new ServiceResponse<>(null, "Cart not found for the user.", HttpStatus.NOT_FOUND);
        }

        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            return new ServiceResponse<>(null, "Cart items not found.", HttpStatus.NOT_FOUND);
        }

        Optional<CartItem> cartItemOptional = cartItems.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(updatedCartItem.getQuantity());

            BigDecimal itemPrice = cartItem.getProduct().getPrice();
            BigDecimal totalPrice = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            cartItem.setTotalPrice(totalPrice);

            BigDecimal totalAmount = cartItems.stream()
                    .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotalAmount(totalAmount);

            cartRepository.save(cart);

            CartItemResponse response = new CartItemResponse();
            response.setId(cartItem.getId());
            response.setProductName(cartItem.getProduct().getName());
            response.setImageURL(cartItem.getProduct().getImageURL());
            response.setQuantity(cartItem.getQuantity());
            response.setAmount(totalPrice);
            response.setProductId(cartItem.getProduct().getId());

            return new ServiceResponse<>(response, "Cart item updated successfully.", HttpStatus.OK);
        } else {
            return new ServiceResponse<>(null, "Cart item not found.", HttpStatus.NOT_FOUND);
        }
    }


    public ServiceResponse<String> deleteCartItem(HttpServletRequest req, Integer itemId){
        String token = req.getHeader(AUTHORIZATION_HEADER);
        Customer customer = getUserFromToken(token);

        if (customer == null) {
            return new ServiceResponse<>(null, INVALID_TOKEN_MESSAGE, HttpStatus.UNAUTHORIZED);
        }

        Cart cart = customer.getCart();
        if (cart == null) {
            return new ServiceResponse<>(null, "Cart not found for the user.", HttpStatus.NOT_FOUND);
        }

        List<CartItem> cartItems = cart.getCartItems();
        Optional<CartItem> cartItemOptional = cartItems.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItems.remove(cartItem);

            cartItemRepository.delete(cartItem);

            BigDecimal totalAmount = cartItems.stream()
                    .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotalAmount(totalAmount);

            cartRepository.save(cart);

            return new ServiceResponse<>(null, "Cart item deleted successfully.", HttpStatus.OK);
        } else {
            return new ServiceResponse<>(null, "Cart item not found.", HttpStatus.NOT_FOUND);
        }
    }

    public BigDecimal calculateAmount(CartItem cartItem) {
        int productId = cartItem.getProduct().getId();
        int quantity = cartItem.getQuantity();

        Product product = productRepository.findById(productId).orElse(null);

        if (product != null) {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        } else {
            return BigDecimal.ZERO;
        }
    }

    private Customer getUserFromToken(String token) {
        return customerRepository.findByToken(token);
    }
}
