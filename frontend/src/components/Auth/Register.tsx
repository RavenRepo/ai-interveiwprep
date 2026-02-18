import React, { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    Checkbox,
    CircularProgress,
    Container,
    FormControlLabel,
    IconButton,
    InputAdornment,
    LinearProgress,
    Link,
    TextField,
    Typography,
} from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import { useAuth } from '../../hooks/useAuth';

// ============================================================
// Form types
// ============================================================

interface RegisterFormData {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
    acceptTerms: boolean;
}

// ============================================================
// Password strength helper
// ============================================================

type StrengthLevel = 'Weak' | 'Medium' | 'Strong';

const getPasswordStrength = (password: string): { level: StrengthLevel; score: number; color: string } => {
    let score = 0;
    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/[0-9]/.test(password)) score++;
    if (/[^A-Za-z0-9]/.test(password)) score++;

    if (score <= 1) return { level: 'Weak', score: 25, color: '#f44336' };
    if (score <= 2) return { level: 'Medium', score: 50, color: '#ff9800' };
    if (score <= 3) return { level: 'Medium', score: 75, color: '#ff9800' };
    return { level: 'Strong', score: 100, color: '#4caf50' };
};

// ============================================================
// Register Component
// ============================================================

const Register: React.FC = () => {
    const navigate = useNavigate();
    const { register: registerUser } = useAuth();

    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
    } = useForm<RegisterFormData>({
        defaultValues: {
            firstName: '',
            lastName: '',
            email: '',
            password: '',
            confirmPassword: '',
            acceptTerms: false,
        },
    });

    const watchPassword = watch('password', '');
    const strength = watchPassword ? getPasswordStrength(watchPassword) : null;

    // ============================================================
    // Submit handler
    // ============================================================

    const onSubmit = async (data: RegisterFormData) => {
        setIsLoading(true);
        setError(null);

        // Validation
        if (!data.firstName.trim() || !data.lastName.trim()) {
            setError('First name and last name are required');
            setIsLoading(false);
            return;
        }

        if (data.password.length < 8) {
            setError('Password must be at least 8 characters');
            setIsLoading(false);
            return;
        }

        if (data.password !== data.confirmPassword) {
            setError('Passwords do not match');
            setIsLoading(false);
            return;
        }

        try {
            await registerUser({
                name: `${data.firstName.trim()} ${data.lastName.trim()}`,
                email: data.email.trim(),
                password: data.password,
            });
            navigate('/dashboard');
        } catch (err: any) {
            if (err.status === 409) {
                setError('An account with this email already exists.');
            } else if (err.status === 0) {
                setError('Network error. Please check your internet connection.');
            } else {
                setError(err.response?.data?.message || err.message || 'Registration failed. Please try again.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    // ============================================================
    // Render
    // ============================================================

    return (
        <Container maxWidth="xs" sx={{ py: 6 }}>
            <Box sx={{ textAlign: 'center', mb: 4 }}>
                <Typography variant="h4" fontWeight={700} gutterBottom>
                    Create Account
                </Typography>
                <Typography color="text.secondary">
                    Start practicing for your next interview
                </Typography>
            </Box>

            <Card elevation={0} sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider' }}>
                <CardContent sx={{ p: 3 }}>
                    {/* Error Alert */}
                    {error && (
                        <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}

                    <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
                        {/* Name fields side by side */}
                        <Box sx={{ display: 'flex', gap: 1.5 }}>
                            <TextField
                                fullWidth
                                label="First Name"
                                autoComplete="given-name"
                                autoFocus
                                margin="normal"
                                error={!!errors.firstName}
                                helperText={errors.firstName?.message}
                                {...register('firstName', {
                                    required: 'First name is required',
                                })}
                            />
                            <TextField
                                fullWidth
                                label="Last Name"
                                autoComplete="family-name"
                                margin="normal"
                                error={!!errors.lastName}
                                helperText={errors.lastName?.message}
                                {...register('lastName', {
                                    required: 'Last name is required',
                                })}
                            />
                        </Box>

                        {/* Email */}
                        <TextField
                            fullWidth
                            label="Email Address"
                            type="email"
                            autoComplete="email"
                            margin="normal"
                            error={!!errors.email}
                            helperText={errors.email?.message}
                            {...register('email', {
                                required: 'Email is required',
                                pattern: {
                                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                                    message: 'Enter a valid email address',
                                },
                            })}
                        />

                        {/* Password */}
                        <TextField
                            fullWidth
                            label="Password"
                            type={showPassword ? 'text' : 'password'}
                            autoComplete="new-password"
                            margin="normal"
                            error={!!errors.password}
                            helperText={errors.password?.message}
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton
                                            onClick={() => setShowPassword(!showPassword)}
                                            edge="end"
                                            size="small"
                                        >
                                            {showPassword ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }}
                            {...register('password', {
                                required: 'Password is required',
                                minLength: { value: 8, message: 'At least 8 characters' },
                                validate: {
                                    hasUppercase: (v) => /[A-Z]/.test(v) || 'Must contain an uppercase letter',
                                    hasNumber: (v) => /[0-9]/.test(v) || 'Must contain a number',
                                    hasSpecial: (v) => /[^A-Za-z0-9]/.test(v) || 'Must contain a special character',
                                },
                            })}
                        />

                        {/* Password strength indicator */}
                        {strength && (
                            <Box sx={{ mt: 0.5, mb: 1 }}>
                                <LinearProgress
                                    variant="determinate"
                                    value={strength.score}
                                    sx={{
                                        height: 4,
                                        borderRadius: 2,
                                        bgcolor: 'grey.200',
                                        '& .MuiLinearProgress-bar': { bgcolor: strength.color },
                                    }}
                                />
                                <Typography variant="caption" sx={{ color: strength.color, fontWeight: 600 }}>
                                    {strength.level}
                                </Typography>
                            </Box>
                        )}

                        {/* Confirm Password */}
                        <TextField
                            fullWidth
                            label="Confirm Password"
                            type={showConfirm ? 'text' : 'password'}
                            autoComplete="new-password"
                            margin="normal"
                            error={!!errors.confirmPassword}
                            helperText={errors.confirmPassword?.message}
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton
                                            onClick={() => setShowConfirm(!showConfirm)}
                                            edge="end"
                                            size="small"
                                        >
                                            {showConfirm ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }}
                            {...register('confirmPassword', {
                                required: 'Please confirm your password',
                                validate: (value) => value === watchPassword || 'Passwords do not match',
                            })}
                        />

                        {/* Terms */}
                        <FormControlLabel
                            control={
                                <Checkbox
                                    size="small"
                                    {...register('acceptTerms', {
                                        required: 'You must accept the terms and conditions',
                                    })}
                                />
                            }
                            label={
                                <Typography variant="body2">
                                    I agree to the{' '}
                                    <Link href="#" underline="hover">
                                        Terms and Conditions
                                    </Link>
                                </Typography>
                            }
                            sx={{ mt: 1 }}
                        />
                        {errors.acceptTerms && (
                            <Typography variant="caption" color="error" sx={{ display: 'block', ml: 2 }}>
                                {errors.acceptTerms.message}
                            </Typography>
                        )}

                        {/* Submit */}
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            size="large"
                            disabled={isLoading}
                            sx={{ mt: 2, py: 1.4 }}
                        >
                            {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Create Account'}
                        </Button>
                    </Box>

                    {/* Login link */}
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 3, textAlign: 'center' }}>
                        Already have an account?{' '}
                        <Link component={RouterLink} to="/login" underline="hover" fontWeight={600}>
                            Sign In
                        </Link>
                    </Typography>
                </CardContent>
            </Card>
        </Container>
    );
};

export default Register;
