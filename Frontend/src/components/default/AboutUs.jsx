import { React, useEffect } from 'react';
import styled, { createGlobalStyle } from 'styled-components';
import { Header } from '../header/Header';


const GlobalStyle = createGlobalStyle`
  body {
    background-color: #fffaf0; 
    margin: 0;
    padding: 0;
    font-family: Arial, sans-serif; 
  }
`;

const AboutUsContainer = styled.div`
  padding: 50px;
  text-align: center;
`;

const PinkBackground = styled.div`
  background-color: #ff6b6b;
  padding: 10px; 
  border-radius: 10px;
  margin-bottom: 20px;
`;

const Heading = styled.h2`
  font-size: 36px;
  color: #fff;
  margin: 0;
`;

const Subheading = styled.p`
  font-size: 18px;
  color: #fff;
  margin: 0;
`;

const Paragraph = styled.p`
  font-size: 16px;
  color: #444;
  margin-bottom: 20px;
`;

const ValuesList = styled.ul`
  list-style: disc;
  padding-left: 20px;
`;

const ValueItem = styled.li`
  font-size: 16px;
  color: #555;
  margin-bottom: 10px;
`;

const DecorativeDivider = styled.hr`
  border-top: 2px solid #ddd;
  margin: 40px 0;
`;

export const AboutUs = () => {
    useEffect(() => {
        document.title = "About";
    }, []);
    return (
        <>
            <Header />

            <GlobalStyle />
            <AboutUsContainer>
                <PinkBackground>
                    <Heading>About Us</Heading>
                    <Subheading>Your Trusted Online Retailer</Subheading>
                </PinkBackground>

                <Paragraph>
                    Welcome to our online store! At BIBA Store, we are dedicated to providing our customers with high-quality products
                    and exceptional shopping experiences. With a wide range of products across various categories, we strive to meet
                    all your needs and preferences.
                </Paragraph>

                <Paragraph>
                    Our mission is to make online shopping convenient, enjoyable, and reliable for everyone. We believe in offering
                    products that are not only functional and durable but also align with our customers' lifestyles and aspirations.
                </Paragraph>

                <DecorativeDivider />

                <Paragraph>Our values are at the core of everything we do:</Paragraph>

                <ValuesList>
                    <ValueItem>Customer Satisfaction: We prioritize your satisfaction and aim to exceed your expectations.</ValueItem>
                    <ValueItem>Quality Assurance: We carefully curate products that meet our strict quality standards.</ValueItem>
                    <ValueItem>Reliability: You can count on us for secure transactions and timely deliveries.</ValueItem>
                    <ValueItem>Innovation: We embrace innovation to bring you the latest trends and technologies.</ValueItem>
                </ValuesList>

                <DecorativeDivider />

                <Paragraph>
                    Thank you for choosing BIBA Store. We look forward to serving you and making your shopping experience unforgettable!
                </Paragraph>
            </AboutUsContainer>

        </>
    );
};

