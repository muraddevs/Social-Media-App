import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import CommentForm from './CommentForm';
import RenderPFP from "./RenderPFP";
import '../design/CommentList.css';

import { LikeOutlined, DislikeOutlined, CommentOutlined } from '@ant-design/icons';

const fetchPfp = async (userId) => {
    let profilePictureUrl = null;
    const token = Cookies.get('token');

    if (!token) {
        console.error('Token not found');
        return profilePictureUrl; // Return null if no token is found
    }

    try {
        const profileImageResponse = await axios.get(`http://localhost:8080/api/user-images/user/${userId}`, {
            headers: { Authorization: `Bearer ${token}` },
            responseType: 'arraybuffer'
        });

        // Convert ArrayBuffer to Base64
        const base64String = btoa(
            new Uint8Array(profileImageResponse.data).reduce((data, byte) => data + String.fromCharCode(byte), '')
        );
        profilePictureUrl = `data:${profileImageResponse.headers['content-type']};base64,${base64String}`;
    } catch (imageError) {
        console.error('Error fetching profile picture:', imageError);
        profilePictureUrl = null; // Handle error or use default image URL
    }

    return profilePictureUrl;
};

const CommentList = ({ postId }) => {
    const [comments, setComments] = useState([])
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

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

            const commentsData = Array.isArray(response.data) ? response.data : [];

            const commentsWithPfp = await Promise.all(
                commentsData.map(async (comment) => {
                    const profilePictureUrl = await fetchPfp(comment.user.id);
                    return { ...comment, user: { ...comment.user, profilePictureUrl } };
                })
            );

            setComments(commentsWithPfp);
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
                        <div className="comment-title">
                            <div className="comment-pfp">
                                <RenderPFP profilePictureUrl={comment.user.profilePictureUrl} width={25} height={25} />
                            </div>
                            <div className="comment-username">
                                {comment.user ? comment.user.userName : 'Could not get username'}
                            </div>
                        </div>
                        <div className="comment-description">
                            {comment.description || 'No description available'}
                        </div>
                        <div className="comment-footer">
                            <div className="comment-activity">
                                <button><LikeOutlined/> Like</button>
                                <button><DislikeOutlined/> Dislike</button>
                                <button><CommentOutlined/> Reply </button>
                            </div>
                            <div className="comment-date">
                                {comment.createdDate ? formatDate(comment.createdDate) : 'Date not available'}
                            </div>
                        </div>
                        <br />
                    </div>
                ))
            )}
            <CommentForm postId={postId} onNewComment={handleNewComment} />
        </div>
    );
};

export default CommentList;
