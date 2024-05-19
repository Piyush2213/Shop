import React, {useEffect} from 'react';
import { Products } from '../products/Products';



const Home = () => {
  useEffect(() => {
    document.title = "ebay";
}, []);
  return (
    <Products />
  );
};

export default Home;