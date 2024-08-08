// PostItem.js
import React from 'react';
import { format, isToday, isYesterday, formatDistanceToNow, parseISO } from 'date-fns';
import { LikeOutlined, DislikeOutlined, CommentOutlined } from '@ant-design/icons';
import CommentList from './CommentList';
import FollowButton from './FollowButton';


const PostItem = ({ post, userId, onLike, onDislike, onDelete, onToggleComments, commentsVisible, navigateToUserProfile }) => {

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
        if (typeof imageData === 'object' && Array.isArray(imageData.url) && imageData.url.length > 0) {
            const image = imageData.url[0];
            if (image.data) {
                return (
                    <img
                        src={`data:${image.type};base64,${image.data}`}
                        alt="Post"
                        onError={(e) => {
                            e.target.src = 'default-image-url.jpg';
                            e.target.alt = 'Image failed to load';
                        }}
                        style={{ width: 400, height: 400, objectFit: 'cover' }}
                    />
                );
            }
            if (image.url) {
                return (
                    <img
                        src={image.url}
                        alt="Post"
                        onError={(e) => {
                            e.target.src = 'default-image-url.jpg';
                            e.target.alt = 'Image failed to load';
                        }}
                        style={{ width: 400, height: 400, objectFit: 'cover' }}
                    />
                );
            }
        }
        return null;
    };

    const renderProfilePicture = (profilePictureUrl) => {
        const defaultProfilePictureUrl = 'https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg';
        return (
            <img
                src={profilePictureUrl || defaultProfilePictureUrl}
                alt="Profile"
                onError={(e) => {
                    e.target.src = defaultProfilePictureUrl;
                    e.target.alt = 'Profile picture failed to load';
                }}
                style={{ width: 50, height: 50, borderRadius: '50%', objectFit: 'cover', marginRight: '10px' }}
            />
        );
    };

    return (
        <div className="post-item" style={{ marginBottom: '20px' }}>
            <div className="post-item-header">
                <div className="username" onClick={() => navigateToUserProfile(post.userName)}>
                    <div className="profile-picture" onClick={() => navigateToUserProfile(post.userName)}>
                        {renderProfilePicture(post.profilePictureUrl)}
                    </div>
                    <h3>{post.userName}</h3>
                </div>
                <p>UserId: {post.userId}</p>
                <FollowButton userIdToFollow={post.userId} />
            </div>
            <p>{post.description}</p>
            {post.postImage && renderImage(post.postImage)}
            <p>{formatDate(post.createdAt)}</p>
            <div className="post-item-actions">
                <button onClick={() => onLike(post.id)} className="like-button">
                    <LikeOutlined /> {post.likeCount}
                </button>
                <button onClick={() => onDislike(post.id)} className="dislike-button">
                    <DislikeOutlined /> {post.dislikeCount}
                </button>
                <button onClick={() => onToggleComments(post.id)}>
                    <CommentOutlined /> Comments
                </button>
                {userId === post.userId && (
                    <button onClick={() => onDelete(post.id)} className="delete-button">
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
    );
};

export default PostItem;
