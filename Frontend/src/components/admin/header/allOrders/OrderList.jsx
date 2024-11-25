import React, { useState, useEffect } from 'react';
import axios from 'axios';
import base_url from '../../../default/BaseUrl';
import Cookies from 'js-cookie';
import { Footer } from '../../../footer/Footer';
import { useNavigate } from 'react-router-dom';
import OrderCard from './OrderCard';
import { AdminHeader } from '../AdminHeader';

export const OrdersList = () => {
  const [orders, setOrders] = useState([]);
  const token = Cookies.get('token');
  const username = Cookies.get('Name');
  const navigate = useNavigate();

  const fetchOrders = async () => {
    try {
      const response = await axios.get(`${base_url}/orders/getAllOrders`, {
        headers: {
          Authorization: token,
        },
      });
      console.log('Orders fetched:', response.data);
      setOrders(response.data);
    } catch (error) {
      console.error('Error fetching orders:', error);
      navigate('/admin-login');
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [token, navigate]);

  const handleUpdateStatus = (orderId, newStatus) => {
    setOrders((prevOrders) =>
      prevOrders.map((order) =>
        order.id === orderId ? { ...order, orderStatus: newStatus } : order
      )
    );
  };

  return (
    <div>
      <AdminHeader token={token} username={username} />

      <div className="mx-auto grid w-full max-w-7xl items-center space-y-4 px-2 py-10 md:grid-cols-2 md:gap-6 md:space-y-0 lg:grid-cols-4">
        {orders.map((order) => (
          <OrderCard key={order.id} order={order} onUpdateStatus={handleUpdateStatus} />
        ))}
      </div>
      <Footer />
    </div>
  );
};

