import { MAX_VIDEO_SIZE_BYTES, SUPPORTED_VIDEO_TYPES } from './constants';

// ============================================================
// Auth Validators
// ============================================================

export const validateEmail = (email: string): string | true => {
    if (!email) return 'Email is required';
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) return 'Please enter a valid email address';
    return true;
};

export const validatePassword = (password: string): string | true => {
    if (!password) return 'Password is required';
    if (password.length < 8) return 'Password must be at least 8 characters';
    if (!/[A-Z]/.test(password)) return 'Password must contain at least one uppercase letter';
    if (!/[a-z]/.test(password)) return 'Password must contain at least one lowercase letter';
    if (!/[0-9]/.test(password)) return 'Password must contain at least one number';
    return true;
};

export const validateName = (name: string): string | true => {
    if (!name) return 'Name is required';
    if (name.length < 2) return 'Name must be at least 2 characters';
    if (name.length > 50) return 'Name must be less than 50 characters';
    return true;
};

// ============================================================
// Video Validators
// ============================================================

export const validateVideoFile = (file: File): string | true => {
    if (!file) return 'Video file is required';

    if (file.size > MAX_VIDEO_SIZE_BYTES) {
        const sizeMB = Math.round(MAX_VIDEO_SIZE_BYTES / (1024 * 1024));
        return `Video file size must be less than ${sizeMB}MB`;
    }

    const isSupported = SUPPORTED_VIDEO_TYPES.some((type) =>
        file.type.startsWith(type.split(';')[0])
    );
    if (!isSupported) {
        return 'Unsupported video format. Please use WebM or MP4';
    }

    return true;
};

// ============================================================
// Interview Validators
// ============================================================

export const validateInterviewStart = (
    resumeId: number | null,
    jobRoleId: number | null
): string | true => {
    if (!resumeId) return 'Please select a resume';
    if (!jobRoleId) return 'Please select a job role';
    return true;
};
