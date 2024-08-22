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
            String messageText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start") || messageText.equals("1")) {
                guessWord.reset();  // Сброс игры
                sendMessage(chatId, "Привет! Я бот для игры в угадывание слов. Для перезапуска введите \"1\"");

                // Начать игру сразу после приветствия
                String initialResponse = guessWord.processInput("");  // Начать с первого шага
                sendMessage(chatId, initialResponse);  // Отправить запрос на ввод известных букв
            } else {
                // Обрабатываем последующие сообщения в игре
                String responseText = guessWord.processInput(messageText);
                sendMessage(chatId, responseText);  // Отправить результат обработки
            }
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
