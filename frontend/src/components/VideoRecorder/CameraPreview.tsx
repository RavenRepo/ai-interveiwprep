import React, { useEffect, useRef } from 'react';
import { Box } from '@mui/material';

// ============================================================
// Props
// ============================================================

interface CameraPreviewProps {
    stream: MediaStream | null;
    isRecording: boolean;
}

// ============================================================
// CameraPreview Component
// ============================================================

const CameraPreview: React.FC<CameraPreviewProps> = ({ stream, isRecording }) => {
    const videoRef = useRef<HTMLVideoElement | null>(null);

    // Update video srcObject when stream changes
    useEffect(() => {
        if (videoRef.current) {
            videoRef.current.srcObject = stream;
        }
    }, [stream]);

    return (
        <Box
            sx={{
                position: 'relative',
                width: '100%',
                paddingTop: '56.25%', // 16:9 aspect ratio
                borderRadius: 2,
                overflow: 'hidden',
                bgcolor: '#1a1a1a',
            }}
        >
            {/* Video Element */}
            <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                    transform: 'scaleX(-1)', // Mirror horizontally for selfie view
                }}
            />

            {/* Recording Indicator */}
            {isRecording && (
                <Box
                    sx={{
                        position: 'absolute',
                        top: 12,
                        right: 12,
                        display: 'flex',
                        alignItems: 'center',
                        gap: 0.75,
                        bgcolor: 'rgba(0, 0, 0, 0.6)',
                        px: 1.5,
                        py: 0.5,
                        borderRadius: 1,
                        zIndex: 1,
                    }}
                >
                    <Box
                        sx={{
                            width: 10,
                            height: 10,
                            borderRadius: '50%',
                            bgcolor: 'error.main',
                            animation: 'cameraPulse 1s ease-in-out infinite',
                            '@keyframes cameraPulse': {
                                '0%, 100%': { opacity: 1 },
                                '50%': { opacity: 0.3 },
                            },
                        }}
                    />
                    <Box
                        component="span"
                        sx={{
                            color: '#fff',
                            fontSize: '0.75rem',
                            fontWeight: 600,
                            letterSpacing: 0.5,
                        }}
                    >
                        REC
                    </Box>
                </Box>
            )}

            {/* No Stream Overlay */}
            {!stream && (
                <Box
                    sx={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'grey.600',
                        fontSize: '1rem',
                    }}
                >
                    Camera preview unavailable
                </Box>
            )}
        </Box>
    );
};

export default CameraPreview;
