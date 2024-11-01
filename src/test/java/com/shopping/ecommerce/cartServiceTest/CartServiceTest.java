package com.shopping.ecommerce.cartServiceTest;


import com.shopping.ecommerce.entity.Cart;
import com.shopping.ecommerce.entity.CartItem;
import com.shopping.ecommerce.entity.Customer;
import com.shopping.ecommerce.entity.Product;
import com.shopping.ecommerce.repository.CartItemRepository;
import com.shopping.ecommerce.repository.CartRepository;
import com.shopping.ecommerce.repository.CustomerRepository;
import com.shopping.ecommerce.repository.ProductRepository;
import com.shopping.ecommerce.request.CartRequest;
import com.shopping.ecommerce.response.*;
import com.shopping.ecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCartItem_ValidRequest() {
        String token = "valid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        Customer customer = new Customer();
        customer.setCart(new Cart());
        when(customerRepository.findByToken(token)).thenReturn(customer);

        int productId = 1;
        int quantity = 2;
        CartRequest request = new CartRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(100));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartItemCreateResponse response = new CartItemCreateResponse();
        when(modelMapper.map(any(CartItem.class), eq(CartItemCreateResponse.class))).thenReturn(response);

        ServiceResponse<CartItemCreateResponse> serviceResponse = cartService.createCartItem(httpServletRequest, request);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.OK, serviceResponse.getStatus());
        assertNotNull(serviceResponse.getData());
        assertEquals("Cart item added successfully.", serviceResponse.getMessage());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void createCartItem_InvalidToken() {
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(customerRepository.findByToken(token)).thenReturn(null);

        CartRequest request = new CartRequest();

        ServiceResponse<CartItemCreateResponse> serviceResponse = cartService.createCartItem(httpServletRequest, request);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.UNAUTHORIZED, serviceResponse.getStatus());
        assertNull(serviceResponse.getData());
        assertEquals("Invalid token or user not found.", serviceResponse.getMessage());
    }

    @Test
    void getAllCartItems_ValidToken() {
        String token = "valid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        Customer customer = new Customer();
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(new Product());
        cartItem.setTotalPrice(BigDecimal.valueOf(200));
        cart.setCartItems(Arrays.asList(cartItem));
        customer.setCart(cart);
        when(customerRepository.findByToken(token)).thenReturn(customer);

        CartItemListResponse cartItemListResponse = new CartItemListResponse(new ArrayList<>(), BigDecimal.ZERO);
        when(modelMapper.map(any(CartItem.class), eq(CartItemResponse.class))).thenReturn(new CartItemResponse());

        ServiceResponse<CartItemListResponse> serviceResponse = cartService.getAllCartItems(httpServletRequest);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.OK, serviceResponse.getStatus());
        assertNotNull(serviceResponse.getData());
        assertEquals("Response Generated", serviceResponse.getMessage());
    }

    @Test
    void getAllCartItems_InvalidToken() {
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(customerRepository.findByToken(token)).thenReturn(null);

        ServiceResponse<CartItemListResponse> serviceResponse = cartService.getAllCartItems(httpServletRequest);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.UNAUTHORIZED, serviceResponse.getStatus());
        assertNull(serviceResponse.getData());
        assertEquals("Invalid token or user not found.", serviceResponse.getMessage());
    }

    @Test
    void getCartItems_ValidTokenAndItemId() {
        String token = "valid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        Customer customer = new Customer();
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(new Product());
        cart.setCartItems(Arrays.asList(cartItem));
        customer.setCart(cart);
        when(customerRepository.findByToken(token)).thenReturn(customer);

        int itemId = 1;
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(cartItem));
        when(modelMapper.map(any(CartItem.class), eq(CartItemResponse.class))).thenReturn(new CartItemResponse());

        ServiceResponse<CartItemResponse> serviceResponse = cartService.getCartItems(httpServletRequest, itemId);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.FOUND, serviceResponse.getStatus());
        assertNotNull(serviceResponse.getData());
        assertEquals("CartItem found.", serviceResponse.getMessage());
    }

    @Test
    void getCartItems_InvalidToken() {
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(customerRepository.findByToken(token)).thenReturn(null);

        ServiceResponse<CartItemResponse> serviceResponse = cartService.getCartItems(httpServletRequest, 1);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.UNAUTHORIZED, serviceResponse.getStatus());
        assertNull(serviceResponse.getData());
        assertEquals("Invalid token or user not found.", serviceResponse.getMessage());
    }

    @Test
    void updateCartItem_ValidTokenAndItemId() {
        String token = "valid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        Customer customer = new Customer();
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(new Product());
        cart.setCartItems(Arrays.asList(cartItem));
        customer.setCart(cart);
        when(customerRepository.findByToken(token)).thenReturn(customer);

        int itemId = 1;
        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setQuantity(5);
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(cartItem));
        when(modelMapper.map(any(CartItem.class), eq(CartItemResponse.class))).thenReturn(new CartItemResponse());

        ServiceResponse<CartItemResponse> serviceResponse = cartService.updateCartItem(httpServletRequest, itemId, updatedCartItem);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.OK, serviceResponse.getStatus());
        assertNotNull(serviceResponse.getData());
        assertEquals("Cart item updated successfully.", serviceResponse.getMessage());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void updateCartItem_InvalidToken() {
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(customerRepository.findByToken(token)).thenReturn(null);

        ServiceResponse<CartItemResponse> serviceResponse = cartService.updateCartItem(httpServletRequest, 1, new CartItem());

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.UNAUTHORIZED, serviceResponse.getStatus());
        assertNull(serviceResponse.getData());
        assertEquals("Invalid token or user not found.", serviceResponse.getMessage());
    }

    @Test
    void deleteCartItem_ValidTokenAndItemId() {
        String token = "valid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        Customer customer = new Customer();
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cart.setCartItems(Arrays.asList(cartItem));
        customer.setCart(cart);
        when(customerRepository.findByToken(token)).thenReturn(customer);

        int itemId = 1;
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(cartItem));

        ServiceResponse<String> serviceResponse = cartService.deleteCartItem(httpServletRequest, itemId);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.OK, serviceResponse.getStatus());
        assertEquals("Cart item deleted successfully.", serviceResponse.getMessage());

        verify(cartItemRepository, times(1)).delete(any(CartItem.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void deleteCartItem_InvalidToken() {
        String token = "invalid-token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(customerRepository.findByToken(token)).thenReturn(null);

        ServiceResponse<String> serviceResponse = cartService.deleteCartItem(httpServletRequest, 1);

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.UNAUTHORIZED, serviceResponse.getStatus());
        assertNull(serviceResponse.getData());
        assertEquals("Invalid token or user not found.", serviceResponse.getMessage());
    }
}

