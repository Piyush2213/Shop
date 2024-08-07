import React, { useState, useEffect } from 'react';
import axios from 'axios';
import base_url from '../default/BaseUrl';
import Cookies from 'js-cookie';
import { Link } from 'react-router-dom';
import { Footer } from '../footer/Footer';
import { Header2 } from '../header/Header2';
import { useNavigate } from 'react-router-dom';
import ElasticsearchService from '../elasticsearchService/ElasticsearchService';
import SearchBar from '../search/SearchBar';
import { toast, ToastContainer } from 'react-toastify';

export function Products() {
    useEffect(() => {
        document.title = 'Ebay';
    }, []);

    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const perPage = 26;
    const [noResultsMessage, setNoResultsMessage] = useState('');
    const username = Cookies.get('firstName');
    const token = Cookies.get('token');
    const navigate = useNavigate();
    const [searchTerm, setSearchTerm] = useState('');
    const [activeButton, setActiveButton] = useState(null);
    const [selectedSubcategory, setSelectedSubcategory] = useState('');

    const handleSubcategorySelect = (e) => {
        setSelectedSubcategory(e.target.value);
    };

    const handleAddToCart = async (productId) => {
        try {
            if (!token) {
                const currentPath = window.location.pathname;
                toast.error('Login to continue shopping');
                navigate(`/login?redirect=${encodeURIComponent(currentPath)}`);
                return;
            }

            const responseMessage = await addToCart(productId);
            toast.success(responseMessage);

            navigate('/cart');
        } catch (error) {
            toast.error('Error Found !');
        }
    };

    const addToCart = async (productId) => {
        try {
            const response = await axios.post(
                `${base_url}/cart/cartItems`,
                {
                    productId: productId,
                    quantity: 1,
                },
                {
                    headers: {
                        Authorization: `${token}`,
                    },
                }
            );

            console.log('Server Response:', response);

            if (response.status >= 200 && response.status < 300) {
                return `Added 1 item to cart.`;
            } else {
                throw new Error('Failed to add product to cart.');
            }
        } catch (error) {
            console.error('Error adding product to cart:', error);
            throw new Error('Failed to add product to cart.');
        }
    };

    const fetchProductCount = async () => {
        try {
            const response = await axios.get(`${base_url}/products/count`);
            const totalCount = response.data.data;
            const calculatedTotalPages = Math.ceil(totalCount / perPage);
            setTotalPages(calculatedTotalPages);
        } catch (error) {
            console.log('Error fetching product count:', error);
        }
    };

    useEffect(() => {
        fetchProductCount();
    }, []);

    const fetchProducts = async () => {
        try {
            const response = await axios.get(`${base_url}/products`, {
                params: {
                    page: currentPage,
                    perPage: perPage,
                    sort: activeButton === 'min' ? 'asc' : 'desc',
                    subcategory: selectedSubcategory,
                },
            });
            console.log('Fetch Products Response:', response.data);
            if (response.data.length === 0) {
                setNoResultsMessage('No products found.');
            } else {
                setNoResultsMessage('');
            }
            setProducts(response.data);
        } catch (error) {
            console.log('Error fetching products:', error);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, [currentPage, selectedSubcategory, activeButton]);

    const handlePrevPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    const handleNextPage = () => {
        if (currentPage < totalPages) {
            setCurrentPage(currentPage + 1);
        }
    };

    const handleSearchButtonClick = async (searchTerm) => {
        try {
            console.log("Search button clicked with search term:", searchTerm);
            const searchResults = await ElasticsearchService.fuzzySearch(searchTerm);
            console.log("Search results:", searchResults);
            setProducts(searchResults);
        } catch (error) {
            console.error('Error searching products:', error);
            toast.error("Something went wrong");
        }
    };

    const handleSortByMaxPrice = () => {
        setActiveButton('max');
    };

    const handleSortByMinPrice = () => {
        setActiveButton('min');
    };

    return (
        <div>
            <Header2 username={username} token={token} />

            <SearchBar
                onSearchButtonClick={handleSearchButtonClick}
                selectedSubcategory={selectedSubcategory}
                onSubcategorySelect={handleSubcategorySelect}
                searchTerm={searchTerm}
            />

            <div className="flex items-center justify-end space-x-4 mb-4">
                <button
                    type="button"
                    onClick={handleSortByMaxPrice}
                    className={`bg-blue-500 text-white px-6 py-3 rounded-full hover:bg-blue-600 focus:outline-none focus:ring focus:border-blue-300 ${activeButton === 'max' ? 'bg-green-600' : ''
                    }`}
                >
                    Max Price
                </button>
                <button
                    type="button"
                    onClick={handleSortByMinPrice}
                    className={`bg-blue-500 text-white px-6 py-3 rounded-full hover:bg-blue-600 focus:outline-none focus:ring focus:border-blue-300 ${activeButton === 'min' ? 'bg-green-600' : ''
                    }`}
                >
                    Min Price
                </button>
            </div>

            <div className="mx-auto grid w-full max-w-7xl items-center space-y-4 px-2 py-10 md:grid-cols-2 md:gap-6 md:space-y-0 lg:grid-cols-4">
                {products.map((product) => (
                    <div key={product.id}>
                        <Link to={`/products/${product.id}`}>
                            <div className="rounded-md border">
                                <img
                                    src={product.imageURL}
                                    alt={product.name}
                                    className="aspect-[16/9] w-full rounded-md md:aspect-auto md:h-[300px] lg:h-[200px]"
                                />
                                <div className="p-4">
                                    <h1 className="inline-flex items-center text-lg font-semibold">{product.name}</h1>
                                    <p className="mt-3 text-sm text-gray-600">Lorem ipsum dolor sit amet consectetur adipisicing elit. Excepturi, debitis?</p>
                                    <div className="mt-4">
                                        <span className="block text-sm font-semibold">Price : ${product.price}</span>
                                    </div>
                                    <div className="mt-3 flex items-center space-x-2">
                                        <span className="block text-sm font-semibold">Colors : </span>

                                        <span className="block h-4 w-4 rounded-full border-2 border-gray-300 bg-red-400"></span>
                                        <span className="block h-4 w-4 rounded-full border-2 border-gray-300 bg-purple-400"></span>
                                        <span className="block h-4 w-4 rounded-full border-2 border-gray-300 bg-orange-400"></span>
                                    </div>
                                </div>
                            </div>
                        </Link>
                        <button
                            type="button"
                            className="mt-4 w-full rounded-sm bg-black px-2 py-2 text-center text-sm font-medium text-white hover:bg-gray-800"
                            onClick={() => handleAddToCart(product.id)}
                        >
                            Add to Cart
                        </button>
                    </div>
                ))}
            </div>

            <div className="flex items-center justify-center space-x-4 mt-4">
                <button
                    type="button"
                    onClick={handlePrevPage}
                    className={`bg-blue-500 text-white px-4 py-2 rounded-md ${currentPage === 1 ? 'opacity-50 cursor-not-allowed' : ''}`}
                    disabled={currentPage === 1}
                >
                    Previous
                </button>
                <span className="text-gray-700">Page {currentPage} of {totalPages}</span>
                <button
                    type="button"
                    onClick={handleNextPage}
                    className={`bg-blue-500 text-white px-4 py-2 rounded-md ${currentPage === totalPages ? 'opacity-50 cursor-not-allowed' : ''}`}
                    disabled={currentPage === totalPages}
                >
                    Next
                </button>
            </div>

            {noResultsMessage && (
                <div className="mt-4 text-center text-gray-500">
                    {noResultsMessage}
                </div>
            )}

            <ToastContainer />

            <Footer />
        </div>
    );
}
