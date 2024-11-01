import React, { useState } from 'react';


const AddressEntry = ({ onAddressSubmit }) => {
    const [tostMessage, setTostMessage] = useState('');
    const [addressDetails, setAddressDetails] = useState({
        houseNo: '',
        street: '',
        city: '',
        pin: '',
    });

    const handleSubmit = (event) => {
        event.preventDefault();
        if (!addressDetails.houseNo || !addressDetails.street || !addressDetails.city || !addressDetails.pin) {
            setTostMessage('Please fill in all address fields.');
            return;
            
        }
        setTostMessage('Address updates please checkout!');                
        onAddressSubmit(addressDetails);
    };

    return (
        <div>
            
            <div className="flex items-center justify-center px-4 py-10 sm:px-6 sm:py-16 lg:px-8 lg:py-24">
                <div className="xl:mx-auto xl:w-full xl:max-w-sm 2xl:max-w-md">
                    <h2 className="text-center text-2xl font-bold leading-tight text-black">
                        Enter Your Address
                    </h2>
                    {tostMessage && (
                    <div className="mt-4 bg-green-100 border border-green-400 text-green-700 px-4 py-2 rounded-md">
                        {tostMessage}
                    </div>
                )}
                

                    <form onSubmit={handleSubmit} className="mt-8">

                        <div className="space-y-5">
                            <div>
                                <label htmlFor="houseNo" className="text-base font-medium text-gray-900">
                                    House No
                                </label>
                                <div className="mt-2">
                                    <input
                                        type="text"
                                        id="houseNo"
                                        name="houseNo"
                                        value={addressDetails.houseNo}
                                        onChange={(e) => setAddressDetails({ ...addressDetails, houseNo: e.target.value })}
                                        className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50"
                                        placeholder="House No"
                                    />
                                </div>
                            </div>

                            <div>
                                <label htmlFor="street" className="text-base font-medium text-gray-900">
                                    Street
                                </label>
                                <div className="mt-2">
                                    <input
                                        type="text"
                                        id="street"
                                        name="street"
                                        value={addressDetails.street}
                                        onChange={(e) => setAddressDetails({ ...addressDetails, street: e.target.value })}
                                        className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50"
                                        placeholder="Street"
                                    />
                                </div>
                            </div>

                            <div>
                                <label htmlFor="city" className="text-base font-medium text-gray-900">
                                    City
                                </label>
                                <div className="mt-2">
                                    <input
                                        type="text"
                                        id="city"
                                        name="city"
                                        value={addressDetails.city}
                                        onChange={(e) => setAddressDetails({ ...addressDetails, city: e.target.value })}
                                        className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50"
                                        placeholder="City"
                                    />
                                </div>
                            </div>

                            <div>
                                <label htmlFor="zipCode" className="text-base font-medium text-gray-900">
                                    Pin Code
                                </label>
                                <div className="mt-2">
                                    <input
                                        type="text"
                                        id="zipCode"
                                        name="zipCode"
                                        value={addressDetails.pin}
                                        onChange={(e) => setAddressDetails({ ...addressDetails, pin: e.target.value })}
                                        className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-1 focus:ring-gray-400 focus:ring-offset-1 disabled:cursor-not-allowed disabled:opacity-50"
                                        placeholder="Zip Code"
                                    />
                                </div>
                            </div>

                            <div>
                                <button
                                    type="submit"
                                    className="inline-flex w-full items-center justify-center rounded-md bg-black px-3.5 py-2.5 font-semibold leading-7 text-white hover:bg-black/80"
                                >
                                    Submit Address
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            
        </div>
    );
};

export default AddressEntry;