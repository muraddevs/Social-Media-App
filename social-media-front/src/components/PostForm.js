import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode'; // Correct import statement for jwt-decode

const PostForm = ({ onPostCreated, onCancel }) => {
    const [description, setDescription] = useState('');
    const [files, setFiles] = useState([]); // Allow multiple files
    const [error, setError] = useState('');

    const handlePost = async (event) => {
        event.preventDefault();

        const token = Cookies.get('token');
        if (!token) {
            setError('Token not found. Please log in.');
            return;
        }

        try {
            const decodedToken = jwtDecode(token);

            const formData = new FormData();
            formData.append('description', description);
            files.forEach((file) => formData.append('files', file)); // Append multiple files

            const response = await axios.post('http://localhost:8080/api/posts', formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });

            alert('Post created successfully!');
            setDescription('');
            setFiles([]);
            setError('');
            if (onPostCreated) {
                onPostCreated(response.data);
            }
        } catch (error) {
            console.error('Error creating post:', error);
            const errorMessage = error.response?.data?.message || 'Failed to create post. Please try again later.';
            setError(errorMessage);
        }
    };

    return (
        <div className="post-form-container">
            <h2>Create Post</h2>
            {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
            <form onSubmit={handlePost}>
                <textarea
                    placeholder="Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    style={{ width: '100%', height: '100px', marginBottom: '10px', padding: '10px' }}
                />
                <input
                    type="file"
                    multiple
                    onChange={(e) => setFiles([...e.target.files].slice(0, 5))} // Limit to 5 files
                    style={{ marginBottom: '10px' }}
                />
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <button type="submit" className="submit-button">
                        Post
                    </button>
                    <button type="button" onClick={onCancel} className="cancel-button">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default PostForm;
