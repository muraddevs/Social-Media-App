import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import FollowButton from './FollowButton';
import CommentList from './CommentList'; // Assume you have a CommentList component

const User = () => {
    const { username } = useParams();
    const [userDetails, setUserDetails] = useState(null);
    const [followerCount, setFollowerCount] = useState(0);
    const [followingCount, setFollowingCount] = useState(0);
    const [error, setError] = useState(null);
    const [visibleComments, setVisibleComments] = useState({}); // State to track visible comments

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const token = Cookies.get('token');
                if (!token) throw new Error('Token not found');

                const userResponse = await axios.get(`http://localhost:8080/api/users/username/${username}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                const user = userResponse.data;

                const [followerResponse, followingResponse] = await Promise.all([
                    axios.get(`http://localhost:8080/api/follows/followers/count/${user.id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    }),
                    axios.get(`http://localhost:8080/api/follows/following/count/${user.id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                ]);

                const postsWithDetails = await Promise.all(
                    user.posts.map(async post => {
                        const images = await fetchImages(post.id);
                        const likeCount = await fetchLikeCount(post.id);
                        const dislikeCount = await fetchDislikeCount(post.id);
                        return { ...post, images, likeCount, dislikeCount };
                    })
                );

                // Sort posts by creation date, most recent first
                postsWithDetails.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

                setUserDetails({ ...user, posts: postsWithDetails });
                setFollowerCount(followerResponse.data);
                setFollowingCount(followingResponse.data);
                setError(null);
            } catch (error) {
                console.error('Error fetching user details:', error);
                setError('Error fetching user details');
            }
        };

        fetchUserDetails();
    }, [username]);

    const fetchImages = async (postId) => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            const response = await axios.get(`http://localhost:8080/api/post-images/post/${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            return response.data;
        } catch (error) {
            console.error('Error fetching images:', error);
            return [];
        }
    };

    const fetchLikeCount = async (postId) => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            const response = await axios.get(`http://localhost:8080/api/likes/count?postId=${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            return response.data;
        } catch (error) {
            console.error('Error fetching like count:', error);
            return 0;
        }
    };

    const fetchDislikeCount = async (postId) => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            const response = await axios.get(`http://localhost:8080/api/likes/dislikeCount?postId=${postId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            return response.data;
        } catch (error) {
            console.error('Error fetching dislike count:', error);
            return 0;
        }
    };

    const handleUpvote = async (postId) => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            await axios.post(`http://localhost:8080/api/likes/upvote`, { postId }, {
                headers: { Authorization: `Bearer ${token}` }
            });

            // Refresh like and dislike counts
            const updatedPosts = await Promise.all(
                userDetails.posts.map(async post =>
                    post.id === postId
                        ? {
                            ...post,
                            likeCount: await fetchLikeCount(postId),
                            dislikeCount: await fetchDislikeCount(postId)
                        }
                        : post
                )
            );
            setUserDetails({ ...userDetails, posts: updatedPosts });
        } catch (error) {
            console.error('Error upvoting post:', error);
        }
    };

    const handleDownvote = async (postId) => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            await axios.post(`http://localhost:8080/api/likes/downvote`, { postId }, {
                headers: { Authorization: `Bearer ${token}` }
            });

            // Refresh like and dislike counts
            const updatedPosts = await Promise.all(
                userDetails.posts.map(async post =>
                    post.id === postId
                        ? {
                            ...post,
                            likeCount: await fetchLikeCount(postId),
                            dislikeCount: await fetchDislikeCount(postId)
                        }
                        : post
                )
            );
            setUserDetails({ ...userDetails, posts: updatedPosts });
        } catch (error) {
            console.error('Error downvoting post:', error);
        }
    };

    const toggleComments = (postId) => {
        setVisibleComments(prevVisibleComments => ({
            ...prevVisibleComments,
            [postId]: !prevVisibleComments[postId]
        }));
    };

    const renderImage = (imageData) => {
        if (imageData && imageData.data) {
            return (
                <img
                    src={`data:${imageData.type};base64,${imageData.data}`}
                    alt={imageData.name}
                    onError={(e) => {
                        console.error('Failed to load image:', e.target.src);
                        e.target.src = 'default-image-url.jpg';
                        e.target.alt = 'Image failed to load';
                    }}
                    style={{
                        width: 400,
                        height: 400,
                        objectFit: 'cover'
                    }}
                />
            );
        }

        console.error('Invalid image data:', imageData);
        return null;
    };

    if (error) {
        return <div>{error}</div>;
    }

    if (!userDetails) {
        return <div>Loading...</div>;
    }

    return (
        <div className="user-profile-container">
            <h1>{userDetails.username}</h1>
            <p>Posts: {userDetails.posts.length}</p>
            <p>Followers: {followerCount}</p>
            <p>Following: {followingCount}</p>
            <FollowButton userIdToFollow={userDetails.id} />
            <h2>Posts</h2>
            <div className="user-posts">
                {userDetails.posts.map(post => (
                    <div key={post.id} className="user-post">
                        <p>{post.description}</p>
                        {post.images && post.images.map(imageData => renderImage(imageData))}
                        <p>Likes: {post.likeCount}</p>
                        <p>Dislikes: {post.dislikeCount}</p>
                        <button onClick={() => handleUpvote(post.id)}>Like</button>
                        <button onClick={() => handleDownvote(post.id)}>Dislike</button>
                        <button onClick={() => toggleComments(post.id)}>
                            {visibleComments[post.id] ? 'Hide Comments' : 'Show Comments'}
                        </button>
                        {visibleComments[post.id] && <CommentList postId={post.id} />} {/* Assuming you have a CommentList component */}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default User;
