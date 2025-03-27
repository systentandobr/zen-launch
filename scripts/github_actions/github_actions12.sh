#!/bin/bash

# Script 12 criado automaticamente
echo "Executando claude-ai12.sh"
cat << 'EOF' > android/app/src/main/res/values/styles.xml
<resources>
    <!-- Tema Base para o App -->
    <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
        <item name="android:windowBackground">@color/background_light</item>
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>
        <item name="android:textColor">@color/text_primary_light</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:windowLightNavigationBar">true</item>
    </style>
    
    <!-- Tema Escuro -->
    <style name="AppTheme.Dark">
        <item name="android:windowBackground">@color/background_dark</item>
        <item name="android:textColor">@color/text_primary_dark</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:windowLightNavigationBar">false</item>
    </style>
    
    <!-- Estilo para a tela de bloqueio -->
    <style name="LockScreenTheme" parent="AppTheme">
        <item name="android:windowFullscreen">true</item>
        <item name="android:windowContentOverlay">@null</item>
    </style>
    
    <!-- Estilos para textos -->
    <style name="TextAppearance.App.Headline" parent="TextAppearance.AppCompat.Headline">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">32sp</item>
        <item name="android:letterSpacing">-0.01</item>
    </style>
    
    <style name="TextAppearance.App.Title" parent="TextAppearance.AppCompat.Title">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">24sp</item>
        <item name="android:letterSpacing">-0.01</item>
    </style>
    
    <style name="TextAppearance.App.Subheading" parent="TextAppearance.AppCompat.Subhead">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">18sp</item>
    </style>
    
    <style name="TextAppearance.App.Body" parent="TextAppearance.AppCompat.Body1">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">16sp</item>
    </style>
    
    <style name="TextAppearance.App.Caption" parent="TextAppearance.AppCompat.Caption">
        <item name="fontFamily">sans-serif</item>
        <item name="android:fontFamily">sans-serif</item>
        <item name="android:textSize">12sp</item>
        <item name="android:alpha">0.7</item>
    </style>
</resources>
EOF