/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./App.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#FF5C5C",
        secondary: "#505BDA",
        background: {
          light: "#F9F9F9",
          dark: "#121212"
        },
        card: {
          light: "#FFFFFF",
          dark: "#1E1E1E"
        },
        text: {
          light: "#121212",
          dark: "#FFFFFF",
          secondary: {
            light: "#757575",
            dark: "#B0B0B0"
          }
        }
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif']
      }
    },
  },
  plugins: [],
}
