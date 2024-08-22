package org.example;

import java.util.HashMap;
import java.util.Map;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MyTelegramBot extends TelegramLongPollingBot {

    // Карта для хранения состояний пользователей
    private final Map<Long, GuessWord> userGames;

    public MyTelegramBot() {
        userGames = new HashMap<>();
    }

    @Override
    public String getBotUsername() {
        return "Helper5Word_bot";  // Замените на имя вашего бота
    }

    @Override
    public String getBotToken() {
        return "7070579986:AAHhl8hUpGWIIUgNL0Cj95tXCmXU0stQuCc";  // Замените на ваш токен API
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();  // Получаем имя пользователя

            // Логируем имя пользователя и его сообщение
            System.out.println("User: " + (username != null ? username : "Unknown") + " - Message: " + messageText);

            // Получаем текущее состояние игры для данного пользователя
            GuessWord guessWord = userGames.computeIfAbsent(chatId, id -> new GuessWord("D:\\Java\\IdeaProjects\\NET project\\GuessWord2\\resources\\russian5word.txt"));

            if (messageText.equals("/start") || messageText.equals("1")) {
                guessWord.reset();  // Сброс игры
                sendMessage(chatId, "Привет! Я бот для игры в угадывание слов. Для перезапуска введите \"1\"");
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

    public static void main(String[] args) throws TelegramApiException {
        // Регистрация бота
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new MyTelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
