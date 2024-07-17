import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';

const PaymentSuccess = () => {
    const [paymentId, setPaymentId]=useState();
    const [referenceId, setReferenceId]=useState();
    const[paymentStatus, setPaymentStatus]=useState();
    const {orderId}=useParams();

    console.log("orderId", orderId);

    const dispatch = useDispatch();
    const {order} = useSelector(store=>store);

    useEffect(()=>{
      const urlParam=new URLSearchParams(window.location.search);
      setPaymentId(url.Param.get("razorpay_payment_id"))
      setPaymentStatus(url.Param.get("razorpay_payment_link_status"))
    },[]);

    useEffect(()=>{
      const data = {orderId, paymentId}
      dispatch(getOrderById(orderId));
      dispatch(updatePayment(data));
  },[orderId, paymentId])
  return (
    <div className='px-2 lg:px-36'>
      <div className='flex flex-col justify-center items-center'>
        <Alert
        variant='filled'
        severity='success'
        sx={{mb:6, width:"fit-content"}}
        >
          <AlertTitle>Payment Success</AlertTitle>
          Congratulation  Your Order Get Placed

        </Alert>

      </div>
      
    </div>
  )
}

export default PaymentSuccess;