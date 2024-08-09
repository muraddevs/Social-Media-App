import React from "react";

const RenderPFP = ({ profilePictureUrl, width, height }) => {
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
                width: `${width}px`,
                height: `${height}px`,
                borderRadius: '50%',
                objectFit: 'cover',
                marginRight: '10px',
                border: '1px solid rgba(0, 0, 0, 0.2)'
            }}
        />
    );
};

export default RenderPFP;

