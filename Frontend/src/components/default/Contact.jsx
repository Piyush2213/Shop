import { React, useEffect } from 'react';
import { Header } from '../header/Header';
import { Footer } from '../footer/Footer';

export function Contact() {
    useEffect(() => {
        document.title = "Contact";
    }, []);
    return (
        <div>
            <Header />
            <section className="bg-white dark:bg-gray-900">
                <div className="py-8 lg:py-16 px-4 mx-auto max-w-screen-md">
                    <h2 className="mb-4 text-4xl tracking-tight font-extrabold text-center text-gray-900 dark:text-white">Contact Us</h2>
                    <p className="mb-8 lg:mb-16 font-light text-center text-gray-500 dark:text-gray-400 sm:text-xl">
                        Got a technical issue? Want to send feedback about a beta feature? Need details about our Business plan? Let us know.
                    </p>
                    <div className="space-y-8">
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300">Phone Number</label>
                            <p className="text-gray-900 dark:text-white">+91 8295318734</p>
                        </div>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300">Landline Number</label>
                            <p className="text-gray-900 dark:text-white">+012-812-72535</p>
                        </div>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300">Address</label>
                            <p className="text-gray-900 dark:text-white">123  Rangoli Main Street, Jaipur, India</p>
                        </div>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300">Working Hours</label>
                            <p className="text-gray-900 dark:text-white">Monday - Friday, 9:00 AM - 5:00 PM</p>
                        </div>
                    </div>
                </div>
            </section>
            <Footer />
        </div>

    );
}