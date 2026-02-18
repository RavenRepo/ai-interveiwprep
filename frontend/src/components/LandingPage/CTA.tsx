
import React from 'react';
import { Box, Container, Typography, Button, useTheme } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

const CTA: React.FC = () => {
    const theme = useTheme();
    const navigate = useNavigate();

    return (
        <Box
            sx={{
                py: 12,
                background: `linear-gradient(45deg, ${theme.palette.primary.dark} 30%, ${theme.palette.primary.main} 90%)`,
                color: 'white',
            }}
        >
            <Container maxWidth="md" sx={{ textAlign: 'center' }}>
                <Typography
                    variant="h3"
                    fontWeight={800}
                    gutterBottom
                    sx={{ mb: 3 }}
                >
                    Ready to Master Your Next Interview?
                </Typography>
                <Typography
                    variant="h6"
                    sx={{ mb: 5, opacity: 0.9, fontWeight: 400, maxWidth: 600, mx: 'auto' }}
                >
                    Join thousands of job seekers who are improving their interview skills and getting hired faster.
                </Typography>

                <Button
                    variant="contained"
                    size="large"
                    color="secondary"
                    onClick={() => navigate('/register')}
                    endIcon={<ArrowForwardIcon />}
                    sx={{
                        px: 6,
                        py: 2,
                        fontSize: '1.2rem',
                        fontWeight: 700,
                        backgroundColor: 'white',
                        color: theme.palette.primary.main,
                        '&:hover': {
                            backgroundColor: 'rgba(255,255,255,0.9)',
                        },
                    }}
                >
                    Get Started for Free
                </Button>
                <Typography variant="body2" sx={{ mt: 2, opacity: 0.7 }}>
                    No credit card required • Cancel anytime
                </Typography>
            </Container>
        </Box>
    );
};

export default CTA;
