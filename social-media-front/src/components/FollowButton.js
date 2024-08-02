import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';

const FollowButton = ({ userIdToFollow }) => {
    const [isFollowing, setIsFollowing] = useState(false);

    const checkIfFollowing = useCallback(async () => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }

        try {
            const decodedToken = JSON.parse(atob(token.split('.')[1]));
            const currentUserId = decodedToken.userId;

            if (!currentUserId || !userIdToFollow) {
                console.error('User ID missing');
                return;
            }

            const response = await axios.get(`http://localhost:8080/api/follows/check?userId=${currentUserId}&followingId=${userIdToFollow}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });

            setIsFollowing(response.data.isFollowing); // Assuming API returns { isFollowing: true/false }
        } catch (error) {
            console.error('Error checking follow status:', error);
        }
    }, [userIdToFollow]);

    useEffect(() => {
        checkIfFollowing();
    }, [checkIfFollowing]);

    const handleFollow = async () => {
        const token = Cookies.get('token');
        if (!token) {
            console.error('Token not found');
            return;
        }

        try {
            const decodedToken = JSON.parse(atob(token.split('.')[1]));
            const currentUserId = decodedToken.userId;

            if (!currentUserId || !userIdToFollow) {
                console.error('User ID missing');
                return;
            }

            if (isFollowing) {
                // If already following, unfollow
                await axios.delete('http://localhost:8080/api/follows', {
                    data: {
                        userId: currentUserId,
                        followingId: userIdToFollow
                    },
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
                setIsFollowing(false);
                alert('Unfollowed successfully');
            } else {
                // If not following, follow
                await axios.post('http://localhost:8080/api/follows', {
                    userId: currentUserId,
                    followingId: userIdToFollow
                }, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
                setIsFollowing(true);
                alert('Followed successfully');
            }
        } catch (error) {
            console.error('Error toggling follow status:', error);
            alert('Failed to update follow status');
        }
    };

    return (
        <button onClick={handleFollow}>
            {isFollowing ? 'Following' : 'Follow'}
        </button>
    );
};

export default FollowButton;
