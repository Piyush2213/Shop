import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import base_url from '../default/BaseUrl';
import { Header } from '../header/Header';
import { Footer } from '../footer/Footer';

const Verification = () => {
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleVerification = async () => {
        try {
            const url = `${base_url}/customers/verify?email=${encodeURIComponent(email)}&otp=${otp}`;
            const response = await axios.post(url);
            if (response.status === 200 && response.data.message === 'Verified successfully') {
                // Redirect to login page if verification is successful
                navigate('/login');
            } else {
                setError('Invalid email or OTP');
            }
        } catch (error) {
            setError('Error verifying user');
        }
    };
    

    return (
        <div>
            <Header />

            <section>
                <div className="flex items-center justify-center px-4 py-10 sm:px-6 sm:py-16 lg:px-8 lg:py-24">
                    <div className="xl:mx-auto xl:w-full xl:max-w-sm 2xl:max-w-md">

                        <h2 className="text-center text-2xl font-bold leading-tight text-black">
                            Verification
                        </h2>
                        <input
                            type="text"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50 mt-4"
                        />
                        <input
                            type="text"
                            placeholder="OTP"
                            value={otp}
                            onChange={(e) => setOtp(e.target.value)}
                            className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50 mt-4"
                        />
                        <button
                            onClick={handleVerification}
                            className="inline-flex w-full items-center justify-center rounded-md bg-black px-3.5 py-2.5 font-semibold leading-7 text-white hover:bg-black/80 mt-4"
                        >
                            Verify
                        </button>
                        {error && <p className="mt-4 bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded-md">{error}</p>}
                    </div>
                </div>
            </section>
            <Footer />
        </div>
    );
};

export default Verification;
