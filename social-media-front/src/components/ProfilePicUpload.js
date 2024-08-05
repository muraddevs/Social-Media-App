import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode';

const ProfilePicUpload = ({ onProfilePictureUploaded }) => {
    const [file, setFile] = useState(null); // State to hold the selected file
    const [error, setError] = useState(''); // State to hold any error messages

    // Handle file input change
    const handleFileChange = (event) => {
        setFile(event.target.files[0]); // Update file state with selected file
    };

    // Handle form submission
    const handleProfilePictureUpload = async (event) => {
        event.preventDefault(); // Prevent default form submission behavior

        const token = Cookies.get('token'); // Retrieve token from cookies
        if (!token) {
            console.error('Token not found. Please log in.'); // Log error if token is missing
            setError('Token not found. Please log in.'); // Update error state
            return; // Handle error or redirect to login
        }

        try {
            const decodedToken = jwtDecode(token); // Decode the JWT token
            const userId = decodedToken.userId; // Extract userId from token
            if (!userId) {
                throw new Error('User ID not found in token');
            }

            const formData = new FormData();
            formData.append('file', file); // Append the profile picture file directly
            formData.append('userId', userId); // Append userId to the form data

            const response = await axios.post('http://localhost:8080/api/user-images/upload', formData, {
                headers: {
                    Authorization: `Bearer ${token}`, // Use the token directly for authorization
                    'Content-Type': 'multipart/form-data' // Ensure correct content type
                }
            });

            console.log('Profile picture uploaded:', response.data); // Log the response data
            alert('Profile picture uploaded successfully!');

            // Clear file input after successful upload
            setFile(null);
            setError('');

            // Notify parent component or refresh the UI if needed
            if (onProfilePictureUploaded) {
                onProfilePictureUploaded(response.data);
            }
        } catch (error) {
            console.error('Error uploading profile picture:', error);
            // Extract and display error message if available
            const errorMessage = error.response?.data?.message || 'Failed to upload profile picture. Please try again later.';
            setError(errorMessage); // Update error message
        }
    };

    return (
        <div>
            <form onSubmit={handleProfilePictureUpload}>
                <input type="file" onChange={handleFileChange} accept="image/*" />
                <button type="submit">Upload Profile Picture</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default ProfilePicUpload;
