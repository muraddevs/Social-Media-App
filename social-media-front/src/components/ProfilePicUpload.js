import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode';

const ProfilePicUpload = ({ onProfilePictureUploaded }) => {
    const [file, setFile] = useState(null);
    const [error, setError] = useState('');

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleProfilePictureUpload = async (event) => {
        event.preventDefault();

        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found. Please log in.');
            setError('Token not found. Please log in.');
            return;
        }

        try {
            const decodedToken = jwtDecode(token);
            const userId = decodedToken.userId;
            if (!userId) {
                throw new Error('User ID not found in token');
            }

            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', userId);

            const response = await axios.post('http://localhost:8080/api/user-images/upload', formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                },
            });

            console.log('Profile picture uploaded:', response.data);
            alert('Profile picture uploaded successfully!');

            setFile(null);
            setError('');

            if (onProfilePictureUploaded) {
                onProfilePictureUploaded(response.data);
            }
        } catch (error) {
            console.error('Error uploading profile picture:', error);
            const errorMessage = error.response?.data?.message || 'Failed to upload profile picture. Please try again later.';
            setError(errorMessage);
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
