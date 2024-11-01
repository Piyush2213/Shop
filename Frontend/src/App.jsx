import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import './App.css'
import { Login } from "./components/login/Login";
import { SignUp } from "./components/signUp/SignUp";
import Home from './components/home/Home';
import { Products } from './components/products/Products';

import Verification from './components/signUp/Verification';
import { AboutUs } from './components/default/AboutUs';
import { Contact } from './components/default/Contact';
import { ProductDetail } from './components/products/ProductDetail';
import { Cart } from './components/cart/Cart';
import AddressEntry from './components/address/AddressEntry';
import { Orders } from './components/orders/Orders';
import { AdminLogin } from './components/admin/header/AdminLogin';
import { OrdersList } from './components/admin/header/allOrders/OrderList';


function App() {


  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/admin-login" element={<AdminLogin />} />
        <Route path="/signUp" element={<SignUp />} />
        <Route path="/products" element={<Products />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/aboutUs" element={<AboutUs />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/Orderslist" element={<OrdersList />} />
        <Route path="/address-entry" element={<AddressEntry />} />
        <Route path="/verification" element={<Verification />} />
      </Routes>
    </Router>
  )
}

export default App
