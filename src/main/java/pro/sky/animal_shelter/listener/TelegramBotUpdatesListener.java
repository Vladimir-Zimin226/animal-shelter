package pro.sky.animal_shelter.listener;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import org.springframework.beans.factory.annotation.Autowired;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pro.sky.animal_shelter.content.TelegramBotContent.*;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                String chatId = update.message().chat().id().toString();
                String text = update.message().text();
                switch (text) {
                    case "/start":
                        SendMessage welcomeMessage = new SendMessage(chatId,WELCOME_MESSAGE);
                        welcomeMessage.replyMarkup(createKeyboard());
                        SendResponse welcomeMessageResponse = telegramBot.execute(welcomeMessage);
                        break;
                    case "История приюта":
                        SendMessage shelterHistory = new SendMessage(chatId,SHELTER_HISTORY);
                        shelterHistory.replyMarkup(createKeyboard());
                        SendResponse shelterHistoryResponse = telegramBot.execute(shelterHistory);
                }

            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    private ReplyKeyboardMarkup createKeyboard() {
        KeyboardButton button1 = new KeyboardButton("История приюта");
        KeyboardButton button2 = new KeyboardButton("Расписание работы");
        KeyboardButton button3 = new KeyboardButton("Схема проезда");
        KeyboardButton button4 = new KeyboardButton("Оформить пропуск");
        KeyboardButton button5 = new KeyboardButton("Техника безопасности");
        KeyboardButton button6 = new KeyboardButton("Оставить контакты для связи");
        KeyboardButton button7 = new KeyboardButton("Назад");

        KeyboardButton[][] keyboardButtons = {
                {button1, button2},
                {button3, button4},
                {button5, button6},
                {button7}
        };

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }
}
