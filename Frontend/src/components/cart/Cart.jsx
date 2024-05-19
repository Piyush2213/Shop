import React, { useState, useEffect } from 'react';
import { Trash, Heart } from 'lucide-react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import base_url from '../default/BaseUrl';

import { Footer } from '../footer/Footer';
import AddressEntry from '../address/AddressEntry';
import { useNavigate } from 'react-router-dom';
import { Header2 } from '../header/Header2';

export function Cart() {
    useEffect(() => {
        document.title = "Ebay's Cart";
    }, []);

    const [cartItems, setCartItems] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [userDetails, setUserDetails] = useState(null);
    const [address, setAddress] = useState(null);
    const [toastMessage, setToastMessage] = useState('');
    const token = Cookies.get('token');
    const username = Cookies.get('firstName');
    const navigate = useNavigate();

    useEffect(() => {
        fetchUserCart();
    }, []);

    const fetchUserCart = async () => {
        try {
            if (!token) {
                alert("Login First!")
                throw new Error('You need to log in first.');
            }

            const response = await axios.get(`${base_url}/cart/cartItems`, {
                headers: {
                    Authorization: token,
                },
            });

            setUserDetails(response.data.data.userDetails);
            setCartItems(response.data.data.cartItems);
            setTotalAmount(response.data.data.totalAmount);
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handleRemoveFromCart = async (itemId) => {
        try {
            if (!token) {
                throw new Error('You need to log in first.');
            }

            await axios.delete(`${base_url}/cart/cartItems/${itemId}`, {
                headers: {
                    Authorization: token,
                },
            });
            fetchUserCart();
            toast.success('Product removed from cart.');
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handlePlaceOrder = async () => {
        try {
            if (!token) {
                setToastMessage('You need to log in first.');
                throw new Error('You need to log in first.');
            }

            if (cartItems.length === 0) {
                setToastMessage('Nothing in the cart. Please add items to your cart before placing an order.');
                return;
            }

            if (!address) {
                setToastMessage('Please provide your address before placing the order.');                
                return;
            }

            
            const response = await axios.post(
                `${base_url}/order`,
                address,
                {
                    headers: {
                        Authorization: token,
                    },
                }
            );


            if (response.status === 201) {
                setCartItems([]);
                setTotalAmount(0);
                toast.success('Order placed successfully!');
                navigate('/orders');
            } else {
                throw new Error('Failed to place order.');
            }
        } catch (error) {
            throw new Error('Error placing order:', error);
        }
    };

    return (
        <div>
            <Header2 username={username} token={token} />
            <div className="mx-auto flex max-w-3xl flex-col space-y-4 p-6 px-2 sm:p-10 sm:px-2">
                <AddressEntry onAddressSubmit={setAddress} />
                {toastMessage && (
                    <div className="mt-4 bg-green-100 border border-green-400 text-green-700 px-4 py-2 rounded-md">
                        {toastMessage}
                    </div>
                )}
                <div className="flex flex-col divide-y divide-gray-200">
                    {cartItems && cartItems.map((cartItem) => (
                        <div key={cartItem.id} className="flex flex-col py-6 sm:flex-row sm:justify-between">
                            <div className="flex w-full space-x-2 sm:space-x-4">
                                <img
                                    className="h-20 w-20 flex-shrink-0 rounded object-contain outline-none dark:border-transparent sm:h-32 sm:w-32"
                                    src={cartItem.imageURL}
                                    alt={cartItem.productName}
                                />
                                <div className="flex w-full flex-col justify-between pb-4">
                                    <div className="flex w-full justify-between space-x-2 pb-2">
                                        <div className="space-y-1">
                                            <h3 className="text-lg font-semibold leading-snug sm:pr-8">{cartItem.productName}</h3>
                                            <p className="text-sm">Quantity: {cartItem.quantity}</p>
                                        </div>
                                        <div className="text-right">
                                            <p className="text-lg font-semibold">{cartItem.amount}</p>
                                        </div>
                                    </div>
                                    <div className="flex divide-x text-sm">
                                        <button
                                            type="button"
                                            onClick={() => handleRemoveFromCart(cartItem.id)}
                                            className="flex items-center space-x-2 px-2 py-1 pl-0"
                                        >
                                            <Trash size={16} />
                                            <span>Remove</span>
                                        </button>
                                        <button type="button" className="flex items-center space-x-2 px-2 py-1">
                                            <Heart size={16} />
                                            <span>Add to favorites</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
                <div className="space-y-1 text-right">
                    <p>
                        Total amount:
                        <span className="font-semibold"> ₹{totalAmount}</span>
                    </p>
                </div>
                <div className="flex justify-end space-x-4">
                    <button
                        type="button"
                        onClick={() => navigate('/products')}
                        className="rounded-md border border-black px-3 py-2 text-sm font-semibold text-black shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-black"
                    >
                        Back to shop
                    </button>
                    <button
                        type="button"
                        onClick={handlePlaceOrder}
                        className="rounded-md border border-black px-3 py-2 text-sm font-semibold text-black shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-black"
                    >
                        Checkout
                    </button>
                </div>
                <Footer />
            </div>
        </div>
    );
}