import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../design/SignUp.css';

const Signup = ({ onSwitch }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [lastName, setLastName] = useState('');
    const [role, setRole] = useState('ROLE_USER'); // Default role
    const navigate = useNavigate();

    const handleSignup = async () => {
        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', {
                username,
                password,
                email,
                name,
                lastName,
                role  // Include role in the registration request
            });
            console.log('User registered successfully', response.data);
            navigate('/login');
        } catch (error) {
            if (error.response) {
                console.error('Error signing up', error.response.data);
            } else {
                console.error('Error signing up', error.message);
            }
        }
    };

    return (
        <div>
            <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type="text"
                placeholder="Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <input
                type="text"
                placeholder="Last Name"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
            />
            {/* Example of how to select a role */}
            <select value={role} onChange={(e) => setRole(e.target.value)}>
                <option value="ROLE_USER">User</option>
                <option value="ROLE_ADMIN">Admin</option>
            </select>
            <button onClick={handleSignup}>Sign Up</button>

        </div>
    );
};

export default Signup;
