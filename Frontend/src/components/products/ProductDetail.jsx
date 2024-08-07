import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import base_url from '../default/BaseUrl';
import axios from 'axios';
import Cookies from 'js-cookie';
import { Star } from 'lucide-react';
import { Footer } from '../footer/Footer';
import { Header2 } from '../header/Header2';
import { toast, ToastContainer } from 'react-toastify';

export const ProductDetail = () => {
    const { id } = useParams();
    const token = Cookies.get('token');
    const username = Cookies.get('firstName');
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);

    const fetchProduct = useCallback(async () => {
        try {
            const response = await axios.get(`${base_url}/products/${id}`);
            setProduct(response.data.data);
            toast.success("Product loaded successfully!");
        } catch (error) {
            console.log('Error fetching product details:', error);
            toast.error("Error fetching product details");
        }
    }, [id]);

    useEffect(() => {
        fetchProduct();
    }, [fetchProduct]);

    const handleAddToCart = async () => {
        try {
            if (!token) {
                toast.error("Error adding to cart. Please log in first.");
                const currentPath = window.location.pathname;
                navigate(`/login?redirect=${encodeURIComponent(currentPath)}`);
                return;
            }
            const responseMessage = await addToCart(product.id, quantity);
            toast.success(responseMessage);
            navigate('/cart');
        } catch (error) {
            toast.error(error.message);
        }
    };

    const addToCart = async (productId, quantity) => {
        try {
            const response = await axios.post(
                `${base_url}/cart/cartItems`,
                {
                    productId: productId,
                    quantity: quantity,
                },
                {
                    headers: {
                        Authorization: `${token}`,
                    },
                }
            );

            if (response.status >= 200 && response.status < 300) {
                return `Added ${quantity} item(s) to cart.`;
            } else {
                throw new Error('Failed to add product to cart.');
            }
        } catch (error) {
            console.log('Error adding product to cart:', error);
            throw new Error('Failed to add product to cart.');
        }
    };

    const handleQuantityChange = (event) => {
        const newQuantity = parseInt(event.target.value, 10);
        if (!isNaN(newQuantity) && newQuantity >= 1 && (!product || newQuantity <= product.quantity)) {
            setQuantity(newQuantity);
        }
    };

    if (!product) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <Header2 username={username} token={token} />
            <section className="overflow-hidden">
                <div className="mx-auto max-w-5xl px-5 py-24">
                    <div className="mx-auto flex flex-wrap items-center lg:w-4/5">
                        <img
                            alt={product.name}
                            className="h-64 w-full rounded object-cover lg:h-96 lg:w-1/2"
                            src={product.imageURL}
                        />
                        <div className="mt-6 w-full lg:mt-0 lg:w-1/2 lg:pl-10">
                            <h1 className="my-4 text-3xl font-semibold text-black">{product.name}</h1>
                            <div className="my-4 flex items-center">
                                <span className="flex items-center space-x-1">
                                    {[...Array(5)].map((_, i) => (
                                        <Star key={i} size={16} className="text-yellow-500" />
                                    ))}
                                    <span className="ml-3 inline-block text-xs font-semibold">4 Reviews</span>
                                </span>
                            </div>
                            <p className="leading-relaxed">
                                Lorem ipsum dolor, sit amet consectetur adipisicing elit. Tenetur rem amet repudiandae
                                neque adipisci eum enim, natus illo inventore totam?
                            </p>
                            <div className="mb-5 mt-6 flex items-center border-b-2 border-gray-100 pb-5">
                                <div className="flex items-center">
                                    <span className="mr-3 text-sm font-semibold">Color</span>
                                    <button className="h-6 w-6 rounded-full border-2 border-gray-300 focus:outline-none"></button>
                                    <button className="ml-1 h-6 w-6 rounded-full border-2 border-gray-300 bg-gray-700 focus:outline-none"></button>
                                    <button className="ml-1 h-6 w-6 rounded-full border-2 border-gray-300 bg-green-200 focus:outline-none"></button>
                                </div>
                                <div className="ml-auto flex items-center">
                                    <span className="mr-3 text-sm font-semibold">Quantity</span>
                                    <input
                                        type="number"
                                        className="appearance-none rounded border border-gray-300 py-2 pl-3 pr-10 text-sm focus:border-black focus:outline-none focus:ring-2 focus:ring-black"
                                        value={quantity}
                                        onChange={handleQuantityChange}
                                        min={1}
                                        max={product.quantity || 1}
                                    />
                                </div>
                            </div>
                            <div className="flex items-center justify-between">
                                <span className="title-font text-xl font-bold text-gray-900">₹{product.price}</span>
                                <button
                                    type="button"
                                    className="rounded-md bg-black px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-black/80 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-black"
                                    onClick={handleAddToCart}
                                >
                                    Add to Cart
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <Footer />
            <ToastContainer />
        </div>
    );
};
