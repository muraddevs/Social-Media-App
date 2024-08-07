import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import Cookies from 'js-cookie';
import FollowButton from './FollowButton';
import CommentList from './CommentList';
import ProfilePicUpload from './ProfilePicUpload';
import { jwtDecode } from 'jwt-decode'; // Correct import

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

    const renderProfilePicture = (profilePictureUrl) => {
        const defaultProfilePictureUrl = 'https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg';

        return (
            <img
                src={profilePictureUrl || defaultProfilePictureUrl}
                alt="Profile"
                onError={(e) => {
                    console.error('Failed to load profile picture:', e.target.src);
                    e.target.src = defaultProfilePictureUrl;
                    e.target.alt = 'Profile picture failed to load';
                }}
                style={{
                    width: 100,
                    height: 100,
                    borderRadius: '50%',
                    objectFit: 'cover'
                }}
            />
        );
    };

    if (error) {
        return <div>{error}</div>;
    }

    if (!userDetails) {
        return <div>Loading...</div>;
    }

    return (
        <div className="user-profile-container">
            {renderProfilePicture(userDetails.profilePictureUrl)}
            <h1>{userDetails.userName}</h1>
            {userDetails.id === userId && <ProfilePicUpload onProfilePictureUploaded={fetchUserDetails} />}
            <p>Posts: {userDetails.posts.length}</p>
            <p>Followers: {followerCount}</p>
            <p>Following: {followingCount}</p>
            <FollowButton userIdToFollow={userDetails.id} />
            <h2>Posts</h2>
            <div className="user-posts">
                {userDetails.posts.map(post => (
                    <div key={post.id} className="user-post">
                        <p>{post.description}</p>
                        {post.images && post.images.map((image, index) => (
                            <div key={index}>
                                {renderImage(image)} {/* Render each post image */}
                            </div>
                        ))}
                        <button onClick={() => handleUpvote(post.id)}>Like</button>
                        <span>{post.likeCount} Likes</span>
                        <button onClick={() => handleDownvote(post.id)}>Dislike</button>
                        <span>{post.dislikeCount} Dislikes</span>
                        <button onClick={() => toggleComments(post.id)}>
                            {visibleComments[post.id] ? 'Hide Comments' : 'Show Comments'}
                        </button>
                        {visibleComments[post.id] && (
                            <CommentList postId={post.id} />
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default User;
