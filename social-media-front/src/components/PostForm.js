import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode'; // Importing jwtDecode

const PostForm = ({ onPostCreated, onCancel }) => {
    const [description, setDescription] = useState('');
    const [file, setFile] = useState(null);
    const [error, setError] = useState(''); // State to manage error messages

    const handlePost = async (event) => {
        event.preventDefault(); // Prevent default form submission behavior

        const token = Cookies.get('token');
        if (!token) {
            setError('Token not found. Please log in.');
            return; // Handle error or redirect to login
        }

        try {
            const decodedToken = jwtDecode(token); // Decode the JWT token
            console.log('Decoded token:', decodedToken); // Log decoded token for verification

            const formData = new FormData();
            formData.append('description', description);
            if (file) {
                formData.append('file', file); // Append file directly, as binary
            }

            const response = await axios.post('http://localhost:8080/api/posts', formData, {
                headers: {
                    Authorization: `Bearer ${token}`, // Use the token directly for authorization
                    'Content-Type': 'multipart/form-data' // Ensure correct content type
                }
            });

            console.log('Post created:', response.data); // Log the response data
            alert('Post created successfully!');

            // Clear form inputs after successful creation
            setDescription('');
            setFile(null);
            setError('');

            // Notify parent component to refresh the feed
            if (onPostCreated) {
                onPostCreated();
            }
        } catch (error) {
            console.error('Error creating post:', error);
            // Extract and display error message if available
            const errorMessage = error.response?.data?.message || 'Failed to create post. Please try again later.';
            setError(errorMessage); // Update error message
        }
    };

    return (
        <div className="post-form-container">
            <h2>Create Post</h2>
            {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>} {/* Display error messages */}
            <form onSubmit={handlePost}>
                <textarea
                    placeholder="Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    style={{ width: '100%', height: '100px', marginBottom: '10px', padding: '10px' }}
                />
                <input
                    type="file"
                    onChange={(e) => setFile(e.target.files[0])}
                    style={{ marginBottom: '10px' }}
                />
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <button
                        type="submit"
                        className="submit-button"
                    >
                        Post
                    </button>
                    <button
                        type="button"
                        onClick={onCancel}
                        className="cancel-button"
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default PostForm;
