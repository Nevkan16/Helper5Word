package org.example;

import Config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Helper5WordBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(Helper5WordBot.class);
    private final BotConfig config;

    // Карта для хранения состояний пользователей
    private final Map<Long, GuessWord> userGames;

    public Helper5WordBot(BotConfig config) {
        this.config = config;
        userGames = new HashMap<>();

        logger.info("Бот {} успешно запущен", this.config.getBotName());
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();  // Замените на имя вашего бота
    }

    @Override
    public String getBotToken() {
        return config.getToken();  // Замените на ваш токен API
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String inputText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();  // Получаем имя пользователя
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Логируем имя пользователя и его сообщение
            logger.info("Time: {} User: {} - Message: {}", formatter.format(dateTime),
                    (username != null ? username : "Unknown"), inputText);

            // Получаем текущее состояние игры для данного пользователя
            GuessWord guessWord = userGames.computeIfAbsent(chatId, id ->
                    new GuessWord("src/main/resources/5letterRusWord.txt"));

            if (inputText.equals("/start") || inputText.equals("/1")) {
                guessWord.reset();  // Сброс игры
                sendMessage(chatId, "Привет, " + username + "!\nЯ бот, который поможет угадать слово из 5 букв! Поехали!");
                String initialResponse = guessWord.processInput("");
                sendLongMessage(chatId, initialResponse);
            } else {
                String responseText = guessWord.processInput(inputText);
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
