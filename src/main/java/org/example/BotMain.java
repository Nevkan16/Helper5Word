package org.example;

import Config.BotConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotMain {
    public static void main(String[] args) throws TelegramApiException {
        ApplicationContext context = new AnnotationConfigApplicationContext(BotConfig.class);

        BotConfig config = context.getBean(BotConfig.class);
        // Регистрация бота
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new Helper5WordBot(config));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
