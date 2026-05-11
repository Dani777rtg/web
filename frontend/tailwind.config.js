/** @type {import('tailwindcss').Config} */
export default {
  // Incluir CSS para que @apply en index.css resuelva bien las clases del tema.
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx,css}'],
  theme: {
    extend: {
      colors: {
        // Azul institucional más oscuro y mayor contraste (fondo / texto / bordes).
        ucal: {
          primary: '#001a36',
          'primary-hover': '#001226',
          accent: '#0a6cbc',
          'accent-soft': '#2580c8',
          muted: '#2c3d4d',
        },
        paper: '#dce6f0',
        'paper-card': '#eef3f9',
      },
      fontFamily: {
        display: ['"Literata"', 'Georgia', 'serif'],
        sans: ['"Lexend"', 'system-ui', 'sans-serif'],
      },
      fontSize: {
        '2xs': ['0.6875rem', { lineHeight: '1rem' }],
      },
      boxShadow: {
        line: 'inset 0 -1px 0 0 rgba(0, 26, 54, 0.22)',
      },
    },
  },
  plugins: [],
}
