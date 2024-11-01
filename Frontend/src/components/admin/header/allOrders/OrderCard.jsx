import React, { useState } from 'react';
import axios from 'axios';
import base_url from '../../../default/BaseUrl';
import Cookies from 'js-cookie';

const OrderCard = ({ order, onUpdateStatus }) => {
    const [selectedStatus, setSelectedStatus] = useState(order.orderStatus);

    const handleStatusChange = async () => {
        try {
            const response = await axios.put(
                `${base_url}/orders/updateStatus/${order.id}?orderStatus=${selectedStatus}`,
                null,
                {
                    headers: {
                        Authorization: Cookies.get('token'),
                    },
                }
            );

            if (response.status === 200) {
                onUpdateStatus(order.id, selectedStatus);
            } else {
                console.error('Error updating order status:', response.data);
            }
        } catch (error) {
            console.error('Error updating order status:', error);
        }
    };

    return (
        <div key={order.id}>
            <div className="rounded-md border">
                <div className="p-4">
                    <h1 className="inline-flex items-center text-lg font-semibold">Order ID: {order.id}</h1>
                    <p className="mt-3 text-sm text-gray-600">Order Date: {new Date(order.dateTime).toLocaleString()}</p>
                    <p className="mt-3 text-sm text-gray-600">Total Amount: ${order.totalAmount}</p>
                    <p className="mt-3 text-sm text-gray-600">Order Status: {order.orderStatus}</p>

                    
                    <div className="mt-3">
                        <label className="block text-sm font-medium text-gray-700">Change Status:</label>
                        <select
                            className="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-blue-500 focus:ring focus:ring-blue-200"
                            value={selectedStatus}
                            onChange={(e) => setSelectedStatus(e.target.value)}
                        >
                            <option value="Pending">PENDING</option>
                            <option value="Shipped">SHIPPED</option>
                            <option value="Out_Of_Delivery">OUT_OF_DELIVERY</option>
                            <option value="Delivered">DELIVERED</option>
                        </select>
                        <button
                            className="mt-4 w-full rounded-sm bg-black px-2 py-1.5 text-sm font-semibold text-white shadow-sm hover:bg-black/80 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-black"
                            onClick={handleStatusChange}
                        >
                            Update Status
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};


export default OrderCard;