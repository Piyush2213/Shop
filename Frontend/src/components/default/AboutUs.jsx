import { React, useEffect } from 'react';
import styled, { createGlobalStyle } from 'styled-components';
import { Header } from '../header/Header';

// Global Styles
const GlobalStyle = createGlobalStyle`
  body {
    background-color: #f4f4f4;
    margin: 0;
    padding: 0;
    font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;
    color: #333;
  }
`;

// Container for the page
const AboutUsContainer = styled.div`
  padding: 80px 20px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #fff;
`;

// Section for the heading with gray accent
const HeroSection = styled.section`
  background-color: #808080;
  padding: 20px 20px;
  border-radius: 8px;
  text-align: center;
  color: white;
  margin-bottom: 40px;
`;

const Heading = styled.h2`
  font-size: 40px;
  font-weight: 600;  /* Normal weight for a more relaxed look */
  margin: 0;
`;

const Subheading = styled.p`
  font-size: 18px;
  margin-top: 10px;
  font-weight: 300;  /* Light font weight for better legibility */
`;

// Paragraph styling for content
const Paragraph = styled.p`
  font-size: 18px;
  line-height: 1.8;  /* Increased line height for better readability */
  margin-bottom: 20px;
  color: #555;
`;

// Section for core values and mission
const ValuesSection = styled.section`
  margin-top: 40px;
`;

const ValuesList = styled.ul`
  list-style: none;
  padding-left: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  font-size: 16px;
  color: #555;
`;

const ValueItem = styled.li`
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #333;
  padding: 10px;
  background-color: #fafafa;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

  &:hover {
    background-color: #ff6b6b;
    color: white;
    cursor: pointer;
    transform: scale(1.05);
    transition: all 0.3s ease;
  }
`;

const MissionStatement = styled.section`
  background-color: #f9f9f9;
  padding: 40px 20px;
  border-radius: 8px;
  margin-top: 40px;
`;

const DecorativeDivider = styled.hr`
  border-top: 2px solid #ddd;
  margin: 40px 0;
`;

// Team section (could be added later)
const TeamSection = styled.section`
  margin-top: 40px;
  text-align: center;
`;

const TeamHeading = styled.h3`
  font-size: 32px;
  margin-bottom: 20px;
`;

const TeamMember = styled.div`
  display: inline-block;
  margin: 20px;
  width: 150px;
`;

const TeamMemberImage = styled.img`
  width: 100%;
  border-radius: 50%;
`;

const TeamMemberName = styled.p`
  font-size: 16px;
  font-weight: normal;  /* Normal weight for team member names */
  margin-top: 10px;
`;

// Main component for About Us page
export const AboutUs = () => {
  useEffect(() => {
    document.title = "About Us";
  }, []);

  return (
    <>
      <Header />
      <GlobalStyle />
      <AboutUsContainer>
        <HeroSection>
          <Heading>About Us</Heading>
          <Subheading>Your Trusted Online Retailer</Subheading>
        </HeroSection>

        <Paragraph>
          Welcome to TrendyMart Store! We are passionate about providing our customers with a curated selection of high-quality products.
          Our mission is to make online shopping convenient, enjoyable, and accessible to everyone. With a focus on customer satisfaction, we carefully select products that meet the highest standards of quality and durability.
        </Paragraph>

        <MissionStatement>
          <Paragraph>
            Our mission is simple: to make your shopping experience seamless, fun, and memorable. We’re here to bring you the best products
            that align with your lifestyle. Whether you're shopping for your home, office, or personal use, we have something for everyone.
          </Paragraph>
        </MissionStatement>

        <DecorativeDivider />

        <ValuesSection>
          <h3>Our Core Values</h3>
          <ValuesList>
            <ValueItem>Customer Satisfaction: We prioritize your satisfaction above all.</ValueItem>
            <ValueItem>Quality Assurance: We offer only the best products that meet our rigorous standards.</ValueItem>
            <ValueItem>Reliability: Count on us for timely deliveries and secure transactions.</ValueItem>
            <ValueItem>Innovation: We stay ahead of the curve to bring you the latest trends and technologies.</ValueItem>
          </ValuesList>
        </ValuesSection>

        <DecorativeDivider />

        <TeamSection>
          <TeamHeading>Meet Our Team</TeamHeading>
          <div>
            <TeamMember>
              <TeamMemberImage src="https://www.citizensbank.com/assets/CB_resources/images/content_2_0/InterviewEmbed2_DH7A2851_635X423.jpg" alt="Team member" />
              <TeamMemberName>James Bond</TeamMemberName>
            </TeamMember>
            <TeamMember>
              <TeamMemberImage src="https://d1fufvy4xao6k9.cloudfront.net/images/blog/posts/2023/11/hockerty_shirt_439c023c_f604_4701_ac44_5c45ab5f22b9.jpg" alt="Team member" />
              <TeamMemberName>Criss Morris</TeamMemberName>
            </TeamMember>
          </div>
        </TeamSection>

        <Paragraph>
          Thank you for choosing TrendyMart Store. We look forward to serving you and making your shopping experience unforgettable!
        </Paragraph>
      </AboutUsContainer>
    </>
  );
};
