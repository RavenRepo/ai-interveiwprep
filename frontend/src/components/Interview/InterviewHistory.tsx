import React, { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Button,
    Card,
    CardContent,
    Chip,
    FormControl,
    InputLabel,
    MenuItem,
    Pagination,
    Select,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import { PlayArrow, Search, Visibility } from '@mui/icons-material';
import { InterviewDTO } from '../../types';

// ============================================================
// Props
// ============================================================

interface InterviewHistoryProps {
    interviews: InterviewDTO[];
}

// ============================================================
// Constants
// ============================================================

const ROWS_PER_PAGE = 10;

// ============================================================
// Helpers
// ============================================================

const getStatusChip = (status: string) => {
    switch (status) {
        case 'COMPLETED':
            return <Chip label="Completed" color="success" size="small" />;
        case 'IN_PROGRESS':
            return <Chip label="In Progress" color="warning" size="small" />;
        case 'PROCESSING':
            return <Chip label="Processing" color="info" size="small" />;
        case 'GENERATING_VIDEOS':
            return <Chip label="Preparing" color="info" variant="outlined" size="small" />;
        case 'CREATED':
            return <Chip label="Created" color="default" variant="outlined" size="small" />;
        case 'FAILED':
            return <Chip label="Failed" color="error" size="small" />;
        default:
            return <Chip label={status} size="small" />;
    }
};

const getScoreDisplay = (score?: number) => {
    if (score === undefined || score === null) {
        return <Typography variant="body2" color="text.secondary">—</Typography>;
    }

    let color: string;
    if (score >= 80) color = 'success.main';
    else if (score >= 60) color = 'warning.main';
    else color = 'error.main';

    return (
        <Typography variant="body2" fontWeight={700} sx={{ color }}>
            {score}/100
        </Typography>
    );
};

// ============================================================
// InterviewHistory Component
// ============================================================

const InterviewHistory: React.FC<InterviewHistoryProps> = ({ interviews }) => {
    const navigate = useNavigate();

    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState<string>('ALL');
    const [page, setPage] = useState(1);

    // ============================================================
    // Filter, search, sort
    // ============================================================

    const filteredInterviews = useMemo(() => {
        let result = [...interviews];

        // Sort by date (newest first)
        result.sort((a, b) => new Date(b.startedAt ?? 0).getTime() - new Date(a.startedAt ?? 0).getTime());

        // Filter by status
        if (statusFilter !== 'ALL') {
            result = result.filter((i) => i.status === statusFilter);
        }

        // Search by job role
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase();
            result = result.filter((i) => (i.jobRoleTitle ?? '').toLowerCase().includes(query));
        }

        return result;
    }, [interviews, statusFilter, searchQuery]);

    // ============================================================
    // Pagination
    // ============================================================

    const totalPages = Math.ceil(filteredInterviews.length / ROWS_PER_PAGE);
    const paginatedInterviews = filteredInterviews.slice(
        (page - 1) * ROWS_PER_PAGE,
        page * ROWS_PER_PAGE
    );

    // Reset page when filters change
    const handleFilterChange = (value: string) => {
        setStatusFilter(value);
        setPage(1);
    };

    const handleSearchChange = (value: string) => {
        setSearchQuery(value);
        setPage(1);
    };

    // ============================================================
    // Render: Empty state
    // ============================================================

    if (interviews.length === 0) {
        return (
            <Card elevation={0} sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider' }}>
                <CardContent sx={{ py: 8, textAlign: 'center' }}>
                    <PlayArrow sx={{ fontSize: 56, color: 'text.disabled', mb: 1 }} />
                    <Typography variant="h6" color="text.secondary" gutterBottom>
                        No interviews yet
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                        Complete your first practice interview to see your history here.
                    </Typography>
                    <Button variant="contained" onClick={() => navigate('/interview/new')}>
                        Start Your First Interview
                    </Button>
                </CardContent>
            </Card>
        );
    }

    // ============================================================
    // Render: Table
    // ============================================================

    return (
        <Box>
            {/* Toolbar: Search + Filter */}
            <Box sx={{ display: 'flex', gap: 2, mb: 2, flexWrap: 'wrap' }}>
                <TextField
                    size="small"
                    placeholder="Search by job role..."
                    value={searchQuery}
                    onChange={(e) => handleSearchChange(e.target.value)}
                    InputProps={{
                        startAdornment: <Search sx={{ color: 'text.disabled', mr: 1 }} />,
                    }}
                    sx={{ flex: 1, minWidth: 200 }}
                />
                <FormControl size="small" sx={{ minWidth: 150 }}>
                    <InputLabel>Status</InputLabel>
                    <Select
                        value={statusFilter}
                        label="Status"
                        onChange={(e) => handleFilterChange(e.target.value)}
                    >
                        <MenuItem value="ALL">All ({interviews.length})</MenuItem>
                        <MenuItem value="COMPLETED">Completed</MenuItem>
                        <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
                        <MenuItem value="PROCESSING">Processing</MenuItem>
                    </Select>
                </FormControl>
            </Box>

            {/* No results after filtering */}
            {filteredInterviews.length === 0 && (
                <Card elevation={0} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider' }}>
                    <CardContent sx={{ py: 4, textAlign: 'center' }}>
                        <Typography color="text.secondary">
                            No interviews match your filters.
                        </Typography>
                    </CardContent>
                </Card>
            )}

            {/* Table */}
            {filteredInterviews.length > 0 && (
                <TableContainer
                    component={Card}
                    elevation={0}
                    sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider' }}
                >
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 600 }}>Date</TableCell>
                                <TableCell sx={{ fontWeight: 600 }}>Job Role</TableCell>
                                <TableCell sx={{ fontWeight: 600 }}>Score</TableCell>
                                <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                                <TableCell sx={{ fontWeight: 600 }} align="right">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {paginatedInterviews.map((interview) => (
                                <TableRow
                                    key={interview.interviewId}
                                    hover
                                    sx={{ cursor: 'pointer' }}
                                    onClick={() => navigate(`/interview/${interview.interviewId}/review`)}
                                >
                                    <TableCell>
                                        <Typography variant="body2">
                                            {new Date(interview.startedAt ?? '').toLocaleDateString()}
                                        </Typography>
                                        <Typography variant="caption" color="text.secondary">
                                            {new Date(interview.startedAt ?? '').toLocaleTimeString([], {
                                                hour: '2-digit',
                                                minute: '2-digit',
                                            })}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight={500}>
                                            {interview.jobRoleTitle ?? 'Interview'}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{getScoreDisplay(interview.overallScore)}</TableCell>
                                    <TableCell>{getStatusChip(interview.status)}</TableCell>
                                    <TableCell align="right">
                                        <Button
                                            size="small"
                                            startIcon={<Visibility />}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                navigate(`/interview/${interview.interviewId}/review`);
                                            }}
                                        >
                                            View
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                    <Pagination
                        count={totalPages}
                        page={page}
                        onChange={(_, value) => setPage(value)}
                        color="primary"
                        shape="rounded"
                    />
                </Box>
            )}
        </Box>
    );
};

export default InterviewHistory;
