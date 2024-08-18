// App.js

import React from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import Signup from './components/Signup';
import Login from './components/Login';
import Home from "./components/Home";
import PostFeed from "./components/PostFeed";

import User from "./components/User";

const App = () => (
    <Router>
        <Routes>
            <Route path="/" element={<Navigate to="/home" />} />
            <Route path="/home" element={<Home />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
            <Route path="/feed" element={<PostFeed />} />

            <Route path="/user/:username" element={<User />} />
        </Routes>
    </Router>
);

export default App;
