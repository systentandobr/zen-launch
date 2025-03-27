#!/bin/bash

# Script 1 criado automaticamente
echo "Executando claude-ai1.sh"
cat << 'EOF' > android/fastlane/Fastfile
default_platform(:android)

platform :android do
  desc "Deploy a new beta version to the Google Play"
  lane :beta do
    gradle(
      task: "clean bundleRelease",
      project_dir: ".",
      properties: {
        "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
        "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
        "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
        "android.injected.signing.key.password" => ENV["KEY_PASSWORD"],
      }
    )
    upload_to_play_store(
      track: 'beta',
      json_key: 'fastlane/google-play-service-account.json',
      skip_upload_metadata: true,
      skip_upload_images: true,
      skip_upload_screenshots: true,
      release_status: 'draft',
      aab: '../app/build/outputs/bundle/release/app-release.aab'
    )
  end

  desc "Deploy a new production version to the Google Play"
  lane :production do
    gradle(
      task: "clean bundleRelease",
      project_dir: ".",
      properties: {
        "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
        "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
        "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
        "android.injected.signing.key.password" => ENV["KEY_PASSWORD"],
      }
    )
    upload_to_play_store(
      track: 'production',
      json_key: 'fastlane/google-play-service-account.json',
      skip_upload_metadata: false,
      skip_upload_images: false,
      skip_upload_screenshots: false,
      aab: '../app/build/outputs/bundle/release/app-release.aab'
    )
  end
end
EOF

cat << 'EOF' > android/fastlane/Appfile
json_key_file("fastlane/google-play-service-account.json")
package_name("com.zenlauncher")
EOF

mkdir -p .github/workflows