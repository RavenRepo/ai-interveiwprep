import React from 'react';
import {
    Box,
    Card,
    CardContent,
    Chip,
    LinearProgress,
    Typography,
} from '@mui/material';
import { Category, TrendingUp } from '@mui/icons-material';

// ============================================================
// Props
// ============================================================

interface QuestionDisplayProps {
    questionText: string;
    questionNumber: number;
    totalQuestions: number;
    category: string;
    difficulty: string;
}

// ============================================================
// Difficulty color mapping
// ============================================================

const getDifficultyColor = (difficulty: string): 'success' | 'warning' | 'error' => {
    switch (difficulty.toUpperCase()) {
        case 'EASY':
            return 'success';
        case 'MEDIUM':
            return 'warning';
        case 'HARD':
            return 'error';
        default:
            return 'warning';
    }
};

// ============================================================
// QuestionDisplay Component
// ============================================================

const QuestionDisplay: React.FC<QuestionDisplayProps> = ({
    questionText,
    questionNumber,
    totalQuestions,
    category,
    difficulty,
}) => {
    const progress = (questionNumber / totalQuestions) * 100;

    return (
        <Card
            elevation={0}
            sx={{
                borderRadius: 3,
                border: '1px solid',
                borderColor: 'divider',
            }}
        >
            <CardContent sx={{ p: 3 }}>
                {/* Progress Bar */}
                <Box sx={{ mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.75 }}>
                        <Typography variant="caption" color="text.secondary" fontWeight={600}>
                            Progress
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                            {questionNumber} of {totalQuestions}
                        </Typography>
                    </Box>
                    <LinearProgress
                        variant="determinate"
                        value={progress}
                        sx={{ height: 6, borderRadius: 3 }}
                    />
                </Box>

                {/* Metadata Chips */}
                <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                    <Chip
                        icon={<Category sx={{ fontSize: 16 }} />}
                        label={category}
                        size="small"
                        variant="outlined"
                    />
                    <Chip
                        icon={<TrendingUp sx={{ fontSize: 16 }} />}
                        label={difficulty}
                        size="small"
                        color={getDifficultyColor(difficulty)}
                    />
                </Box>

                {/* Question Text */}
                <Typography variant="h6" sx={{ fontWeight: 500, lineHeight: 1.6 }}>
                    {questionText}
                </Typography>
            </CardContent>
        </Card>
    );
};

export default QuestionDisplay;
