import React, { ReactNode } from 'react';
import { Box, Button, Typography } from '@mui/material';
import { InboxOutlined } from '@mui/icons-material';

// ============================================================
// Props
// ============================================================

interface EmptyStateProps {
    /** MUI icon element to display */
    icon?: ReactNode;
    /** Heading text */
    title: string;
    /** Description text */
    message?: string;
    /** Optional action button label */
    actionLabel?: string;
    /** Called when action button is clicked */
    onAction?: () => void;
    /** MUI sx prop pass-through */
    sx?: object;
}

// ============================================================
// Component
// ============================================================

const EmptyState: React.FC<EmptyStateProps> = ({
    icon,
    title,
    message,
    actionLabel,
    onAction,
    sx,
}) => {
    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                textAlign: 'center',
                py: 8,
                px: 3,
                ...sx,
            }}
        >
            <Box sx={{ color: 'text.disabled', mb: 2, '& .MuiSvgIcon-root': { fontSize: 64 } }}>
                {icon || <InboxOutlined />}
            </Box>

            <Typography variant="h6" fontWeight={600} gutterBottom>
                {title}
            </Typography>

            {message && (
                <Typography variant="body2" color="text.secondary" sx={{ maxWidth: 360, mb: actionLabel ? 3 : 0 }}>
                    {message}
                </Typography>
            )}

            {actionLabel && onAction && (
                <Button variant="contained" onClick={onAction}>
                    {actionLabel}
                </Button>
            )}
        </Box>
    );
};

export default EmptyState;
