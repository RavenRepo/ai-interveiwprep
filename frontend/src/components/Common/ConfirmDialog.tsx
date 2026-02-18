import React from 'react';
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
} from '@mui/material';

// ============================================================
// Props
// ============================================================

interface ConfirmDialogProps {
    /** Whether the dialog is open */
    open: boolean;
    /** Dialog title */
    title: string;
    /** Dialog body message */
    message: string;
    /** Label for the confirm button */
    confirmText?: string;
    /** Label for the cancel button */
    cancelText?: string;
    /** Confirm button color */
    confirmColor?: 'primary' | 'secondary' | 'error' | 'success' | 'warning' | 'info';
    /** Called on confirm */
    onConfirm: () => void;
    /** Called on cancel or backdrop click */
    onCancel: () => void;
    /** Disable confirm button (e.g. while processing) */
    loading?: boolean;
}

// ============================================================
// Component
// ============================================================

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
    open,
    title,
    message,
    confirmText = 'Confirm',
    cancelText = 'Cancel',
    confirmColor = 'primary',
    onConfirm,
    onCancel,
    loading = false,
}) => {
    return (
        <Dialog
            open={open}
            onClose={onCancel}
            maxWidth="xs"
            fullWidth
            PaperProps={{ sx: { borderRadius: 3 } }}
        >
            <DialogTitle fontWeight={600}>{title}</DialogTitle>
            <DialogContent>
                <DialogContentText>{message}</DialogContentText>
            </DialogContent>
            <DialogActions sx={{ px: 3, pb: 2 }}>
                <Button onClick={onCancel} color="inherit" disabled={loading}>
                    {cancelText}
                </Button>
                <Button
                    onClick={onConfirm}
                    variant="contained"
                    color={confirmColor}
                    disabled={loading}
                    autoFocus
                >
                    {confirmText}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ConfirmDialog;
