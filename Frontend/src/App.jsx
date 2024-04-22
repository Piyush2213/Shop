import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import './App.css'
import { Login } from "./components/login/Login";
import { SignUp } from "./components/signUp/SignUp";
import Verification from './components/signUp/Verification';


function App() {


  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signUp" element={<SignUp />} />
        <Route path="/verification" element={<Verification />} />
      </Routes>
    </Router>
  )
}

export default App
