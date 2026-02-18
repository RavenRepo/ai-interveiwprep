import { useCallback, useEffect, useState } from 'react';

// ============================================================
// useMediaPermissions Hook
// ============================================================

interface UseMediaPermissionsReturn {
    hasPermission: boolean | null;
    isChecking: boolean;
    error: string | null;
    checkPermissions: () => Promise<void>;
    requestPermissions: () => Promise<MediaStream | null>;
}

export const useMediaPermissions = (): UseMediaPermissionsReturn => {
    const [hasPermission, setHasPermission] = useState<boolean | null>(null);
    const [isChecking, setIsChecking] = useState(false);
    const [error, setError] = useState<string | null>(null);

    /**
     * Check if camera/microphone permissions are available.
     * Stops tracks immediately after checking.
     */
    const checkPermissions = useCallback(async () => {
        setIsChecking(true);
        setError(null);

        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true,
            });

            // Stop all tracks — we only needed to verify access
            stream.getTracks().forEach((track) => track.stop());

            setHasPermission(true);
        } catch (err: any) {
            setHasPermission(false);

            if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
                setError('Camera and microphone access was denied. Please allow access in your browser settings.');
            } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
                setError('No camera or microphone found. Please connect a device and try again.');
            } else if (err.name === 'NotReadableError' || err.name === 'TrackStartError') {
                setError('Camera or microphone is already in use by another application.');
            } else if (err.name === 'OverconstrainedError') {
                setError('Camera does not meet the required constraints.');
            } else {
                setError('An unexpected error occurred while accessing media devices.');
            }
        } finally {
            setIsChecking(false);
        }
    }, []);

    /**
     * Request camera and microphone access.
     * Returns the MediaStream if successful, null otherwise.
     */
    const requestPermissions = useCallback(async (): Promise<MediaStream | null> => {
        setIsChecking(true);
        setError(null);

        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true,
            });

            setHasPermission(true);
            return stream;
        } catch (err: any) {
            setHasPermission(false);

            if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
                setError('Camera and microphone access was denied. Please allow access in your browser settings.');
            } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
                setError('No camera or microphone found. Please connect a device and try again.');
            } else if (err.name === 'NotReadableError' || err.name === 'TrackStartError') {
                setError('Camera or microphone is already in use by another application.');
            } else {
                setError('An unexpected error occurred while accessing media devices.');
            }

            return null;
        } finally {
            setIsChecking(false);
        }
    }, []);

    // Check permissions on mount
    useEffect(() => {
        checkPermissions();
    }, [checkPermissions]);

    return {
        hasPermission,
        isChecking,
        error,
        checkPermissions,
        requestPermissions,
    };
};

export default useMediaPermissions;
