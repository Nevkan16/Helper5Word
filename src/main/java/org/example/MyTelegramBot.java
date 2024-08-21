package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MyTelegramBot extends TelegramLongPollingBot {

    private final GuessWord guessWord;

    public MyTelegramBot() {
        guessWord = new GuessWord("D:\\Java\\IdeaProjects\\NET project\\GuessWord2\\resources\\russian5word.txt");
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
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));

            // Проверка на команду /start
            if (messageText.equals("/start")) {
                message.setText("Привет! Я бот для игры в угадывание слов. Используйте команды:\n" +
                        " - present:а,б — добавить буквы, которые есть в слове.\n" +
                        " - absent:в,г — добавить буквы, которых нет в слове.\n" +
                        " - pattern:*о*о* — установить шаблон, где * заменяет неизвестные буквы.\n" +
                        " - reset — сбросить данные и начать заново.");
            } else {
                // Обработка остальных команд
                String responseText = guessWord.processInput(messageText);
                message.setText(responseText);
            }

            try {
                execute(message); // Отправляем сообщение пользователю
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        // Регистрируем бота
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new MyTelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
