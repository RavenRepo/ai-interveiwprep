import React, { useCallback, useState } from "react";
import ReactPlayer from "react-player";
import {
  Box,
  Card,
  Typography,
  CircularProgress,
  Button,
  Alert,
} from "@mui/material";
import { Refresh, QuestionAnswer } from "@mui/icons-material";

// Cast ReactPlayer to any to suppress "url" prop type error
const Player = ReactPlayer as any;

// ============================================================
// Props
// ============================================================

interface AvatarPlayerProps {
  /** Video URL — if undefined/empty, text-only fallback is shown */
  videoUrl?: string;
  questionText: string;
  onVideoEnd: () => void;
  questionNumber?: number;
  totalQuestions?: number;
}

// ============================================================
// AvatarPlayer Component
// ============================================================

const AvatarPlayer: React.FC<AvatarPlayerProps> = ({
  videoUrl,
  questionText,
  onVideoEnd,
  questionNumber,
  totalQuestions,
}) => {
  const hasVideo = Boolean(videoUrl && videoUrl.trim().length > 0);
  const [isLoading, setIsLoading] = useState(hasVideo);
  const [hasError, setHasError] = useState(false);
  const [retryKey, setRetryKey] = useState(0);

  const handleReady = useCallback(() => {
    setIsLoading(false);
    setHasError(false);
  }, []);

  const handleError = useCallback(() => {
    setIsLoading(false);
    setHasError(true);
  }, []);

  const handleEnded = useCallback(() => {
    onVideoEnd();
  }, [onVideoEnd]);

  const handleRetry = useCallback(() => {
    setIsLoading(true);
    setHasError(false);
    setRetryKey((prev) => prev + 1);
  }, []);

  // Prevent right-click / download
  const handleContextMenu = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
  }, []);

  return (
    <Card
      elevation={0}
      sx={{
        borderRadius: 3,
        border: "1px solid",
        borderColor: "divider",
        overflow: "hidden",
      }}
    >
      {/* Video Player — 16:9 aspect ratio */}
      <Box
        onContextMenu={handleContextMenu}
        sx={{
          position: "relative",
          width: "100%",
          paddingTop: "56.25%",
          bgcolor: "#0a0a0a",
        }}
      >
        {/* Text-only fallback — no avatar video available or error loading */}
        {(!hasVideo || hasError) && (
          <Box
            sx={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              zIndex: 2,
              background:
                "linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)",
              p: 3,
            }}
          >
            <QuestionAnswer sx={{ fontSize: 48, color: "grey.400", mb: 1.5 }} />
            <Typography
              variant="body1"
              sx={{ color: "grey.300", textAlign: "center", maxWidth: 400 }}
            >
              Avatar video is not available for this question.
            </Typography>
            <Typography
              variant="body2"
              sx={{ color: "grey.500", textAlign: "center", mt: 0.5 }}
            >
              Read the question below and click "Start recording" when ready.
            </Typography>
            <Button
              variant="outlined"
              size="small"
              onClick={onVideoEnd}
              sx={{ mt: 2, color: "grey.300", borderColor: "grey.600" }}
            >
              Start recording
            </Button>
          </Box>
        )}

        {/* Loading Spinner */}
        {hasVideo && isLoading && !hasError && (
          <Box
            sx={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              zIndex: 2,
            }}
          >
            <CircularProgress size={48} sx={{ color: "#fff", mb: 1.5 }} />
            <Typography variant="body2" sx={{ color: "grey.400" }}>
              Loading avatar video...
            </Typography>
          </Box>
        )}

        {/* Error State */}
        {hasVideo && hasError && (
          <Box
            sx={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              zIndex: 2,
              p: 3,
            }}
          >
            <Alert
              severity="warning"
              sx={{ mb: 2, maxWidth: 360 }}
              action={
                <Button
                  color="inherit"
                  size="small"
                  onClick={handleRetry}
                  startIcon={<Refresh />}
                >
                  Retry
                </Button>
              }
            >
              Video failed to load.
            </Alert>
            <Typography
              variant="body2"
              sx={{ color: "grey.400", textAlign: "center" }}
            >
              You can still read the question below and record your answer.
            </Typography>
          </Box>
        )}

        {/* React Player */}
        {hasVideo && !hasError && (
          <Box
            sx={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
            }}
          >
            <Player
              key={retryKey}
              url={videoUrl}
              playing
              controls={false}
              width="100%"
              height="100%"
              onReady={handleReady}
              onError={handleError}
              onEnded={handleEnded}
            />
          </Box>
        )}
      </Box>

      {/* Question Info */}
      <Box sx={{ p: 2.5 }}>
        {/* Question Counter */}
        {questionNumber && totalQuestions && (
          <Box
            sx={{
              display: "inline-flex",
              alignItems: "center",
              gap: 0.75,
              bgcolor: "primary.main",
              color: "#fff",
              px: 1.5,
              py: 0.4,
              borderRadius: 1,
              mb: 1.5,
            }}
          >
            <QuestionAnswer sx={{ fontSize: 16 }} />
            <Typography variant="caption" sx={{ fontWeight: 600 }}>
              Question {questionNumber} of {totalQuestions}
            </Typography>
          </Box>
        )}

        {/* Question Text */}
        <Typography variant="body1" sx={{ fontWeight: 500, lineHeight: 1.6 }}>
          {questionText}
        </Typography>
      </Box>
    </Card>
  );
};

export default AvatarPlayer;
