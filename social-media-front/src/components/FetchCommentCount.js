import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';

const FetchCommentCount = ({ postId }) => {
    const [commentCount, setCommentCount] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCommentCount = async () => {
            try {
                const token = Cookies.get('token'); // Retrieve token from cookies

                if (!token) {
                    throw new Error('Token not found');
                }

                const response = await axios.get(`http://localhost:8080/api/comments/comment/count/${postId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });

                setCommentCount(response.data); // Update state with the comment count
            } catch (error) {
                console.error('Error fetching comment count:', error.response ? error.response.data : error.message);
                setError(error.response ? error.response.data : 'Failed to fetch comment count'); // Set error state with detailed message
            }
        };

        fetchCommentCount();
    }, [postId]);

    return (
        <div>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {commentCount !== null ? <p>{commentCount}</p> : <p>Loading...</p>}
        </div>
    );
};

export default FetchCommentCount;
