#!/bin/bash

# Script 10 criado automaticamente
echo "Executando claude-ai10.sh"
cat << 'EOF' > android/app/src/main/res/values/strings.xml
<resources>
    <string name="app_name">Zen Launcher</string>
    
    <!-- Strings gerais -->
    <string name="welcome">Bem-vindo ao Zen Launcher</string>
    <string name="set_as_default">Definir como launcher padrão</string>
    <string name="settings">Configurações</string>
    
    <!-- Permissions -->
    <string name="permission_required">Permissão necessária</string>
    <string name="usage_stats_permission">Para rastrear o uso de aplicativos, o Zen Launcher precisa acessar as estatísticas de uso.</string>
    <string name="notification_permission">Para mostrar notificações, o Zen Launcher precisa de acesso às suas notificações.</string>
    <string name="location_permission">Para mostrar a previsão do tempo, o Zen Launcher precisa acessar sua localização.</string>
    
    <!-- Focus Mode -->
    <string name="focus_mode">Modo Foco</string>
    <string name="focus_mode_enabled">Modo Foco ativado</string>
    <string name="focus_mode_disabled">Modo Foco desativado</string>
    <string name="focus_time_remaining">Tempo restante</string>
    <string name="focus_app_restricted">Este aplicativo está restrito durante o Modo Foco</string>
    
    <!-- App Drawer -->
    <string name="apps">Aplicativos</string>
    <string name="search_apps">Pesquisar aplicativos</string>
    <string name="no_apps_found">Nenhum aplicativo encontrado</string>
    <string name="app_info">Informações do aplicativo</string>
    <string name="uninstall">Desinstalar</string>
    
    <!-- Tasks -->
    <string name="tasks">Tarefas</string>
    <string name="add_task">Adicionar tarefa</string>
    <string name="task_completed">Tarefa concluída</string>
    <string name="no_tasks">Nenhuma tarefa</string>
    
    <!-- Weather -->
    <string name="weather">Clima</string>
    <string name="temperature">Temperatura</string>
    <string name="chance_of_rain">Chance de chuva</string>
    
    <!-- Settings -->
    <string name="theme">Tema</string>
    <string name="theme_light">Claro</string>
    <string name="theme_dark">Escuro</string>
    <string name="theme_system">Sistema</string>
    <string name="clock_format">Formato de hora</string>
    <string name="show_seconds">Mostrar segundos</string>
    <string name="show_weather">Mostrar clima</string>
    <string name="app_orientation">Orientação do aplicativo</string>
    <string name="portrait">Retrato</string>
    <string name="landscape">Paisagem</string>
    <string name="auto">Automático</string>
    <string name="reset_settings">Redefinir configurações</string>
    <string name="reset_confirm">Tem certeza de que deseja redefinir todas as configurações?</string>
    <string name="about">Sobre</string>
    <string name="version">Versão</string>
    <string name="feedback">Enviar feedback</string>
    
    <!-- Lock Screen -->
    <string name="lock_screen">Tela de bloqueio</string>
    <string name="swipe_to_unlock">Deslize para cima para desbloquear</string>
    <string name="enable_lock_screen">Habilitar tela de bloqueio</string>
    <string name="lock_timeout">Tempo para bloqueio</string>
</resources>
EOF