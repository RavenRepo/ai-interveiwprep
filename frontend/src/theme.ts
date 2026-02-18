import { createTheme, ThemeOptions, PaletteMode } from '@mui/material';

// ============================================================
// Palette definitions
// ============================================================

const lightPalette: ThemeOptions['palette'] = {
    mode: 'light' as PaletteMode,
    primary: {
        main: '#1976d2',
        light: '#42a5f5',
        dark: '#1565c0',
        contrastText: '#ffffff',
    },
    secondary: {
        main: '#dc004e',
        light: '#ff5983',
        dark: '#9a0036',
        contrastText: '#ffffff',
    },
    success: {
        main: '#2e7d32',
        light: '#4caf50',
        dark: '#1b5e20',
    },
    warning: {
        main: '#ed6c02',
        light: '#ff9800',
        dark: '#e65100',
    },
    error: {
        main: '#d32f2f',
        light: '#ef5350',
        dark: '#c62828',
    },
    info: {
        main: '#0288d1',
        light: '#03a9f4',
        dark: '#01579b',
    },
    background: {
        default: '#f5f7fa',
        paper: '#ffffff',
    },
    text: {
        primary: '#1a2027',
        secondary: '#5f6368',
    },
    divider: 'rgba(0, 0, 0, 0.08)',
};

const darkPalette: ThemeOptions['palette'] = {
    mode: 'dark' as PaletteMode,
    primary: {
        main: '#42a5f5',
        light: '#80d6ff',
        dark: '#0077c2',
        contrastText: '#000000',
    },
    secondary: {
        main: '#ff5983',
        light: '#ff8cb3',
        dark: '#c41056',
        contrastText: '#000000',
    },
    success: {
        main: '#66bb6a',
        light: '#81c784',
        dark: '#388e3c',
    },
    warning: {
        main: '#ffa726',
        light: '#ffb74d',
        dark: '#f57c00',
    },
    error: {
        main: '#ef5350',
        light: '#e57373',
        dark: '#d32f2f',
    },
    info: {
        main: '#29b6f6',
        light: '#4fc3f7',
        dark: '#0288d1',
    },
    background: {
        default: '#121212',
        paper: '#1e1e1e',
    },
    text: {
        primary: '#e0e0e0',
        secondary: '#aaaaaa',
    },
    divider: 'rgba(255, 255, 255, 0.08)',
};

// ============================================================
// Typography
// ============================================================

const typography: ThemeOptions['typography'] = {
    fontFamily: '"Roboto", "Arial", sans-serif',
    h1: { fontSize: '2.5rem', fontWeight: 700, lineHeight: 1.2 },
    h2: { fontSize: '2rem', fontWeight: 700, lineHeight: 1.3 },
    h3: { fontSize: '1.75rem', fontWeight: 700, lineHeight: 1.3 },
    h4: { fontSize: '1.5rem', fontWeight: 600, lineHeight: 1.35 },
    h5: { fontSize: '1.25rem', fontWeight: 600, lineHeight: 1.4 },
    h6: { fontSize: '1rem', fontWeight: 600, lineHeight: 1.5 },
    subtitle1: { fontSize: '1rem', fontWeight: 500, lineHeight: 1.5 },
    subtitle2: { fontSize: '0.875rem', fontWeight: 500, lineHeight: 1.5 },
    body1: { fontSize: '1rem', lineHeight: 1.6 },
    body2: { fontSize: '0.875rem', lineHeight: 1.6 },
    caption: { fontSize: '0.75rem', lineHeight: 1.5 },
    button: { fontSize: '0.875rem', fontWeight: 600, textTransform: 'none' as const },
};

// ============================================================
// Breakpoints
// ============================================================

const breakpoints: ThemeOptions['breakpoints'] = {
    values: {
        xs: 0,
        sm: 600,
        md: 900,
        lg: 1200,
        xl: 1536,
    },
};

// ============================================================
// Component overrides
// ============================================================

const components: ThemeOptions['components'] = {
    MuiButton: {
        styleOverrides: {
            root: {
                textTransform: 'none',
                borderRadius: 8,
                padding: '10px 24px',
                fontWeight: 600,
                boxShadow: 'none',
                '&:hover': { boxShadow: 'none' },
            },
            sizeSmall: {
                padding: '6px 16px',
                fontSize: '0.8125rem',
            },
            sizeLarge: {
                padding: '12px 32px',
                fontSize: '1rem',
            },
            contained: {
                '&:hover': { boxShadow: '0 2px 8px rgba(25, 118, 210, 0.3)' },
            },
        },
        defaultProps: {
            disableElevation: true,
        },
    },
    MuiCard: {
        styleOverrides: {
            root: {
                borderRadius: 16,
                boxShadow: '0 2px 12px rgba(0, 0, 0, 0.08)',
            },
        },
    },
    MuiCardContent: {
        styleOverrides: {
            root: {
                padding: 20,
                '&:last-child': { paddingBottom: 20 },
            },
        },
    },
    MuiTextField: {
        styleOverrides: {
            root: {
                '& .MuiOutlinedInput-root': {
                    borderRadius: 8,
                },
            },
        },
        defaultProps: {
            variant: 'outlined',
            size: 'medium',
        },
    },
    MuiOutlinedInput: {
        styleOverrides: {
            root: {
                borderRadius: 8,
            },
        },
    },
    MuiChip: {
        styleOverrides: {
            root: {
                borderRadius: 8,
                fontWeight: 500,
            },
        },
    },
    MuiAlert: {
        styleOverrides: {
            root: {
                borderRadius: 10,
            },
        },
    },
    MuiDialog: {
        styleOverrides: {
            paper: {
                borderRadius: 16,
            },
        },
    },
    MuiLinearProgress: {
        styleOverrides: {
            root: {
                borderRadius: 4,
                height: 6,
            },
        },
    },
    MuiAppBar: {
        defaultProps: {
            elevation: 0,
        },
    },
};

// ============================================================
// Spacing
// ============================================================

const spacing = 8; // default 8px base

// ============================================================
// Shape
// ============================================================

const shape = { borderRadius: 12 };

// ============================================================
// Theme factory
// ============================================================

export const createAppTheme = (mode: PaletteMode = 'light') =>
    createTheme({
        palette: mode === 'light' ? lightPalette : darkPalette,
        typography,
        breakpoints,
        components,
        spacing,
        shape,
    });

// ============================================================
// Default export (light theme)
// ============================================================

const theme = createAppTheme('light');
export default theme;
