import React, { useState, useEffect } from 'react';
import { ArrowRight } from 'lucide-react';
import axios from 'axios';
import base_url from '../../default/BaseUrl';
import { Link, useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import { Footer } from '../../footer/Footer';
import { AdminHeader } from './AdminHeader';

export function AdminLogin() {
    useEffect(() => {
        document.title = "Admin";
    }, []);
    const token = Cookies.get('token');
    const username = Cookies.get('Name');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    const handleFormSubmit = async (e) => {
        e.preventDefault();
    
        try {

            const response = await axios.post(`${base_url}/admin/login`, { email, password });
            const data = response.data.data;
    
            if (data.token) {
                setErrorMessage('');
                Cookies.set('token', data.token);
                Cookies.set('Name', data.name);
                Cookies.set('userRole', data.role);
                navigate('/OrdersList');
            } else {
                setErrorMessage('Invalid email or password. Please try again.');
            }
        } catch (error) {
            console.log('Error:', error);
    
            if (error.response) {
                console.log('Error Response:', error.response);
                if (error.response.status === 500) {
                    setErrorMessage('An error occurred on the server. Please try again later.');
                } else if (error.response.status === 401) {
                    setErrorMessage('Invalid email or password. Please try again.');
                } else {
                    setErrorMessage('An unexpected error occurred. Please try again later.');
                }
            } else {
                setErrorMessage('An unexpected error occurred. Please try again later.');
            }
        }
    };
    
    

    const handleEmailChange = (e) => {
        setEmail(e.target.value);
    };

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    };

    return (
        <div>
            <AdminHeader username={username} token={token} />

            <section>
                <div className="flex items-center justify-center px-4 py-10 sm:px-6 sm:py-16 lg:px-8 lg:py-24">
                    <div className="xl:mx-auto xl:w-full xl:max-w-sm 2xl:max-w-md">

                        <h2 className="text-center text-2xl font-bold leading-tight text-black">
                            Sign in to your account
                        </h2>
                        {errorMessage && (
                            <div className="mt-4 bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded-md">
                                {errorMessage}
                            </div>
                        )}
                        <form onSubmit={handleFormSubmit} className="mt-8">
                            <div className="space-y-5">
                                <div>
                                    <label htmlFor="email" className="text-base font-medium text-gray-900">
                                        {' '}
                                        Email address{' '}
                                    </label>
                                    <div className="mt-2">
                                        <input
                                            id="email"
                                            name="email"
                                            value={email}
                                            onChange={handleEmailChange}
                                            className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50"
                                            type="email"
                                            placeholder="Email"
                                        />
                                    </div>
                                </div>
                                <div>
                                    <div className="flex items-center justify-between">
                                        <label htmlFor="password" className="text-base font-medium text-gray-900">
                                            {' '}
                                            Password{' '}
                                        </label>
                                    </div>
                                    <div className="mt-2">
                                        <input
                                            id="password"
                                            name="password"
                                            value={password}
                                            onChange={handlePasswordChange}
                                            className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50"
                                            type="password"
                                            placeholder="Password"
                                        />
                                    </div>
                                </div>
                                <div>
                                    <button
                                        type="submit"
                                        className="inline-flex w-full items-center justify-center rounded-md bg-black px-3.5 py-2.5 font-semibold leading-7 text-white hover:bg-black/80"
                                    >
                                        Log In<ArrowRight className="ml-2" size={16} />
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </section>
            <Footer />
        </div>
    );
}