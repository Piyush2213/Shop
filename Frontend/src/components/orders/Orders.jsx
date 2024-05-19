import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom'; 

import base_url from '../default/BaseUrl';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Container } from 'react-bootstrap';
import { Footer } from '../footer/Footer';
import { Header2 } from '../header/Header2';

export const Orders = () => {
    const [orders, setOrders] = useState([]);
    const token = Cookies.get('token');
    const username = Cookies.get('firstName');
    const navigate = useNavigate(); 

    useEffect(() => {
        fetchOrders();
    }, []);

    const cancelOrder = async (orderId) => {
        try {
            const token = Cookies.get('token');
            if (!token) {
                throw new Error('You need to log in first.');
            }

            await axios.delete(`${base_url}/order/${orderId}`, {
                headers: {
                    Authorization: token,
                },
            });

            toast.success('Order cancelled successfully.');
            fetchOrders();
        } catch (error) {
            toast.error(error.message);
        }
    };

    const fetchOrders = async () => {
        try {
            const token = Cookies.get('token');
            if (!token) {
                throw new Error('You need to log in first.');
            }

            const response = await axios.get(`${base_url}/order`, {
                headers: {
                    Authorization: token,
                },
            });

            console.log("Fetched Orders: ", response.data.data);

            // Filter out cancelled orders
            const activeOrders = response.data.data.filter(order => order.orderStatus.toLowerCase() !== 'cancelled');

            console.log("Active Orders: ", activeOrders);

            setOrders(activeOrders);
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handleProductClick = (productId) => {
        navigate(`/products/${productId}`);
    };

    return (
        <div>
            <Header2 username={username} token={token} />
            <div className="page-container">
                <ToastContainer />
                <Container>
                    <h1 className="order-title">Order Details</h1>
                    <div className="order-description">
                        Check the status of recent and old orders & discover more products
                    </div>
                    {orders.length > 0 ? (
                        orders.map((order) => (
                            <div key={order.id} className="order-container">
                                <div className="mt-8 flex flex-col overflow-hidden rounded-lg border border-gray-300 md:flex-row">
                                    <div className="w-full border-r border-gray-300 bg-gray-100 md:max-w-xs">
                                        <div className="p-8">
                                            <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-1">
                                                {[
                                                    ['Order ID', order.id],
                                                    ['Date', order.dateTime],
                                                    ['Total Amount', `$${order.totalAmount}`],
                                                    ['Order Status', order.orderStatus],
                                                    ['Shipment Address', order.deliveryAddress?.houseNo && `${order.deliveryAddress.houseNo}, ${order.deliveryAddress.street}, ${order.deliveryAddress.city}, ${order.deliveryAddress.pin}`],
                                                ].map(([key, value]) => (
                                                    <div key={key} className="mb-4">
                                                        <div className="text-sm font-semibold">{key}</div>
                                                        <div className="text-sm font-medium text-gray-700">{value}</div>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex-1">
                                        <div className="p-8">
                                            <ul className="-my-7 divide-y divide-gray-200">
                                                {order.orderItems.map((item) => (
                                                    <li
                                                        key={item.productId}
                                                        className="flex flex-col justify-between space-x-5 py-7 md:flex-row"
                                                        onClick={() => handleProductClick(item.productId)}
                                                        style={{ cursor: 'pointer' }}
                                                    >
                                                        <div className="flex flex-1 items-stretch">
                                                            <div className="flex-shrink-0">
                                                                <img
                                                                    className="h-20 w-20 rounded-lg border border-gray-200 object-contain"
                                                                    src={item.productImageURL}
                                                                    alt={item.productName}
                                                                />
                                                            </div>

                                                            <div className="ml-5 flex flex-col justify-between">
                                                                <div className="flex-1">
                                                                    <p className="text-sm font-bold text-gray-900">{item.productName}</p>
                                                                </div>

                                                                <p className="mt-4 text-sm font-medium text-gray-500">x {item.quantity}</p>
                                                            </div>
                                                        </div>

                                                        <div className="ml-auto flex flex-col items-end justify-between">
                                                            <p className="text-right text-sm font-bold text-gray-900">${item.price}</p>
                                                        </div>
                                                    </li>
                                                ))}
                                            </ul>
                                            <hr className="my-8 border-t border-t-gray-200" />
                                            <div className="space-x-4">
                                                <button
                                                    type="button"
                                                    className="rounded-md bg-black px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-black/80 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-black"
                                                    onClick={() => cancelOrder(order.id)}
                                                >
                                                    Cancel Order
                                                </button>
                                                
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No orders available.</p>
                    )}
                </Container>
            </div>
            <Footer />
        </div>
    );
};
