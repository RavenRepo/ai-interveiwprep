
import React from 'react';
import { Box, Container, Typography, Button, Paper, List, ListItem, ListItemIcon, ListItemText, useTheme, Chip } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import StarIcon from '@mui/icons-material/Star';
import { useNavigate } from 'react-router-dom';

const pricingPlans = [
  {
    title: 'Free',
    price: '$0',
    period: '/forever',
    features: [
      '5 AI Interview Sessions',
      'Basic Performance Analysis',
      'Common Interview Questions',
      '720p Video Recording',
    ],
    buttonText: 'Get Started',
    buttonVariant: 'outlined' as const,
    highlight: false,
  },
  {
    title: 'Pro',
    price: '$19',
    period: '/month',
    features: [
      'Unlimited Interview Sessions',
      'Advanced AI Feedback & Scoring',
      'Role-Specific Question Banks',
      'HD Video Recording & Export',
      'Progress Tracking Dashboard',
    ],
    buttonText: 'Start Free Trial',
    buttonVariant: 'contained' as const,
    highlight: true,
  },
  {
    title: 'Enterprise',
    price: '$49',
    period: '/month',
    features: [
        'Everything in Pro',
        'Team Management',
        'Custom Question Sets',
        'API Access',
        'Priority Support',
    ],
    buttonText: 'Contact Sales',
    buttonVariant: 'outlined' as const,
    highlight: false,
  },
];

const Pricing: React.FC = () => {
  const theme = useTheme();
  const navigate = useNavigate();

  return (
    <Box sx={{ py: 10, bgcolor: 'background.paper' }}>
      <Container maxWidth="lg">
        <Box sx={{ textAlign: 'center', mb: 8 }}>
            <Typography variant="h3" fontWeight={700} gutterBottom>
                Simple, Transparent Pricing
            </Typography>
            <Typography variant="h6" color="text.secondary">
                Invest in your career for less than the cost of a lunch.
            </Typography>
        </Box>

        <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', alignItems: 'stretch', mx: -2 }}>
            {pricingPlans.map((plan, index) => (
                <Box
                    key={index}
                    sx={{
                        p: 2,
                        width: { xs: '100%', md: '33.33%' },
                        display: 'flex',
                    }}
                >
                    <Paper
                        elevation={plan.highlight ? 8 : 1}
                        sx={{
                            p: 4,
                            width: '100%',
                            display: 'flex',
                            flexDirection: 'column',
                            borderRadius: 4,
                            border: plan.highlight ? `2px solid ${theme.palette.primary.main}` : `1px solid ${theme.palette.divider}`,
                            position: 'relative',
                            transition: 'transform 0.2s',
                            '&:hover': {
                                transform: 'translateY(-4px)',
                            },
                        }}
                    >
                        {plan.highlight && (
                            <Chip
                                icon={<StarIcon sx={{ fontSize: 16 }} />}
                                label="Most Popular"
                                color="primary"
                                size="small"
                                sx={{ position: 'absolute', top: -16, left: '50%', transform: 'translateX(-50%)', px: 1 }}
                            />
                        )}

                        <Typography variant="h5" fontWeight={700} gutterBottom align="center">
                            {plan.title}
                        </Typography>
                        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'baseline', mb: 4 }}>
                            <Typography variant="h3" fontWeight={800}>
                                {plan.price}
                            </Typography>
                            <Typography variant="subtitle1" color="text.secondary">
                                {plan.period}
                            </Typography>
                        </Box>

                        <List sx={{ mb: 4, flex: 1 }}>
                            {plan.features.map((feature, idx) => (
                                <ListItem key={idx} disableGutters sx={{ py: 1 }}>
                                    <ListItemIcon sx={{ minWidth: 36 }}>
                                        <CheckCircleIcon color="primary" fontSize="small" />
                                    </ListItemIcon>
                                    <ListItemText primary={feature} secondaryTypographyProps={{ variant: 'body2' }} />
                                </ListItem>
                            ))}
                        </List>

                        <Button
                            variant={plan.buttonVariant}
                            fullWidth
                            size="large"
                            onClick={() => navigate('/register')}
                            sx={{
                                py: 1.5,
                                borderRadius: 2,
                                fontWeight: 700,
                            }}
                        >
                            {plan.buttonText}
                        </Button>
                    </Paper>
                </Box>
            ))}
        </Box>
      </Container>
    </Box>
  );
};

export default Pricing;
