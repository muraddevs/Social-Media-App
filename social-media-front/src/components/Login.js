import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';
import '../design/Login.css';
import { message } from "antd";

const Login = ({ onSwitch }) => {
    const [usernameOrEmail, setUsernameOrEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        const loadingMessage = message.loading('Logging in...', 0);

        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                usernameOrEmail,
                password
            });

            if (!response.data || !response.data.jwt) {
                throw new Error('No valid token received');
            }

            const token = response.data.jwt;
            Cookies.set('token', token, { path: '/', secure: process.env.NODE_ENV === 'production', sameSite: 'Strict' });
            console.log('Token stored in cookies:', Cookies.get('token'));

            const decodedToken = jwtDecode(token);
            console.log('Decoded Token:', decodedToken);

            if (decodedToken.roles) {
                if (decodedToken.roles.includes('ROLE_ADMIN') || decodedToken.roles.includes('ROLE_USER')) {
                    loadingMessage();
                    message.success('Login Successful');
                    setTimeout(() => navigate('/feed'), 2000); // Delay navigation to show message
                } else {
                    throw new Error('Unknown role');
                }
            } else {
                throw new Error('Roles not found in token');
            }
        } catch (error) {
            console.error('Error logging in', error);
            loadingMessage(); // Close the loading message

            if (error.response && error.response.status === 401) {
                message.error('Login failed: Incorrect email or password');
            } else {
                message.error(`Login failed: ${error.message || 'An error occurred'}`);
            }
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <input
                type="text"
                placeholder="Username or Email"
                value={usernameOrEmail}
                onChange={(e) => setUsernameOrEmail(e.target.value)}
            />
            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={handleLogin}>Login</button>
        </div>
    );
};

export default Login;
