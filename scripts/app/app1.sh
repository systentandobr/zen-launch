#!/bin/bash

# Script 1 criado automaticamente
echo "Executando claude-ai1.sh"
cat << 'EOF' > package.json
{
  "name": "zen-launcher",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "android": "react-native run-android",
    "ios": "react-native run-ios",
    "start": "react-native start",
    "test": "jest",
    "lint": "eslint . --ext .js,.jsx,.ts,.tsx"
  },
  "dependencies": {
    "@react-native-async-storage/async-storage": "^1.19.3",
    "@react-navigation/bottom-tabs": "^6.5.8",
    "@react-navigation/native": "^6.1.7",
    "@react-navigation/stack": "^6.3.17",
    "@reduxjs/toolkit": "^1.9.5",
    "date-fns": "^2.30.0",
    "nativewind": "^2.0.11",
    "react": "18.2.0",
    "react-native": "0.72.4",
    "react-native-gesture-handler": "^2.12.1",
    "react-native-reanimated": "^3.4.2",
    "react-native-safe-area-context": "^4.7.1",
    "react-native-screens": "^3.24.0",
    "react-native-svg": "^13.13.0",
    "react-native-vector-icons": "^10.0.0",
    "react-redux": "^8.1.2",
    "tailwindcss": "^3.3.2"
  },
  "devDependencies": {
    "@babel/core": "^7.20.0",
    "@babel/preset-env": "^7.20.0",
    "@babel/runtime": "^7.20.0",
    "@react-native/eslint-config": "^0.72.2",
    "@react-native/metro-config": "^0.72.11",
    "@tsconfig/react-native": "^3.0.0",
    "@types/react": "^18.0.24",
    "@types/react-test-renderer": "^18.0.0",
    "babel-jest": "^29.2.1",
    "eslint": "^8.19.0",
    "jest": "^29.2.1",
    "metro-react-native-babel-preset": "0.76.8",
    "prettier": "^2.4.1",
    "react-test-renderer": "18.2.0",
    "typescript": "4.8.4"
  }
}
EOF

cat << 'EOF' > tailwind.config.js
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
EOF

cat << 'EOF' > babel.config.js
module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [
    "nativewind/babel",
    [
      'module-resolver',
      {
        root: ['./src'],
        extensions: ['.ios.js', '.android.js', '.js', '.ts', '.tsx', '.json'],
        alias: {
          '@core': './src/core',
          '@modules': './src/modules',
          '@navigation': './src/navigation',
          '@state': './src/state',
          '@app': './src/app',
        },
      },
    ],
  ],
};
EOF

cat << 'EOF' > tsconfig.json
{
  "extends": "@tsconfig/react-native/tsconfig.json",
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@core/*": ["src/core/*"],
      "@modules/*": ["src/modules/*"],
      "@navigation/*": ["src/navigation/*"],
      "@state/*": ["src/state/*"],
      "@app/*": ["src/app/*"]
    },
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "strictPropertyInitialization": true,
    "noImplicitThis": true,
    "alwaysStrict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "forceConsistentCasingInFileNames": true
  }
}
EOF

mkdir -p src/app src/core/ui/theme src/core/ui/components src/modules/launcher/components src/modules/focus/components src/navigation src/state/slices
