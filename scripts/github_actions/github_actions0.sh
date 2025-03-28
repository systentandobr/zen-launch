#!/bin/bash

# Script 0 criado automaticamente
echo "Executando claude-ai0.sh"
cat << 'EOF' > .github/workflows/android-release.yml
name: Android Release

on:
  push:
    branches: [ main ]
    tags:
      - 'v*'
  # Permite execução manual através da interface do GitHub
  workflow_dispatch:
    inputs:
      lane:
        description: 'Fastlane lane to use (beta, production)'
        required: true
        default: 'beta'
        type: choice
        options:
          - beta
          - production

jobs:
  android-build-and-release:
    name: Android Build & Release
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
        with:
          fetch-depth: 0

      - name: Setup Ruby
        uses: ruby/setup-ruby@v1
        with:
          ruby-version: '3.0'
          bundler-cache: true

      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          distribution: 'zulu'
          java-version: '11'

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'yarn'

      - name: Install dependencies
        run: |
          yarn install --frozen-lockfile
          gem install bundler
          bundle install

      - name: Setup Android SDK
        uses: android-actions/setup-android@v2

      - name: Cache Gradle
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      - name: Decode Android keystore
        id: decode_keystore
        uses: timheuer/base64-to-file@v1
        with:
          fileName: 'release.keystore'
          encodedString: ${{ secrets.KEYSTORE_BASE64 }}

      - name: Create keystore.properties
        run: |
          echo "storeFile=${{ steps.decode_keystore.outputs.filePath }}" > android/keystore.properties
          echo "storePassword=${{ secrets.KEYSTORE_PASSWORD }}" >> android/keystore.properties
          echo "keyAlias=${{ secrets.KEY_ALIAS }}" >> android/keystore.properties
          echo "keyPassword=${{ secrets.KEY_PASSWORD }}" >> android/keystore.properties

      - name: Create Google Play Config
        run: |
          echo '${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT_JSON }}' > android/fastlane/google-play-service-account.json

      - name: Increment version
        id: versioning
        run: |
          if [[ "${{ github.ref }}" == refs/tags/v* ]]; then
            VERSION=${GITHUB_REF#refs/tags/v}
            echo "Version from tag: $VERSION"
            # Split version into parts
            IFS='.' read -ra VERSION_PARTS <<< "$VERSION"
            MAJOR=${VERSION_PARTS[0]}
            MINOR=${VERSION_PARTS[1]}
            PATCH=${VERSION_PARTS[2]}
            
            # Calculate version code (10000*MAJOR + 100*MINOR + PATCH)
            VERSION_CODE=$((10000*MAJOR + 100*MINOR + PATCH))
            
            echo "VERSION_NAME=$VERSION" >> $GITHUB_ENV
            echo "VERSION_CODE=$VERSION_CODE" >> $GITHUB_ENV
          else
            # Default versioning if not using tags
            CURRENT_DATE=$(date +'%Y%m%d')
            echo "VERSION_NAME=1.0.0-$CURRENT_DATE" >> $GITHUB_ENV
            echo "VERSION_CODE=$(date +'%Y%m%d')" >> $GITHUB_ENV
          fi

      - name: Update version in build.gradle
        run: |
          sed -i "s/versionCode [0-9]*/versionCode ${{ env.VERSION_CODE }}/g" android/app/build.gradle
          sed -i "s/versionName \"[^\"]*\"/versionName \"${{ env.VERSION_NAME }}\"/g" android/app/build.gradle

      - name: Build AAB
        run: |
          cd android
          ./gradlew bundleRelease --no-daemon

      - name: Upload AAB Artifact
        uses: actions/upload-artifact@v3
        with:
          name: app-release
          path: android/app/build/outputs/bundle/release/app-release.aab

      - name: Deploy to Play Store
        run: |
          cd android
          LANE="${{ github.event.inputs.lane || 'beta' }}"
          bundle exec fastlane $LANE

      - name: Create GitHub Release
        if: startsWith(github.ref, 'refs/tags/v')
        uses: softprops/action-gh-release@v1
        with:
          files: |
            android/app/build/outputs/bundle/release/app-release.aab
          draft: false
          prerelease: ${{ contains(github.ref, '-rc') || contains(github.ref, '-beta') || contains(github.ref, '-alpha') }}
          body: |
            ## Zen Launcher ${{ env.VERSION_NAME }}
            
            Release automaticamente gerado pelo GitHub Actions.
            
            ### Alterações
            
            Veja o histórico de commits para detalhes sobre as alterações.
EOF