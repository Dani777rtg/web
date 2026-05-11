/** @type {import('tailwindcss').Config} */
export default {
  // Incluir CSS para que @apply en index.css resuelva bien las clases del tema.
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx,css}'],
  theme: {
    extend: {
      colors: {
        // Paleta inspirada en identidad institucional (azul académico, acentos fríos).
        ucal: {
          primary: '#003b7a',
          'primary-hover': '#002d5e',
          accent: '#1a6fa8',
          'accent-soft': '#3d8cc9',
          muted: '#4a5f73',
        },
        paper: '#f2f6fb',
        'paper-card': '#fbfcfe',
      },
      fontFamily: {
        display: ['"Literata"', 'Georgia', 'serif'],
        sans: ['"Lexend"', 'system-ui', 'sans-serif'],
      },
      fontSize: {
        '2xs': ['0.6875rem', { lineHeight: '1rem' }],
      },
      boxShadow: {
        line: 'inset 0 -1px 0 0 rgba(0, 59, 122, 0.14)',
      },
    },
  },
  plugins: [],
}
