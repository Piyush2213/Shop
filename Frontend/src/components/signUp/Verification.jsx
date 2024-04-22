import React, { useState } from 'react';
import axios from 'axios';
import base_url from '../default/BaseUrl';

const Verification = () => {
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleEmailChange = (e) => {
        setEmail(e.target.value);
    };

    const handleOtpChange = (e) => {
        setOtp(e.target.value);
    };

    const handleVerification = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(`${base_url}/customers/verify`, {
                email: email,
                otp: otp
            });

            if (response.data.status === 200) {
                setMessage(response.data.message);
            } else {
                setError(response.data.message);
            }
        } catch (error) {
            console.error(error);
            setError('Error verifying user. Please try again.');
        }
    };

    return (
        <div>
            <h2>Verification</h2>
            {message && <p>{message}</p>}
            {error && <p>{error}</p>}
            <form onSubmit={handleVerification}>
                <div>
                    <label htmlFor="email">Email:</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={handleEmailChange}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="otp">OTP:</label>
                    <input
                        type="text"
                        id="otp"
                        value={otp}
                        onChange={handleOtpChange}
                        required
                    />
                </div>
                <button type="submit">Verify</button>
            </form>
        </div>
    );
};

export default Verification;
