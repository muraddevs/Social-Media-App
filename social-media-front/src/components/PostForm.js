import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';

import {Upload, Button, message, Image} from 'antd';
import { UploadOutlined } from '@ant-design/icons';

const PostForm = ({ onPostCreated, onCancel }) => {
    const [description, setDescription] = useState('');
    const [files, setFiles] = useState([]); // Array of file objects
    const [error, setError] = useState('');

    const handlePost = async (event) => {
        event.preventDefault();

        const token = Cookies.get('token');
        if (!token) {
            setError('Token not found. Please log in.');
            return;
        }

        // Show loading message
        const loadingMessage = message.loading('Creating post...', 0);

        try {


            const formData = new FormData();
            formData.append('description', description);
            files.forEach((file) => formData.append('files', file)); // Append multiple files

            const response = await axios.post('http://localhost:8080/api/posts', formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });

            // Hide loading message and show success message
            loadingMessage();
            message.success('Post created successfully!');

            setDescription('');
            setFiles([]);
            setError('');
            if (onPostCreated) {
                onPostCreated(response.data);
            }
        } catch (error) {
            console.error('Error creating post:', error);
            const errorMessage = error.response?.data?.message || 'Failed to create post. Please try again later.';
            loadingMessage(); // Hide loading message on error
            message.error(errorMessage);
            setError(errorMessage);
        }
    };

    // Handle file change and enforce the limit of 5 images
    const handleFileChange = (info) => {
        const selectedFiles = info.fileList.map(file => file.originFileObj);
        if (selectedFiles.length > 5) {
            setFiles([]); // Clear the file list
            if (!error) {
                message.error('You can only upload up to 5 images.');
                setError('You can only upload up to 5 images.');
            }
        } else {
            setFiles(selectedFiles);
            setError(''); // Clear the error when valid
        }
    };

    const uploadProps = {
        multiple: true,
        showUploadList: false,
        customRequest: ({ file, onSuccess }) => {
            // Handle custom file upload
            onSuccess && onSuccess(file);
        },
        onChange: handleFileChange,
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
                    style={{width: '100%', height: '100px', marginBottom: '10px', padding: '10px'}}
                />
                <Upload {...uploadProps}>
                    <Button icon={<UploadOutlined />}>Upload Images</Button>
                </Upload>
                <div className="image-previews" style={{
                    display: 'flex',
                    flexDirection: 'row',
                    overflow: 'scroll',
                    marginTop: '10px',
                    padding: '5px', // Optional: Adds padding around the images
                    gap: '10px' // Optional: Adds space between images
                }}>
                    {files.map((file, index) => (
                        <img
                            key={index}
                            src={URL.createObjectURL(file)}
                            alt={`Preview ${index}`}
                            width={150}
                            height={150}
                            style={{
                                objectFit: 'cover',
                            }}
                        />
                    ))}
                </div>

                <div style={{display: 'flex', justifyContent: 'space-between'}}>
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
