import React, { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Breadcrumbs,
    IconButton,
    Link,
    Typography,
} from '@mui/material';
import { ArrowBack, NavigateNext } from '@mui/icons-material';

// ============================================================
// Props
// ============================================================

interface BreadcrumbItem {
    label: string;
    href?: string;
}

interface PageHeaderProps {
    /** Page title */
    title: string;
    /** Optional subtitle */
    subtitle?: string;
    /** Breadcrumb trail */
    breadcrumbs?: BreadcrumbItem[];
    /** Show a back arrow that calls navigate(-1) */
    showBack?: boolean;
    /** Action buttons rendered on the right */
    actions?: ReactNode;
    /** MUI sx prop pass-through */
    sx?: object;
}

// ============================================================
// Component
// ============================================================

const PageHeader: React.FC<PageHeaderProps> = ({
    title,
    subtitle,
    breadcrumbs,
    showBack = false,
    actions,
    sx,
}) => {
    const navigate = useNavigate();

    return (
        <Box sx={{ mb: 3, ...sx }}>
            {/* Breadcrumbs */}
            {breadcrumbs && breadcrumbs.length > 0 && (
                <Breadcrumbs separator={<NavigateNext fontSize="small" />} sx={{ mb: 1 }}>
                    {breadcrumbs.map((crumb, idx) =>
                        crumb.href ? (
                            <Link
                                key={idx}
                                underline="hover"
                                color="text.secondary"
                                href={crumb.href}
                                onClick={(e: React.MouseEvent) => {
                                    e.preventDefault();
                                    navigate(crumb.href!);
                                }}
                                sx={{ fontSize: '0.875rem' }}
                            >
                                {crumb.label}
                            </Link>
                        ) : (
                            <Typography key={idx} variant="body2" color="text.primary" fontWeight={500}>
                                {crumb.label}
                            </Typography>
                        )
                    )}
                </Breadcrumbs>
            )}

            {/* Title row */}
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2, flexWrap: 'wrap' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {showBack && (
                        <IconButton onClick={() => navigate(-1)} size="small" sx={{ mr: 0.5 }}>
                            <ArrowBack />
                        </IconButton>
                    )}
                    <Box>
                        <Typography variant="h4" fontWeight={700}>
                            {title}
                        </Typography>
                        {subtitle && (
                            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.25 }}>
                                {subtitle}
                            </Typography>
                        )}
                    </Box>
                </Box>

                {actions && <Box sx={{ display: 'flex', gap: 1 }}>{actions}</Box>}
            </Box>
        </Box>
    );
};

export default PageHeader;
