@echo off
echo ===================================================
echo      CRIANDO BUILD DE PRODUCAO DO ZENLAUNCHER
echo ===================================================
echo.

rem Sempre recriar o keystore para garantir consistencia
echo Criando keystore para assinatura...
echo call create_keystore.bat
echo.

echo Limpando builds anteriores...
call gradlew clean

echo.
echo Compilando aplicativo...
call gradlew assembleRelease --info

echo.
echo Gerando App Bundle para Google Play...
call gradlew bundleRelease --info

echo.
echo ===================================================
echo Build concluido!
echo.
echo Os arquivos foram gerados em:
echo APK: app\build\outputs\apk\release\app-release.apk
echo Bundle: app\build\outputs\bundle\release\app-release.aab
echo ===================================================
