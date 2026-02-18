
import React from 'react';
import { Box, Container, Typography, Link, Stack, TextField, Button, IconButton, useTheme } from '@mui/material';
import FacebookIcon from '@mui/icons-material/Facebook';
import TwitterIcon from '@mui/icons-material/Twitter';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import InstagramIcon from '@mui/icons-material/Instagram';

const Footer: React.FC = () => {
    const theme = useTheme();
    const currentYear = new Date().getFullYear();

    return (
        <Box
            component="footer"
            sx={{
                bgcolor: theme.palette.mode === 'light' ? 'grey.900' : 'grey.900',
                color: 'white',
                pt: 10,
                pb: 4,
            }}
        >
            <Container maxWidth="lg">
                <Box sx={{ display: 'flex', flexWrap: 'wrap', mx: -2 }}>
                    {/* Brand & Newsletter */}
                    <Box sx={{ p: 2, width: { xs: '100%', md: '33.33%' } }}>
                        <Typography variant="h5" fontWeight={700} gutterBottom sx={{ color: 'white' }}>
                            AI Interview Prep
                        </Typography>
                        <Typography variant="body2" sx={{ mb: 4, opacity: 0.7 }}>
                            Master your interview skills with our AI-powered platform. Get instant feedback and land your dream job.
                        </Typography>

                        <Typography variant="subtitle2" fontWeight={600} gutterBottom>
                            Subscribe to our newsletter
                        </Typography>
                        <Stack direction="row" spacing={1}>
                            <TextField
                                size="small"
                                placeholder="Email address"
                                variant="outlined"
                                sx={{
                                    bgcolor: 'rgba(255,255,255,0.1)',
                                    borderRadius: 1,
                                    input: { color: 'white' },
                                    '& fieldset': { border: 'none' },
                                }}
                            />
                            <Button variant="contained" color="primary">
                                Subscribe
                            </Button>
                        </Stack>
                    </Box>

                    {/* Links Column 1 */}
                    <Box sx={{ p: 2, width: { xs: '50%', sm: '33.33%', md: '16.66%' } }}>
                        <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                            Product
                        </Typography>
                        <Stack spacing={1}>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Features</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Pricing</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Enterprise</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Case Studies</Link>
                        </Stack>
                    </Box>

                    {/* Links Column 2 */}
                    <Box sx={{ p: 2, width: { xs: '50%', sm: '33.33%', md: '16.66%' } }}>
                        <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                            Resources
                        </Typography>
                        <Stack spacing={1}>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Blog</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Interview Guide</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Community</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Help Center</Link>
                        </Stack>
                    </Box>

                    {/* Links Column 3 */}
                    <Box sx={{ p: 2, width: { xs: '50%', sm: '33.33%', md: '16.66%' } }}>
                        <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                            Company
                        </Typography>
                        <Stack spacing={1}>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>About Us</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Careers</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Legal</Link>
                            <Link href="#" color="inherit" sx={{ opacity: 0.7, textDecoration: 'none', '&:hover': { opacity: 1 } }}>Contact</Link>
                        </Stack>
                    </Box>
                </Box>

                <Box sx={{ borderTop: '1px solid rgba(255,255,255,0.1)', mt: 8, pt: 4, display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="body2" sx={{ opacity: 0.5 }}>
                        © {currentYear} AI Interview Preparation. All rights reserved.
                    </Typography>

                    <Stack direction="row" spacing={2} sx={{ mt: { xs: 2, sm: 0 } }}>
                        <IconButton size="small" sx={{ color: 'white', opacity: 0.7, '&:hover': { opacity: 1 } }}>
                            <TwitterIcon />
                        </IconButton>
                        <IconButton size="small" sx={{ color: 'white', opacity: 0.7, '&:hover': { opacity: 1 } }}>
                            <LinkedInIcon />
                        </IconButton>
                        <IconButton size="small" sx={{ color: 'white', opacity: 0.7, '&:hover': { opacity: 1 } }}>
                            <FacebookIcon />
                        </IconButton>
                        <IconButton size="small" sx={{ color: 'white', opacity: 0.7, '&:hover': { opacity: 1 } }}>
                            <InstagramIcon />
                        </IconButton>
                    </Stack>
                </Box>
            </Container>
        </Box>
    );
};

export default Footer;
