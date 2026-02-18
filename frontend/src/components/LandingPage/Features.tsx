
import React from 'react';
import { Box, Container, Typography, Paper, useTheme } from '@mui/material';
import PsychologyIcon from '@mui/icons-material/Psychology';
import VideocamIcon from '@mui/icons-material/Videocam';
import AutoGraphIcon from '@mui/icons-material/AutoGraph';
import SecurityIcon from '@mui/icons-material/Security';

const features = [
  {
    icon: <PsychologyIcon fontSize="large" color="primary" />,
    title: 'AI-Powered Questions',
    description: 'Get interview questions tailored specifically to your job role, industry, and experience level.',
  },
  {
    icon: <VideocamIcon fontSize="large" color="secondary" />,
    title: 'Video Simulation',
    description: 'Practice answering in a realistic video interview environment to get comfortable on camera.',
  },
  {
    icon: <AutoGraphIcon fontSize="large" color="success" />,
    title: 'Instant Feedback',
    description: 'Receive detailed analysis of your answers, including pacing, clarity, and keyword usage.',
  },
  {
    icon: <SecurityIcon fontSize="large" color="info" />,
    title: 'Private & Secure',
    description: 'Your practice sessions are private. We prioritize your data security so you can practice freely.',
  },
];

const Features: React.FC = () => {
  const theme = useTheme();

  return (
    <Box sx={{ py: 10, bgcolor: 'background.paper' }}>
      <Container maxWidth="lg">
        <Typography
          variant="h3"
          component="h2"
          align="center"
          gutterBottom
          sx={{ fontWeight: 700, mb: 6 }}
        >
          Everything You Need to Succeed
        </Typography>
        
        <Box sx={{ display: 'flex', flexWrap: 'wrap', mx: -2 }}>
          {features.map((feature, index) => (
            <Box 
              key={index} 
              sx={{ 
                p: 2, 
                width: { xs: '100%', sm: '50%', md: '25%' },
                display: 'flex'
              }}
            >
              <Paper
                elevation={0}
                sx={{
                  p: 4,
                  width: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  textAlign: 'center',
                  borderRadius: 4,
                  transition: 'transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out',
                  border: `1px solid ${theme.palette.divider}`,
                  bgcolor: theme.palette.background.default,
                  '&:hover': {
                    transform: 'translateY(-8px)',
                    boxShadow: theme.shadows[4],
                    borderColor: theme.palette.primary.light,
                  },
                }}
              >
                <Box
                  sx={{
                    mb: 2,
                    p: 2,
                    borderRadius: '50%',
                    bgcolor: (theme) => theme.palette.mode === 'light' ? 'rgba(0,0,0,0.04)' : 'rgba(255,255,255,0.04)',
                  }}
                >
                  {feature.icon}
                </Box>
                <Typography variant="h6" component="h3" gutterBottom fontWeight={600}>
                  {feature.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {feature.description}
                </Typography>
              </Paper>
            </Box>
          ))}
        </Box>
      </Container>
    </Box>
  );
};

export default Features;
