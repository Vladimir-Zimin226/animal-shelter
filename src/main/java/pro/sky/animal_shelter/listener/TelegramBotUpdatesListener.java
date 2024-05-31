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
import pro.sky.animal_shelter.entity.Pass;
import pro.sky.animal_shelter.service.TelegrambotService;

import javax.annotation.PostConstruct;
import java.util.*;

import static pro.sky.animal_shelter.content.TelegramBotContent.*;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegrambotService telegrambotService;


    public TelegramBotUpdatesListener(TelegrambotService telegrambotService) {
        this.telegrambotService = telegrambotService;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    private Map<String, ConversationState> userStates = new HashMap<>();
    private Map<String, Pass> userPasses = new HashMap<>();

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                String chatId = update.message().chat().id().toString();
                String text = update.message().text();
                if (text.equals("/start")) {
                    SendMessage welcomeMessage = new SendMessage(chatId, WELCOME_MESSAGE);
                    welcomeMessage.replyMarkup(createKeyboard());
                    SendResponse welcomeMessageResponse = telegramBot.execute(welcomeMessage);
                } else if (text.equals("История приюта")) {
                    SendMessage shelterHistory = new SendMessage(chatId, SHELTER_HISTORY);
                    shelterHistory.replyMarkup(createKeyboard());
                    SendResponse shelterHistoryResponse = telegramBot.execute(shelterHistory);
                } else if (text.equals("Расписание работы")) {
                    SendMessage workTableMessage = new SendMessage(chatId, TABLE_MESSAGE);
                    workTableMessage.replyMarkup(createKeyboard());
                    SendResponse workTabkeResponse = telegramBot.execute(workTableMessage);
                } else if (text.equals("Схема проезда")) {
                    SendMessage direction = new SendMessage(chatId, DIRECTION_MESSAGE);
                    direction.replyMarkup(createKeyboard());
                    SendResponse directionResponse = telegramBot.execute(direction);
                } else if (text.equals("Оформить пропуск")) {
                    initiatePassProcess(chatId);
                } else if (text.equals("Согласен(-на)")) {
                    handleUserResponse(chatId, text);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    private void initiatePassProcess(String chatId) {
        SendMessage passMessage = new SendMessage(chatId, PASS_MESSAGE);
        passMessage.replyMarkup(createKeyboardForPass());
        telegramBot.execute(passMessage);
        userStates.put(chatId, ConversationState.WAITING_FOR_FULL_NAME);
    }

    private void handleUserResponse(String chatId, String text) {
        switch (getUserState(chatId)) {
            case WAITING_FOR_FULL_NAME:
                handleFullName(chatId, text);
                break;
            case WAITING_FOR_DATE_OF_BIRTH:
                handleDateOfBirth(chatId, text);
                break;
            case WAITING_FOR_PHONE_NUMBER:
                handlePhoneNumber(chatId, text);
                break;
            default:
                sendUnknownCommandMessage(chatId);
                break;
        }
    }

    private void sendUnknownCommandMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Неизвестная команда. Пожалуйста, используйте клавиатуру для выбора опций.");
        telegramBot.execute(message);
    }

    private ConversationState getUserState(String chatId) {
        return userStates.getOrDefault(chatId, ConversationState.NONE);
    }

    private void handleFullName(String chatId, String text) {
        Pass pass = new Pass();
        pass.setFullName(text);
        userPasses.put(chatId, pass);

        SendMessage dateOfBirthMessage = new SendMessage(chatId, DATE_OF_BIRTH_MESSAGE);
        telegramBot.execute(dateOfBirthMessage);

        userStates.put(chatId, ConversationState.WAITING_FOR_DATE_OF_BIRTH);
    }

    private void handleDateOfBirth(String chatId, String text) {
        Pass pass = userPasses.get(chatId);
        pass.setDateOfBirth(text);

        SendMessage phoneNumberMessage = new SendMessage(chatId, PHONE_NUMBER_MESSAGE);
        telegramBot.execute(phoneNumberMessage);

        userStates.put(chatId, ConversationState.WAITING_FOR_PHONE_NUMBER);
    }

    private void handlePhoneNumber(String chatId, String text) {
        Pass pass = userPasses.get(chatId);
        pass.setPhoneNumber(text);

        SendMessage confirmationMessage = new SendMessage(chatId, "Ваш пропуск успешно оформлен.");
        telegramBot.execute(confirmationMessage);

        telegrambotService.add(pass);

        userStates.put(chatId, ConversationState.NONE);
    }



    private ReplyKeyboardMarkup createKeyboard() {
        KeyboardButton button1 = new KeyboardButton("История приюта");
        KeyboardButton button2 = new KeyboardButton("Расписание работы");
        KeyboardButton button3 = new KeyboardButton("Схема проезда");
        KeyboardButton button4 = new KeyboardButton("Оформить пропуск");
        KeyboardButton button5 = new KeyboardButton("Техника безопасности");
        KeyboardButton button6 = new KeyboardButton("Оставить контакты для связи");

        KeyboardButton[][] keyboardButtons = {
                {button1, button2},
                {button3, button4},
                {button5, button6}
        };

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createKeyboardForPass() {
        KeyboardButton button1 = new KeyboardButton("Согласен(-на)");
        KeyboardButton button2 = new KeyboardButton("Назад");

        KeyboardButton[][] keyboardButtons = {
                {button1, button2}
        };

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createKeyBoardBack() {
        KeyboardButton buttonBack = new KeyboardButton("Назад");

        KeyboardButton[] keyboardButton = {buttonBack};

        return new ReplyKeyboardMarkup(keyboardButton).resizeKeyboard(true).oneTimeKeyboard(true);
    }
}
