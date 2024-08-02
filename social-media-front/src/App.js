import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import Signup from './components/Signup';
import Login from './components/Login'; // Import the Login component
import Home from "./components/Home";
import PostFeed from "./components/PostFeed";
import PostForm from "./components/PostForm";
const App = () => (
    <Router>
        <Routes>
            <Route path="/" element={<Navigate to="/home" />} />
            <Route path="/home" element={<Home />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
            <Route path="/feed" element={<PostFeed />} />
            <Route path="/create" element={<PostForm />} />
        </Routes>
    </Router>
);

export default App;
