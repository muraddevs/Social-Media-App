import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode';

const CommentForm = ({ postId, onNewComment }) => {
    const [comment, setComment] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [userName, setUserName] = useState('Unknown User');
    const [userId, setUserId] = useState(null);

    useEffect(() => {
        const token = Cookies.get('token');
        if (token) {
            try {
                const decodedToken = jwtDecode(token);
                setUserId(decodedToken.userId);
                setUserName(decodedToken.userName || 'Unknown User');
            } catch (error) {
                console.error('Error decoding token:', error);
                setError('Failed to decode token');
            }
        }
    }, []);

    const handleComment = async () => {
        if (comment.trim() === '') return;

        setLoading(true);

        try {
            const token = Cookies.get('token');
            if (!token) {
                throw new Error('Token not found');
            }

            const response = await axios.post('http://localhost:8080/api/comments', {
                description: comment,
                postId: postId,
                userId: userId,
                userName: userName // Include username in the payload
            }, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            console.log('Comment posted successfully:', response.data);

            // Notify parent component of the new comment
            if (onNewComment) {
                onNewComment(response.data);
            }

            // Clear the comment input and reset error state
            setComment('');
            setError(null);
        } catch (error) {
            console.error('Error posting comment:', error);
            setError('Failed to post comment');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        handleComment();
    };

    return (
        <form onSubmit={handleSubmit} className="comment-form">
            <textarea
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="Add a comment..."
                rows="4"
                cols="50"
                disabled={loading}
            />
            <button type="submit" disabled={loading}>
                {loading ? 'Posting...' : 'Comment'}
            </button>
            {error && <div className="error-message">{error}</div>}
        </form>
    );
};

export default CommentForm;
