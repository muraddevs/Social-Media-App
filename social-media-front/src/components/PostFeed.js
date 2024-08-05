// PostFeed.js

import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import PostForm from './PostForm';
import '../design/PostFeedDesign.css';
import { format, isToday, isYesterday, formatDistanceToNow, parseISO } from 'date-fns';
import { PlusCircleOutlined, LikeOutlined, DislikeOutlined, CommentOutlined } from '@ant-design/icons';
import CommentList from './CommentList';
import FollowButton from "./FollowButton";
import {useNavigate} from "react-router-dom";


const PostFeed = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [userId, setUserId] = useState(null);
    const [commentsVisible, setCommentsVisible] = useState({});

    const navigate = useNavigate();

    const navigateToUserProfile = (userName) => {
        navigate(`/user/${userName}`); // Programmatically navigate to user profile
    };

    const handleCancel = () => {
        setIsFormOpen(false); // Close the form and show the button
    };

    const fetchImages = async (postId) => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            const response = await axios.get(`http://localhost:8080/api/post-images/post/${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            console.log('Fetched image data:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching images:', error);
            return null;
        }
    };

    const fetchPosts = useCallback(async () => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            const response = await axios.get('http://localhost:8080/api/posts', {
                headers: { Authorization: `Bearer ${token}` }
            });

            const decodedToken = JSON.parse(atob(token.split('.')[1]));
            const userIdFromToken = decodedToken.userId;
            console.log('Decoded userId from token:', userIdFromToken); // Log userId
            setUserId(userIdFromToken);

            if (Array.isArray(response.data)) {
                const sortedPosts = response.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

                const postsWithDetails = await Promise.all(sortedPosts.map(async (post) => {
                    try {
                        const imageUrl = await fetchImages(post.id);
                        const [likesResponse, dislikesResponse] = await Promise.all([
                            axios.get('http://localhost:8080/api/likes/count', { params: { postId: post.id }, headers: { Authorization: `Bearer ${token}` } }),
                            axios.get('http://localhost:8080/api/likes/dislikeCount', { params: { postId: post.id }, headers: { Authorization: `Bearer ${token}` } })
                        ]);

                        return {
                            ...post,
                            postImage: imageUrl ? { url: imageUrl } : null,
                            likeCount: likesResponse.data || 0,
                            dislikeCount: dislikesResponse.data || 0,
                        };
                    } catch (error) {
                        console.error('Error fetching post details:', error);
                        return {
                            ...post,
                            postImage: null,
                            likeCount: 0,
                            dislikeCount: 0,
                        };
                    }
                }));

                setPosts(postsWithDetails);
                setError(null);
            } else {
                console.error('Response data is not an array:', response.data);
                setError('Error fetching posts');
            }
        } catch (error) {
            console.error('Error fetching posts:', error);
            setError('Error fetching posts');
        }
    }, []);

    useEffect(() => {
        fetchPosts();
        const intervalId = setInterval(fetchPosts, 30000);
        return () => clearInterval(intervalId);
    }, [fetchPosts]);

    const formatDate = (dateString) => {
        const postDate = parseISO(dateString);

        if (isToday(postDate)) {
            return `Posted today at ${format(postDate, 'h:mm a')}`;
        } else if (isYesterday(postDate)) {
            return `Posted yesterday at ${format(postDate, 'h:mm a')}`;
        } else if (postDate > new Date(new Date().setDate(new Date().getDate() - 7))) {
            return `Posted ${formatDistanceToNow(postDate, { addSuffix: true })}`;
        } else {
            return `Posted on ${format(postDate, 'd MMMM yyyy')} at ${format(postDate, 'h:mm a')}`;
        }
    };

    const renderImage = (imageData) => {
        console.log('Image Data:', imageData); // Log image data for debugging

        // Check if imageData is an object and has a url property
        if (typeof imageData === 'object' && Array.isArray(imageData.url) && imageData.url.length > 0) {
            const image = imageData.url[0];

            // Check if image has a data property (for base64 data)
            if (image.data) {
                return (
                    <img
                        src={`data:${image.type};base64,${image.data}`}
                        alt="Post"
                        onError={(e) => {
                            // Log the error and provide a fallback
                            console.error('Failed to load image:', e.target.src);
                            e.target.src = 'default-image-url.jpg'; // Fallback image URL
                            e.target.alt = 'Image failed to load'; // Update alt text
                        }}
                        style={{
                            width: 400,
                            height: 400,
                            objectFit: 'cover' // Ensures the image covers the container
                        }}
                    />
                );
            }

            // If image does not have base64 data but just a URL
            if (image.url) {
                return (
                    <img
                        src={image.url}
                        alt="Post"
                        onError={(e) => {
                            // Log the error and provide a fallback
                            console.error('Failed to load image:', e.target.src);
                            e.target.src = 'default-image-url.jpg'; // Fallback image URL
                            e.target.alt = 'Image failed to load'; // Update alt text
                        }}
                        style={{
                            width: 400,
                            height: 400,
                            objectFit: 'cover' // Ensures the image covers the container
                        }}
                    />
                );
            }
        }

        console.error('Invalid image data:', imageData);
        return null;
    };

    // Update the handleNewPost function
    const handleNewPost = useCallback(async (newPost) => {
        try {
            // Fetch details for the new post
            const token = Cookies.get('token');
            const imageUrl = await fetchImages(newPost.id);
            const [likesResponse, dislikesResponse] = await Promise.all([
                axios.get('http://localhost:8080/api/likes/count', { params: { postId: newPost.id }, headers: { Authorization: `Bearer ${token}` } }),
                axios.get('http://localhost:8080/api/likes/dislikeCount', { params: { postId: newPost.id }, headers: { Authorization: `Bearer ${token}` } })
            ]);

            const newPostWithDetails = {
                ...newPost,
                postImage: imageUrl ? { url: imageUrl } : null,
                likeCount: likesResponse.data || 0,
                dislikeCount: dislikesResponse.data || 0,
            };

            // Add the new post to the beginning of the posts array
            setPosts((prevPosts) => [newPostWithDetails, ...prevPosts]);
            setIsFormOpen(false);
        } catch (error) {
            console.error('Error adding new post:', error);
        }
    }, []);

    const handleLike = async (postId) => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }

        try {
            await axios.post('http://localhost:8080/api/likes/upvote', { postId }, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchPosts();
        } catch (error) {
            console.error('Error liking post:', error);
            alert('Failed to like post');
        }
    };

    const handleDislike = async (postId) => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }

        try {
            await axios.post('http://localhost:8080/api/likes/downvote', { postId }, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchPosts();
        } catch (error) {
            console.error('Error disliking post:', error);
            alert('Failed to dislike post');
        }
    };

    const handleDeletePost = async (postId) => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }

        try {
            await axios.delete(`http://localhost:8080/api/posts/${postId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            fetchPosts();
        } catch (error) {
            console.error('Error deleting post:', error);
            alert('Failed to delete post');
        }
    };

    const toggleComments = (postId) => {
        setCommentsVisible(prev => ({ ...prev, [postId]: !prev[postId] }));
    };

    return (
        <div className="post-feed-container">
            <h2>Post Feed</h2>

            {error && <p className="error-message">{error}</p>}

            <div className="post-feed">
                {posts.length === 0 ? (
                    <p>No posts found.</p>
                ) : (
                    posts.map(post => (
                        <div className="post-feed-card" key={post.id} style={{ marginBottom: '20px' }}>

                            <div className="post-feed-content">
                                <div className="post-feed-header">
                                    <div className="username" onClick={() => navigateToUserProfile(post.userName)}>
                                        <h3>{post.userName}</h3>
                                    </div>
                                    <p>UserId: {post.userId}</p> {/* Add logging */}
                                    <FollowButton userIdToFollow={post.userId}/>
                                    <p>{post.description}</p>
                                    {post.postImage && renderImage(post.postImage)}
                                    <p>{formatDate(post.createdAt)}</p>
                                </div>

                                <div className="post-feed-body">
                                <div className="post-feed-actions">
                                        <button onClick={() => handleLike(post.id)} className="like-button">
                                            <LikeOutlined/> {post.likeCount}
                                        </button>
                                        <button onClick={() => handleDislike(post.id)} className="dislike-button">
                                            <DislikeOutlined/> {post.dislikeCount}
                                        </button>
                                        <button onClick={() => toggleComments(post.id)}>
                                            <CommentOutlined/> Comments
                                        </button>
                                        {userId === post.userId && (
                                            <button onClick={() => handleDeletePost(post.id)} className="delete-button">
                                                Delete Post
                                            </button>
                                        )}
                                    </div>

                                    {commentsVisible[post.id] && (
                                        <div className="comments-section">
                                            <CommentList postId={post.id} />
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {isFormOpen ? (
                <PostForm onPostCreated={handleNewPost} onCancel={handleCancel} />
            ) : (
                <button className="open-form-button" onClick={() => setIsFormOpen(true)}>
                    <PlusCircleOutlined /> Add New Post
                </button>
            )}
        </div>
    );
};

export default PostFeed;
