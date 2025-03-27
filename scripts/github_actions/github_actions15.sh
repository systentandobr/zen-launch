#!/bin/bash

# Script 15 criado automaticamente
echo "Executando claude-ai15.sh"
cat << 'EOF' > package.json
{
  "name": "zen-launcher",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "android": "react-native run-android",
    "ios": "react-native run-ios",
    "lint": "eslint .",
    "start": "react-native start",
    "test": "jest",
    "clean": "cd android && ./gradlew clean && cd ..",
    "build:android": "cd android && ./gradlew assembleRelease && cd ..",
    "build:bundle": "cd android && ./gradlew bundleRelease && cd .."
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
    "react-native-permissions": "^3.9.0",
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
    "@types/jest": "^29.5.3",
    "@types/react": "^18.0.24",
    "@types/react-native": "^0.72.2",
    "@types/react-test-renderer": "^18.0.0",
    "babel-jest": "^29.2.1",
    "babel-plugin-module-resolver": "^5.0.0",
    "eslint": "^8.19.0",
    "jest": "^29.2.1",
    "metro-react-native-babel-preset": "0.76.8",
    "prettier": "^2.4.1",
    "react-test-renderer": "18.2.0",
    "typescript": "4.8.4"
  },
  "jest": {
    "preset": "react-native",
    "moduleFileExtensions": [
      "ts",
      "tsx",
      "js",
      "jsx",
      "json",
      "node"
    ]
  }
}
EOF