/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        ucal: { primary: '#003366', accent: '#c9a227' },
      },
    },
  },
  plugins: [],
}
