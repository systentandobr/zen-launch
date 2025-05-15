@echo off
REM Limpa profundamente o projeto para resolver problemas de compilação com Hilt

echo Fechando o Android Studio caso esteja aberto...
taskkill /F /IM studio64.exe 2>nul

echo Removendo diretórios de build...
rmdir /S /Q build 2>nul
rmdir /S /Q app\build 2>nul
rmdir /S /Q .gradle 2>nul
rmdir /S /Q app\.gradle 2>nul

echo Removendo arquivos de cache do Hilt...
rmdir /S /Q app\build\generated\hilt 2>nul
rmdir /S /Q app\build\tmp\kapt3\stubs 2>nul

echo Limpando cache do Gradle...
del /S /Q .gradle\*.lock 2>nul

echo Executando limpeza do Gradle...
call gradlew clean

echo Invalidando cache do Android Studio...
echo "Agora por favor, abra o Android Studio e vá em:"
echo "File > Invalidate Caches / Restart..."
echo "E selecione 'Invalidate and Restart'"

echo Pronto! Agora você pode tentar compilar o projeto novamente.
