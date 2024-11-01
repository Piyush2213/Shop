import axios from 'axios';
import base_url from '../default/BaseUrl';

const elasticsearchBaseUrl = `${base_url}/products/fuzzySearch`;

const ElasticsearchService = {
    fuzzySearch: async (approximateProductName) => {
        try {
            console.log("Attempting to perform fuzzy search for:", approximateProductName);
            const response = await axios.get(`${elasticsearchBaseUrl}/${encodeURIComponent(approximateProductName)}`);
            console.log("Fuzzy search response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error searching products:', error);
            throw new Error('Error searching products.');
        }
    },
};

export default ElasticsearchService;

