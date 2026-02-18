import React, { ReactNode } from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';

// ============================================================
// Props
// ============================================================

interface StatCardProps {
    /** Stat label */
    title: string;
    /** Stat value (can be number or formatted string) */
    value: string | number;
    /** MUI icon element */
    icon: ReactNode;
    /** Theme color key or custom hex */
    color?: string;
    /** Use a gradient background for the icon */
    gradient?: boolean;
    /** Optional trend text, e.g. "+12%" */
    trend?: string;
    /** Trend direction */
    trendDirection?: 'up' | 'down' | 'neutral';
    /** MUI sx pass-through */
    sx?: object;
}

// ============================================================
// Component
// ============================================================

const StatCard: React.FC<StatCardProps> = ({
    title,
    value,
    icon,
    color = '#1976d2',
    gradient = false,
    trend,
    trendDirection = 'neutral',
    sx,
}) => {
    const trendColor =
        trendDirection === 'up' ? '#2e7d32' : trendDirection === 'down' ? '#d32f2f' : 'text.secondary';

    return (
        <Card elevation={0} sx={{ border: '1px solid', borderColor: 'divider', height: '100%', ...sx }}>
            <CardContent sx={{ display: 'flex', alignItems: 'flex-start', gap: 2 }}>
                {/* Icon */}
                <Box
                    sx={{
                        width: 48,
                        height: 48,
                        borderRadius: 2,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                        color: '#fff',
                        ...(gradient
                            ? { background: `linear-gradient(135deg, ${color}, ${color}cc)` }
                            : { bgcolor: color + '14', color }),
                    }}
                >
                    {icon}
                </Box>

                {/* Text */}
                <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography variant="body2" color="text.secondary" noWrap>
                        {title}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 1 }}>
                        <Typography variant="h5" fontWeight={700}>
                            {value}
                        </Typography>
                        {trend && (
                            <Typography variant="caption" fontWeight={600} sx={{ color: trendColor }}>
                                {trend}
                            </Typography>
                        )}
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
};

export default StatCard;
