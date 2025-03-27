#!/bin/bash

# Script 3 criado automaticamente
echo "Executando claude-ai3.sh"
cat << 'EOF' > android/app/build.gradle
/**
 * Arquivo de configuração Gradle do aplicativo Zen Launcher
 */

def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
def keystorePropertiesExist = keystorePropertiesFile.exists()
if (keystorePropertiesExist) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

apply plugin: "com.android.application"
apply plugin: "com.facebook.react"

/**
 * Essa função gera unique BuildIDs para as variants
 * Isso é importante para aplicativos nativos para evitar conflitos de instalação
 */
def generateBuildID = { def id ->
    return String.format("\"%s\"", id.replaceAll("\"", "\\\\\""))
}

// Obtém a versão do pacote do Node
def packageJSON = new groovy.json.JsonSlurper().parseText(file('../../package.json').text)
def appVersion = packageJSON.version

import com.android.build.OutputFile

/**
 * Este é o arquivo principal de configuração para o aplicativo React Native Android.
 *
 * A configuração pode ser editada manualmente ou usando as ferramentas do Android Studio.
 */
android {
    namespace "com.zenlauncher"
    compileSdkVersion rootProject.ext.compileSdkVersion

    defaultConfig {
        applicationId "com.zenlauncher"
        minSdkVersion rootProject.ext.minSdkVersion
        targetSdkVersion rootProject.ext.targetSdkVersion
        versionCode 10000  // Será substituído pelo workflow do GitHub Actions
        versionName appVersion  // Usa a versão do package.json
        buildConfigField "String", "BUILD_ID", generateBuildID(System.getenv("BUILD_ID") ?: UUID.randomUUID().toString())
    }

    signingConfigs {
        debug {
            storeFile file('debug.keystore')
            storePassword 'android'
            keyAlias 'androiddebugkey'
            keyPassword 'android'
        }
        release {
            if (keystorePropertiesExist) {
                storeFile file(keystoreProperties['storeFile'])
                storePassword keystoreProperties['storePassword']
                keyAlias keystoreProperties['keyAlias']
                keyPassword keystoreProperties['keyPassword']
            }
        }
    }

    buildTypes {
        debug {
            signingConfig signingConfigs.debug
        }
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    // Configurações para habilitar o Launcher como opção para o usuário
    android.applicationVariants.all { variant ->
        variant.outputs.each { output ->
            // Para cada variante do aplicativo (release, debug, etc.)
            def versionCodes = ["armeabi-v7a": 1, "x86": 2, "arm64-v8a": 3, "x86_64": 4]
            def abi = output.getFilter(OutputFile.ABI)
            if (abi != null) {  // Null para o caso de um aplicativo universal sem filtro ABI
                output.versionCodeOverride = defaultConfig.versionCode * 1000 + versionCodes.get(abi)
            }
        }
    }
}

dependencies {
    // Dependências padrão do React Native
    implementation "com.facebook.react:react-android"
    implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"

    // Dependências para funcionalidades do Launcher
    implementation "androidx.core:core:1.9.0"
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "androidx.preference:preference:1.2.1"

    // Para acesso a estatísticas de uso e notificações
    implementation "androidx.work:work-runtime:2.8.1"
    implementation "androidx.browser:browser:1.5.0"

    debugImplementation("com.facebook.flipper:flipper:${FLIPPER_VERSION}")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:${FLIPPER_VERSION}") {
        exclude group:'com.squareup.okhttp3', module:'okhttp'
    }

    debugImplementation("com.facebook.flipper:flipper-fresco-plugin:${FLIPPER_VERSION}")
    if (hermesEnabled.toBoolean()) {
        implementation("com.facebook.react:hermes-android")
    } else {
        implementation jscFlavor
    }
}

apply from: file("../../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesAppBuildGradle(project)
EOF