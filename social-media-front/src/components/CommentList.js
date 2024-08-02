import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import CommentForm from './CommentForm'; // Import CommentForm component

const CommentList = ({ postId }) => {
    const [comments, setComments] = useState([]);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    // Fetch comments function
    const fetchComments = useCallback(async () => {
        setIsLoading(true);
        try {
            const token = Cookies.get('token');
            if (!token) {
                throw new Error('Token not found');
            }

            const response = await axios.get(`http://localhost:8080/api/comments/post/${postId}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            console.log('Fetched comments:', response.data);
            setComments(response.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching comments:', error);
            setError('Failed to fetch comments');
        } finally {
            setIsLoading(false);
        }
    }, [postId]);

    useEffect(() => {
        fetchComments(); // Fetch comments when component mounts or postId changes
    }, [fetchComments]);

    // Function to handle adding a new comment
    const handleNewComment = async (newComment) => {
        // Simulate adding comment with a slight delay
        setTimeout(async () => {
            await fetchComments(); // Fetch updated comments after posting
        }, 500);
    };

    const formatDate = (date) => {
        const options = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: true };
        return new Date(date).toLocaleDateString(undefined, options).replace(',', ' at');
    };

    if (isLoading) {
        return <div>Loading comments...</div>;
    }

    if (error) {
        return <div className="error-message">Error: {error}</div>;
    }

    return (
        <div className="comment-list">
            {comments.length === 0 ? (
                <p>No comments yet.</p>
            ) : (
                comments.map((comment, index) => (
                    <div key={comment.id || index} className="comment-item">
                        <br/>
                        <div className="comment-username">
                            {comment.user ? comment.user.userName : 'Could not get username'}
                        </div>
                        <div className="comment-description">
                            {comment.description || 'No description available'}
                        </div>
                        <div className="comment-date">
                            {comment.createdDate ? formatDate(comment.createdDate) : 'Date not available'}
                        </div>
                        <br/>
                    </div>
                ))
            )}
            <CommentForm postId={postId} onNewComment={handleNewComment} />
        </div>
    );
};

export default CommentList;
