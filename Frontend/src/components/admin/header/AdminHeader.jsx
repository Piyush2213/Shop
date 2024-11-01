import React from 'react';
import { NavLink } from 'react-router-dom';
import Cookies from 'js-cookie';

export function AdminHeader({username, token }) {
    const handleLogout = () => {
        Cookies.remove('token');
        Cookies.remove('Name');
        Cookies.remove('userRole');
        window.location.href = '/admin-login';
    };

    const handleAdminLogin = () => {
        window.location.href = '/admin-login';
    };
    const handleCustomerLogin = () => {
        window.location.href = '/login';
    };



    return (
        <section className="relative overflow-hidden bg-white py-8 mb-4">
            <div className="container relative z-10 mx-auto px-4">
                <div className="-m-8 flex flex-wrap items-center justify-between">
                    <div className="w-auto p-8">
                        <NavLink to="/products" activeClassName="active">
                            <div className="inline-flex items-center">
                                <img
                                    src="https://svgsilh.com/svg_v2/189064.svg"
                                    alt="DevUI Logo"
                                    width="80"
                                    height="46"
                                />
                                {username && (
                                    <span className="font-medium text-gray-600 ml-12">{`Hello, ${username}!`}</span>
                                )}
                            </div>
                        </NavLink>
                    </div>
                    <div className="w-auto p-8">
                        <ul className="-m-5 flex flex-wrap items-center">
                            <li className="p-5">
                                <NavLink to="/products" activeClassName="active" className="font-medium text-gray-600 hover:text-gray-700">
                                    Visit
                                </NavLink>
                            </li>

                            {token ? (
                                <>
                                    <li className="p-5">
                                        <button
                                            className="font-medium text-gray-600 hover:text-gray-700"
                                            onClick={handleLogout}
                                        >
                                            Logout
                                        </button>
                                    </li>
                                </>
                            ) : (
                                <>
                                    <li className="p-5">
                                        <button
                                            className="font-medium text-gray-600 hover:text-gray-700"
                                            onClick={handleCustomerLogin}
                                        >
                                            Customer Login
                                        </button>
                                    </li>
                                    <li className="p-5">
                                        <button
                                            className="font-medium text-gray-600 hover:text-gray-700"
                                            onClick={handleAdminLogin}
                                        >
                                            Admin Login
                                        </button>
                                    </li>
                                </>
                            )}
                        </ul>
                    </div>
                </div>
            </div>
            <style>
                {`
          .active {
            color: red;
            text-decoration: underline;
          }
        `}
            </style>
        </section>
    );
}