import axios from 'axios';
import { CREATE_PAYMENT_REQUEST, CREATE_PAYMENT_SUCCESS, CREATE_PAYMENT_FAILURE } from './ActionTypes';
import base_url from '../default/BaseUrl';

export const createPayment = (orderId) => async (dispatch) => {
    dispatch({ type: CREATE_PAYMENT_REQUEST });
    try {
        const response = await axios.post(`${base_url}/api/payments/${orderId}`);
        const paymentLinkUrl = response.data.data.payment_link_url;
        window.location.href = paymentLinkUrl;
        dispatch({ type: CREATE_PAYMENT_SUCCESS });
    } catch (error) {
        dispatch({ type: CREATE_PAYMENT_FAILURE, payload: error.message });
    }
};