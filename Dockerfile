# Используем базовый образ с Java
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем jar файл в контейнер
COPY target/Helper5Word_bot-1.0.jar Helper5Word_bot-1.0.jar

# Открываем порт (если это необходимо)
EXPOSE 8085

# Указываем команду для запуска приложения
CMD ["java", "-jar", "Helper5Word_bot-1.0.jar"]

