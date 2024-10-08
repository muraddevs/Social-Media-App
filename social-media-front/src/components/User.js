import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import FollowButton from './FollowButton';
import CommentList from './CommentList';
import ProfilePicUpload from './ProfilePicUpload';
import { jwtDecode } from 'jwt-decode';
import RenderPFP from "./RenderPFP"; // Correct import
import '../design/UserDesign.css';
import { LikeOutlined, DislikeOutlined, CommentOutlined } from '@ant-design/icons';

const User = () => {
    const { username } = useParams();
    const [userDetails, setUserDetails] = useState(null);
    const [followerCount, setFollowerCount] = useState(0);
    const [followingCount, setFollowingCount] = useState(0);
    const [error, setError] = useState(null);
    const [visibleComments, setVisibleComments] = useState({});
    const [userId, setUserId] = useState(null);

    const fetchUserDetails = async () => {
        try {
            const token = Cookies.get('token');
            if (!token) throw new Error('Token not found');

            const userResponse = await axios.get(`http://localhost:8080/api/users/username/${username}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            const user = userResponse.data;
            const decodedToken = jwtDecode(token);
            const userIdFromToken = decodedToken.userId;
            setUserId(userIdFromToken);

            const [followerResponse, followingResponse] = await Promise.all([
                axios.get(`http://localhost:8080/api/follows/followers/count/${user.id}`, {
                    headers: { Authorization: `Bearer ${token}` }
                }),
                axios.get(`http://localhost:8080/api/follows/following/count/${user.id}`, {
                    headers: { Authorization: `Bearer ${token}` }
                })
            ]);

            // Fetch the user's profile picture
            let profilePictureUrl = null;
            try {
                const profileImageResponse = await axios.get(`http://localhost:8080/api/user-images/user/${user.id}`, {
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
                // Handle error or use default image URL
                profilePictureUrl = null;
            }

            const postsWithDetails = await Promise.all(
                user.posts.map(async post => {
                    const images = await fetchImages(post.id);
                    const likeCount = await fetchLikeCount(post.id);
                    const dislikeCount = await fetchDislikeCount(post.id);
                    return { ...post, images, likeCount, dislikeCount };
                })
            );

            postsWithDetails.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

            setUserDetails({ ...user, posts: postsWithDetails, profilePictureUrl });
            setFollowerCount(followerResponse.data);
            setFollowingCount(followingResponse.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching user details:', error);
            setError('Error fetching user details');
        }
    };



    useEffect(() => {
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
        const defaultImageUrl = 'https://via.placeholder.com/400';

        if (imageData && imageData.data) {
            return (
                <img
                    src={`data:${imageData.type};base64,${imageData.data}`}
                    alt={imageData.name}
                    onError={(e) => {
                        console.error('Failed to load image:', e.target.src);
                        e.target.src = defaultImageUrl;
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
        return <img src={defaultImageUrl} alt="Default" style={{ width: 400, height: 400, objectFit: 'cover' }} />;
    };


    if (error) {
        return <div>{error}</div>;
    }

    if (!userDetails) {
        return <div>Loading...</div>;
    }

    return (
        <div className="user-profile-container">
            <div className="user-profile-header">
                <RenderPFP profilePictureUrl={userDetails.profilePictureUrl} width={150} height={150} />
                <div className="user-profile-info">
                    <h1 className="user-profile-username">{userDetails.userName}</h1>
                    {userDetails.id === userId && <ProfilePicUpload onProfilePictureUploaded={fetchUserDetails} />}
                    <div className="user-stats">
                        <p className="user-posts-count">Posts: {userDetails.posts.length}</p>
                        <p className="user-followers-count">Followers: {followerCount}</p>
                        <p className="user-following-count">Following: {followingCount}</p>
                    </div>
                    <FollowButton userIdToFollow={userDetails.id} />
                </div>
            </div>
            <div className="user-posts-section">
                <h2 className="user-posts-header">Posts</h2>
                <div className="user-posts-list">
                    {userDetails.posts.map(post => (
                        <div key={post.id} className="user-post-item">
                            <RenderPFP profilePictureUrl={userDetails.profilePictureUrl} width={50} height={50} />
                            <h2>{userDetails.userName}</h2>
                            <p className="user-post-description">{post.description}</p>
                            {post.images && post.images.length > 0 && (
                                <div className="user-post-images">
                                    {post.images.map((image, index) => (
                                        <div key={index} className="user-post-image">
                                            {renderImage(image)}
                                        </div>
                                    ))}
                                </div>
                            )}

                            <div className="user-post-actions">
                                <button className="user-post-like" onClick={() => handleUpvote(post.id)}><LikeOutlined/></button>
                                <span className="user-post-like-count">{post.likeCount} </span>
                                <button className="user-post-dislike" onClick={() => handleDownvote(post.id)}><DislikeOutlined/></button>
                                <span className="user-post-dislike-count">{post.dislikeCount} </span>
                                <button className="user-post-comments-toggle" onClick={() => toggleComments(post.id)}>
                                     <CommentOutlined />
                                </button>
                                {visibleComments[post.id] && (
                                    <CommentList postId={post.id} />
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default User;
