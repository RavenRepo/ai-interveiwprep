import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';

// ============================================================
// Props
// ============================================================

interface LoadingSpinnerProps {
    /** Optional message to display below spinner */
    message?: string;
    /** 'fullPage' centers in viewport; 'inline' fits in parent */
    variant?: 'fullPage' | 'inline';
    /** Spinner size in px */
    size?: number;
}

// ============================================================
// Component
// ============================================================

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
    message,
    variant = 'inline',
    size = 40,
}) => {
    const isFullPage = variant === 'fullPage';

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                ...(isFullPage
                    ? { position: 'fixed', inset: 0, bgcolor: 'background.default', zIndex: 1300 }
                    : { py: 6, width: '100%' }),
            }}
        >
            <CircularProgress size={size} />
            {message && (
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                    {message}
                </Typography>
            )}
        </Box>
    );
};

export default LoadingSpinner;
