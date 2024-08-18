import React from 'react';
import { format, isToday, isYesterday, formatDistanceToNow, parseISO } from 'date-fns';
import { LikeOutlined, DislikeOutlined, CommentOutlined } from '@ant-design/icons';
import CommentList from './CommentList';
import FollowButton from './FollowButton';
import RenderPFP from "./RenderPFP";
import '../design/PostFeedDesign.css';
import axios from 'axios';
import Cookies from 'js-cookie';

const PostItem = ({ post, userId, fetchPosts, onDeletePost }) => {
    const [commentsVisible, setCommentsVisible] = React.useState(false);


    const renderImage = (imageData) => {
        if (imageData) {
            return (
                <img
                    src={imageData}
                    alt="Post"
                    onError={(e) => {
                        console.error('Failed to load image:', e.target.src);
                        e.target.src = 'default-image-url.jpg';
                        e.target.alt = 'Image failed to load';
                    }}
                    style={{
                        width: 400,
                        height: 400,
                        objectFit: 'cover',
                        margin: '10px',
                    }}
                />
            );
        }
        console.error('Image data is missing or invalid:', imageData);
        return null;
    };

    const handleLike = async () => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }
        try {
            await axios.post('http://localhost:8080/api/likes/upvote', { postId: post.id }, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchPosts();
        } catch (error) {
            console.error('Error liking post:', error);
            alert('Failed to like post');
        }
    };

    const handleDislike = async () => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }
        try {
            await axios.post('http://localhost:8080/api/likes/downvote', { postId: post.id }, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchPosts();
        } catch (error) {
            console.error('Error disliking post:', error);
            alert('Failed to dislike post');
        }
    };

    const handleDeletePost = async () => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }
        try {
            await axios.delete(`http://localhost:8080/api/posts/${post.id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            onDeletePost(post.id); // Callback to update parent component state
        } catch (error) {
            console.error('Error deleting post:', error);
            alert('Failed to delete post');
        }
    };

    return (
        <div className="post-feed-card" style={{ marginBottom: '20px' }}>
            <div className="post-feed-content">
                <div className="post-feed-header">
                    <div className="post-feed-title">
                        <div className="profile-picture">
                            <RenderPFP profilePictureUrl={post.profilePictureUrl} width={50} height={50} />
                        </div>
                        <div className="username">
                            <h3>{post.userName}</h3>
                        </div>
                        <FollowButton className="follow-button" userIdToFollow={post.userId} />
                    </div>
                    <div className="post-body">
                        <p>{post.description}</p>
                        <div className="post-images-container">
                            {post.postImage && renderImage(post.postImage)}
                        </div>
                        <p>{formatDate(post.createdAt)}</p>
                    </div>
                </div>
                <div className="post-feed-body">
                    <div className="post-feed-actions">
                        <button onClick={handleLike} className="like-button">
                            <LikeOutlined /> {post.likeCount}
                        </button>
                        <button onClick={handleDislike} className="dislike-button">
                            <DislikeOutlined /> {post.dislikeCount}
                        </button>
                        <button onClick={() => setCommentsVisible(!commentsVisible)}>
                            <CommentOutlined /> Comments
                        </button>
                        {userId === post.userId && (
                            <button onClick={handleDeletePost} className="delete-button">
                                Delete Post
                            </button>
                        )}
                    </div>
                    {commentsVisible && (
                        <div className="comments-section">
                            <CommentList postId={post.id} />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PostItem;