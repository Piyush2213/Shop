import React from 'react';
import { Link } from 'react-router-dom';
import Logo from './Logo.jpg';

export function Header() {
    return (
        <section className="relative overflow-hidden bg-white py-8">
            <div className="container relative z-10 mx-auto px-4">
                <div className="-m-8 flex flex-wrap items-center justify-between">
                    <div className="w-auto p-8">
                        <Link to="/products">
                            <div className="inline-flex items-center">
                                <img
                                    src={Logo}
                                    alt="DevUI Logo"
                                    width="120"
                                    height="46"
                                />
                            </div>
                        </Link>
                    </div>
                    <div className="w-auto p-8">
                        <ul className="-m-5 flex flex-wrap items-center">
                            <li className="p-5">
                                <Link to="/products" className="font-medium text-gray-600 hover:text-gray-700">
                                    Visit
                                </Link>
                            </li>
                            <li className="p-5">
                                <Link to="/aboutUs" className="font-medium text-gray-600 hover:text-gray-700">
                                    About Us
                                </Link>
                            </li>
                            <li className="p-5">
                                <Link to="/contact" className="font-medium text-gray-600 hover:text-gray-700">
                                    Contact Us
                                </Link>
                            </li>
                        </ul>
                    </div>
                    <div className="w-auto p-8">
                        <div className="-m-1.5 flex flex-wrap">
                            <div className="w-auto p-1.5">
                                <a href="https://github.com/Piyush2213/">
                                    <div className="flex h-8 w-8 items-center justify-center rounded-full border border-gray-300 hover:border-gray-400">
                                        <img
                                            src="data:image/png;base64,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"

                                            alt="DevUI Logo"
                                            width="80"
                                            height="46"
                                        />
                                    </div>
                                </a>
                            </div>
                            <div className="w-auto p-1.5">
                                <a href="https://www.linkedin.com/in/piyushyadav2213/">
                                    <div className="flex h-8 w-8 items-center justify-center rounded-full border border-gray-300 hover:border-gray-400">
                                        <img
                                            src="data:image/png;base64,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"

                                            alt="DevUI Logo"
                                            width="80"
                                            height="46"
                                        />
                                    </div>
                                </a>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}