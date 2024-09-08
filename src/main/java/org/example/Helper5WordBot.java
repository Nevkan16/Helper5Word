package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Helper5WordBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;

    // Карта для хранения состояний пользователей
    private final Map<Long, GuessWord> userGames;

    public Helper5WordBot() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                throw new RuntimeException("Cannot find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error loading properties", ex);
        }

        this.botUsername = properties.getProperty("bot.username");
        this.botToken = properties.getProperty("bot.token");
        userGames = new HashMap<>();
    }

    @Override
    public String getBotUsername() {
        return botUsername;  // Замените на имя вашего бота
    }

    @Override
    public String getBotToken() {
        return botToken;  // Замените на ваш токен API
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();  // Получаем имя пользователя
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Логируем имя пользователя и его сообщение
            System.out.println("Time: " + formatter.format(dateTime) + " User: " +
                    (username != null ? username : "Unknown") + " - Message: " + messageText);

            // Получаем текущее состояние игры для данного пользователя
            GuessWord guessWord = userGames.computeIfAbsent(chatId, id ->
                    new GuessWord("src/main/resources/5letterRusWord.txt"));

            if (messageText.equals("/start") || messageText.equals("/1")) {
                guessWord.reset();  // Сброс игры
                sendMessage(chatId, "Привет! Я бот, который поможет угадать слово из 5 букв! Поехали!");
                String initialResponse = guessWord.processInput("");
                sendLongMessage(chatId, initialResponse);
            } else {
                String responseText = guessWord.processInput(messageText);
                sendLongMessage(chatId, responseText);
            }
        }
    }

    private void sendLongMessage(long chatId, String text) {
        int maxLines = 20;
        String[] lines = text.split("\n");
        StringBuilder messagePart = new StringBuilder();

        int lineCounter = 0;
        for (String line : lines) {
            if (lineCounter == maxLines) {
                sendMessage(chatId, messagePart.toString());
                messagePart = new StringBuilder();
                lineCounter = 0;
            }
            messagePart.append(line).append("\n");
            lineCounter++;
        }

        if (messagePart.length() > 0) {
            sendMessage(chatId, messagePart.toString());
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);  // Отправить сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
