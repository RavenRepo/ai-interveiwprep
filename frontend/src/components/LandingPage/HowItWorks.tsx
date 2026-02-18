
import React from 'react';
import { Box, Container, Typography, Step, StepLabel, Stepper, useTheme, useMediaQuery } from '@mui/material';

const steps = [
  {
    label: 'Select Your Role',
    description: 'Choose from hundreds of job titles or customize your own interview scenario.',
  },
  {
    label: 'Record Your Answer',
    description: 'Respond to AI-generated questions in real-time using your webcam and microphone.',
  },
  {
    label: 'Get AI Analysis',
    description: 'Review your performance with instant feedback on content, tone, and body language.',
  },
];

const HowItWorks: React.FC = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  return (
    <Box sx={{ py: 10, bgcolor: 'background.default' }}>
      <Container maxWidth="lg">
        <Typography
            variant="h3"
            align="center"
            gutterBottom
            sx={{ fontWeight: 700, mb: 2 }}
        >
            How It Works
        </Typography>
        <Typography
            variant="h6"
            align="center"
            color="text.secondary"
            sx={{ mb: 8, maxWidth: 600, mx: 'auto' }}
        >
            Three simple steps to interview mastery.
        </Typography>

        <Box sx={{ width: '100%' }}>
            {/* Desktop View: Horizontal Stepper */}
            {!isMobile && (
                <Stepper alternativeLabel activeStep={-1}>
                    {steps.map((step) => (
                        <Step key={step.label} expanded>
                            <StepLabel
                                StepIconProps={{
                                    sx: { fontSize: '2rem' }
                                }}
                            >
                                <Typography variant="h6" sx={{ mt: 1 }}>{step.label}</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ maxWidth: 250, mx: 'auto', mt: 1 }}>
                                    {step.description}
                                </Typography>
                            </StepLabel>
                        </Step>
                    ))}
                </Stepper>
            )}

            {/* Mobile View: Vertical Box Layout */}
            {isMobile && (
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                    {steps.map((step, index) => (
                        <Box key={step.label} sx={{ textAlign: 'center', p: 2 }}>
                            <Box
                                sx={{
                                    width: 40,
                                    height: 40,
                                    borderRadius: '50%',
                                    bgcolor: 'primary.main',
                                    color: 'white',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    fontSize: '1.2rem',
                                    fontWeight: 'bold',
                                    mx: 'auto',
                                    mb: 2,
                                }}
                            >
                                {index + 1}
                            </Box>
                            <Typography variant="h6" gutterBottom>{step.label}</Typography>
                            <Typography variant="body2" color="text.secondary">
                                {step.description}
                            </Typography>
                        </Box>
                    ))}
                </Box>
            )}
        </Box>
      </Container>
    </Box>
  );
};

export default HowItWorks;
