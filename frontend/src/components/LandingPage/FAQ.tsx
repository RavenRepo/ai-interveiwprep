
import React from 'react';
import { Box, Container, Typography, Accordion, AccordionSummary, AccordionDetails, useTheme } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const faqs = [
  {
    question: "How accurate is the AI feedback?",
    answer: "Our AI model is trained on thousands of successful interview responses and follows industry-standard behavioral interview guidelines. It provides analysis on keywords, tone, pace, and clarity with high accuracy."
  },
  {
    question: "Can I use this for technical interviews?",
    answer: "Yes! We have specific modules for software engineering, data science, and product management that include technical questions. The AI evaluates the structure and content of your technical explanations."
  },
  {
    question: "is my video data private?",
    answer: "Absolutely. Your privacy is our top priority. Video recordings are processed securely and are only accessible by you. We do not use your personal interview data to train our public models without your explicit consent."
  },
  {
    question: "Do you offer a free trial?",
    answer: "Yes, you can try our Free plan which includes 5 AI mock interview sessions and basic performance analysis. No credit card required to start."
  },
  {
    question: "Can I cancel my subscription anytime?",
    answer: "Yes, you can cancel your subscription at any time from your account settings. You will continue to have access until the end of your billing cycle."
  },
];

const FAQ: React.FC = () => {
    const theme = useTheme();

    return (
        <Box sx={{ py: 10, bgcolor: 'background.default' }}>
            <Container maxWidth="md">
                <Typography
                    variant="h3"
                    align="center"
                    gutterBottom
                    sx={{ fontWeight: 700, mb: 6 }}
                >
                    Frequently Asked Questions
                </Typography>

                <Box>
                    {faqs.map((faq, index) => (
                        <Accordion
                            key={index}
                            elevation={0}
                            sx={{
                                mb: 2,
                                border: `1px solid ${theme.palette.divider}`,
                                borderRadius: '12px !important',
                                '&:before': { display: 'none' },
                                overflow: 'hidden',
                            }}
                        >
                            <AccordionSummary
                                expandIcon={<ExpandMoreIcon />}
                                sx={{
                                    px: 3,
                                    '& .MuiAccordionSummary-content': { my: 2 },
                                }}
                            >
                                <Typography variant="h6" fontWeight={600}>
                                    {faq.question}
                                </Typography>
                            </AccordionSummary>
                            <AccordionDetails sx={{ px: 3, pb: 3 }}>
                                <Typography color="text.secondary" style={{ lineHeight: 1.7 }}>
                                    {faq.answer}
                                </Typography>
                            </AccordionDetails>
                        </Accordion>
                    ))}
                </Box>
            </Container>
        </Box>
    );
};

export default FAQ;
