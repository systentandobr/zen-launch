#!/bin/bash

# Script 11 criado automaticamente
echo "Executando claude-ai11.sh"
cat << 'EOF' > android/app/src/main/res/values/colors.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Cores principais -->
    <color name="primary">#FF5C5C</color>
    <color name="primary_dark">#D84848</color>
    <color name="primary_light">#FFB3B3</color>
    <color name="accent">#505BDA</color>
    
    <!-- Cores para tema claro -->
    <color name="background_light">#F9F9F9</color>
    <color name="card_background_light">#FFFFFF</color>
    <color name="text_primary_light">#121212</color>
    <color name="text_secondary_light">#757575</color>
    <color name="divider_light">#E0E0E0</color>
    
    <!-- Cores para tema escuro -->
    <color name="background_dark">#121212</color>
    <color name="card_background_dark">#1E1E1E</color>
    <color name="text_primary_dark">#FFFFFF</color>
    <color name="text_secondary_dark">#B0B0B0</color>
    <color name="divider_dark">#2C2C2C</color>
    
    <!-- Estado e feedback -->
    <color name="success">#34C759</color>
    <color name="error">#FF3B30</color>
    <color name="warning">#FF9500</color>
    <color name="info">#5AC8FA</color>
</resources>
EOF