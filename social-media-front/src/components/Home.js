import React, { useEffect, useCallback } from "react";
import { Route, Routes, useNavigate } from "react-router-dom";
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode'; // Import jwtDecode
import '../design/HomeDesign.css';
import Signup from './Signup';  // Import your Signup component
import Login from './Login';    // Import your Login component

function Home() {
    const navigate = useNavigate();

    // Function to check if the JWT token is valid
    const checkToken = useCallback(() => {
        const token = Cookies.get('token');
        if (token) {
            try {
                const decodedToken = jwtDecode(token);
                const currentTime = Date.now() / 1000; // Get the current time in seconds
                if (decodedToken.exp > currentTime) {
                    // Token is valid, redirect to /feed
                    navigate('/feed');
                }
            } catch (error) {
                // Token is invalid, do nothing or handle accordingly
            }
        }
    }, [navigate]);

    useEffect(() => {
        checkToken(); // Check token validity on component mount
    }, [checkToken]);

    return (
        <div className="home-container">
            <div className="blur-background">
                <Routes>
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/login" element={<Login />} />
                </Routes>
            </div>
            <nav className="nav-container">
                <div className="button-container">
                    <button className="signup-button" onClick={() => navigate("/signup")}>Signup</button>
                    <button className="login-button" onClick={() => navigate("/login")}>Login</button>
                </div>
            </nav>
        </div>
    );
}

export default Home;
