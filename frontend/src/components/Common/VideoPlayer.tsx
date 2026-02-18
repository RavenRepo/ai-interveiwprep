import React, { useRef, useState } from 'react';
import ReactPlayer from 'react-player';
import { Box, IconButton, LinearProgress, Typography } from '@mui/material';
import { Fullscreen, Pause, PlayArrow, VolumeOff, VolumeUp } from '@mui/icons-material';

// Cast ReactPlayer to any to suppress "url" prop type error
const Player = ReactPlayer as any;

// ============================================================
// Props
// ============================================================

interface VideoPlayerProps {
    /** Video source URL */
    url: string;
    /** Optional poster / thumbnail image */
    thumbnail?: string;
    /** Player width (CSS value) */
    width?: string | number;
    /** Player aspect ratio height (CSS value) */
    height?: string | number;
    /** Called when playback finishes */
    onEnded?: () => void;
    /** Show custom controls (set false to use native) */
    controls?: boolean;
    /** Auto play enabled */
    autoPlay?: boolean;
}

// ============================================================
// Component
// ============================================================

const VideoPlayer: React.FC<VideoPlayerProps> = ({
    url,
    thumbnail,
    width = '100%',
    height = '100%',
    onEnded,
    controls = true,
}) => {
    const playerRef = useRef<any>(null);
    const containerRef = useRef<HTMLDivElement>(null);

    const [playing, setPlaying] = useState(false);
    const [muted, setMuted] = useState(false);
    const [played, setPlayed] = useState(0);
    const [duration, setDuration] = useState(0);
    const [showControls, setShowControls] = useState(true);

    const togglePlay = () => setPlaying((p) => !p);
    const toggleMute = () => setMuted((m) => !m);

    const handleProgress = (state: any) => {
        setPlayed(state.played);
    };

    const handleSeek = (e: React.MouseEvent<HTMLDivElement>) => {
        const rect = e.currentTarget.getBoundingClientRect();
        const fraction = (e.clientX - rect.left) / rect.width;
        playerRef.current?.seekTo(fraction, 'fraction');
        setPlayed(fraction);
    };

    const handleFullscreen = () => {
        const el = containerRef.current;
        if (!el) return;
        if (document.fullscreenElement) {
            document.exitFullscreen();
        } else {
            el.requestFullscreen();
        }
    };

    const formatTime = (secs: number) => {
        const m = Math.floor(secs / 60);
        const s = Math.floor(secs % 60);
        return `${m}:${s.toString().padStart(2, '0')}`;
    };

    // If controls are disabled, fall back to native player
    if (!controls) {
        return (
            <Box sx={{ width, position: 'relative', aspectRatio: '16/9', borderRadius: 2, overflow: 'hidden', bgcolor: '#000' }}>
                <Player
                    url={url}
                    width="100%"
                    height="100%"
                    controls
                    light={thumbnail}
                    onEnded={onEnded}
                />
            </Box>
        );
    }

    return (
        <Box
            ref={containerRef}
            onMouseEnter={() => setShowControls(true)}
            onMouseLeave={() => playing && setShowControls(false)}
            onContextMenu={(e) => e.preventDefault()}
            sx={{
                width,
                position: 'relative',
                aspectRatio: '16/9',
                borderRadius: 2,
                overflow: 'hidden',
                bgcolor: '#000',
                cursor: 'pointer',
            }}
        >
            <Player
                ref={playerRef}
                url={url}
                width="100%"
                height="100%"
                playing={playing}
                muted={muted}
                light={thumbnail}
                onProgress={handleProgress}
                onDuration={setDuration}
                onEnded={() => { setPlaying(false); onEnded?.(); }}
                onClick={togglePlay}
            // config={{ file: { attributes: { controlsList: 'nodownload', playsInline: true } } }}
            />

            {/* Custom overlay controls */}
            <Box
                sx={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0,
                    right: 0,
                    background: 'linear-gradient(transparent, rgba(0,0,0,0.7))',
                    px: 1.5,
                    pb: 1,
                    pt: 3,
                    opacity: showControls ? 1 : 0,
                    transition: 'opacity 0.3s',
                }}
            >
                {/* Progress bar */}
                <Box onClick={handleSeek} sx={{ cursor: 'pointer', py: 0.5 }}>
                    <LinearProgress
                        variant="determinate"
                        value={played * 100}
                        sx={{
                            height: 4,
                            borderRadius: 2,
                            bgcolor: 'rgba(255,255,255,0.2)',
                            '& .MuiLinearProgress-bar': { bgcolor: '#fff' },
                            '&:hover': { height: 6 },
                            transition: 'height 0.15s',
                        }}
                    />
                </Box>

                {/* Buttons row */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
                    <IconButton size="small" onClick={togglePlay} sx={{ color: '#fff' }}>
                        {playing ? <Pause fontSize="small" /> : <PlayArrow fontSize="small" />}
                    </IconButton>
                    <IconButton size="small" onClick={toggleMute} sx={{ color: '#fff' }}>
                        {muted ? <VolumeOff fontSize="small" /> : <VolumeUp fontSize="small" />}
                    </IconButton>
                    <Typography variant="caption" sx={{ color: '#fff', ml: 0.5, flex: 1, userSelect: 'none' }}>
                        {formatTime(played * duration)} / {formatTime(duration)}
                    </Typography>
                    <IconButton size="small" onClick={handleFullscreen} sx={{ color: '#fff' }}>
                        <Fullscreen fontSize="small" />
                    </IconButton>
                </Box>
            </Box>
        </Box>
    );
};

export default VideoPlayer;
