import React, { useState } from 'react';
import { Alert, AlertTitle, Collapse, IconButton } from '@mui/material';
import { Close } from '@mui/icons-material';

// ============================================================
// Props
// ============================================================

interface ErrorMessageProps {
    /** Error message text */
    message: string;
    /** Alert severity level */
    severity?: 'error' | 'warning' | 'info' | 'success';
    /** Optional bold title above the message */
    title?: string;
    /** Allow user to dismiss */
    dismissible?: boolean;
    /** Called when dismissed */
    onDismiss?: () => void;
    /** MUI sx prop pass-through */
    sx?: object;
}

// ============================================================
// Component
// ============================================================

const ErrorMessage: React.FC<ErrorMessageProps> = ({
    message,
    severity = 'error',
    title,
    dismissible = false,
    onDismiss,
    sx,
}) => {
    const [open, setOpen] = useState(true);

    const handleClose = () => {
        setOpen(false);
        onDismiss?.();
    };

    return (
        <Collapse in={open}>
            <Alert
                severity={severity}
                sx={{ borderRadius: 2, ...sx }}
                action={
                    dismissible ? (
                        <IconButton size="small" color="inherit" onClick={handleClose}>
                            <Close fontSize="small" />
                        </IconButton>
                    ) : undefined
                }
            >
                {title && <AlertTitle>{title}</AlertTitle>}
                {message}
            </Alert>
        </Collapse>
    );
};

export default ErrorMessage;
