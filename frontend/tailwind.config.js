/** @type {import('tailwindcss').Config} */
export default {
  // Incluir CSS para que @apply en index.css resuelva bien las clases del tema.
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx,css}'],
  theme: {
    extend: {
      colors: {
        ucal: {
          primary: '#0c2d48',
          accent: '#9a7b0f',
          muted: '#5c6670',
        },
        // Nombres planos: con objetos anidados + DEFAULT, @apply bg-paper a veces falla en PostCSS.
        paper: '#f3f0ea',
        'paper-card': '#faf8f4',
      },
      fontFamily: {
        display: ['"Literata"', 'Georgia', 'serif'],
        sans: ['"Lexend"', 'system-ui', 'sans-serif'],
      },
      fontSize: {
        '2xs': ['0.6875rem', { lineHeight: '1rem' }],
      },
      boxShadow: {
        line: 'inset 0 -1px 0 0 rgba(12, 45, 72, 0.12)',
      },
    },
  },
  plugins: [],
}
