package pro.sky.animal_shelter.listener;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendPhoto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.chatStates.ChatStateForBackButton;
import pro.sky.animal_shelter.chatStates.ChatStateForContactInfo;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.services.UserService;

import java.util.*;

import static pro.sky.animal_shelter.content.TelegramBotContent.*;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserService userService;

    public TelegramBotUpdatesListener(UserService userService) {
        this.userService = userService;
    }

    Map<String, ChatStateForBackButton> chatStateForBackButtonMap = new HashMap<>();
    Map<String, ChatStateForContactInfo> chatStateForContactInfoMap = new HashMap<>();
    Map<String, Users> userContactMap = new HashMap<>();

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
                String chatId = String.valueOf(update.message().chat().id());
                String text = update.message().text();
                String telegramId = String.valueOf(update.message().from().id());
                ChatStateForBackButton chatStateForBackButton;
                if (text.equals("/start")) {
                    SendPhoto welcomePhoto = new SendPhoto(chatId, WELCOME_PHOTO);
                    telegramBot.execute(welcomePhoto);
                    SendMessage welcomeMessage = new SendMessage(chatId, WELCOME_MESSAGE);
                    telegramBot.execute(welcomeMessage);
                } else if (text.equals("/help")) {
                    SendMessage whatUWant = new SendMessage(chatId, "Я являюсь ботом приюта Счастье В Дом. Нажмите на кнопку ниже, чтобы узнать о нашей компании больше");
                    whatUWant.replyMarkup(createKeyBoardForStart());
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("main_menu", "null"));
                    telegramBot.execute(whatUWant);
                } else if (text.equals("Узнать информацию о приюте")) {
                    SendMessage whatUWant = new SendMessage(chatId, "Что вы хотели бы узнать?");
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelter_info", "main_menu"));
                    whatUWant.replyMarkup(createKeyboardForShelterInfo());
                    telegramBot.execute(whatUWant);
                } else if (text.equals("История приюта")) {
                    SendMessage shelterHistoryMessage = new SendMessage(chatId, HISTORY);
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
                    shelterHistoryMessage.replyMarkup(createBackKeyboard());
                    telegramBot.execute(shelterHistoryMessage);
                } else if (text.equals("Расписание работы")) {
                    SendMessage shelterHistoryMessage = new SendMessage(chatId, OPENING_HOURS);
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
                    shelterHistoryMessage.replyMarkup(createBackKeyboard());
                    telegramBot.execute(shelterHistoryMessage);
                } else if (text.equals("Схема проезда")) {
                    SendPhoto shelterMapPhoto = new SendPhoto(chatId, SCHEMA_SHELTER);
                    telegramBot.execute(shelterMapPhoto);
                    SendMessage shelterHistoryMessage = new SendMessage(chatId, ADDRES);
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
                    shelterHistoryMessage.replyMarkup(createBackKeyboard());
                    telegramBot.execute(shelterHistoryMessage);
                } else if (text.equals("Помочь приюту")) {
                    SendMessage shelterHistoryMessage = new SendMessage(chatId, DONATE);
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
                    shelterHistoryMessage.replyMarkup(createBackKeyboard());
                    telegramBot.execute(shelterHistoryMessage);
                } else if (text.equals("Техника безопасности")) {
                    SendMessage techdefMessage = new SendMessage(chatId, SAFETY_MEASURES);
                    chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
                    techdefMessage.replyMarkup(createBackKeyboard());
                    telegramBot.execute(techdefMessage);
                } else if (text.equals("Назад") && chatStateForBackButtonMap.containsKey(chatId)) {
                    chatStateForBackButton = chatStateForBackButtonMap.get(chatId);
                    if (chatStateForBackButton.getPreviousMenu().equals("main_menu")) {
                        SendMessage backMessage = new SendMessage(chatId, "Возвращаемся");
                        backMessage.replyMarkup(createKeyBoardForStart());
                        telegramBot.execute(backMessage);
                        chatStateForBackButton.setCurrentMenu("main_menu", "null");
                    } else if (chatStateForBackButton.getPreviousMenu().equals("shelter_info")) {
                        SendMessage backMessage = new SendMessage(chatId, "Возвращаемся");
                        backMessage.replyMarkup(createKeyboardForShelterInfo());
                        telegramBot.execute(backMessage);
                        chatStateForBackButton.setCurrentMenu("shelter_info", "main_menu");
                    }
                }else if (text.equals("Оставить контакты для связи")) {
                    initiatePassProcess(chatId);
                }else if (text.equals("Согласен(-на)") || !chatStateForContactInfoMap.get(chatId).equals(ChatStateForContactInfo.WAITING_FOR_CONFIRMATION)) {
                    switch (chatStateForContactInfoMap.getOrDefault(chatId,ChatStateForContactInfo.NONE)) {
                        case WAITING_FOR_CONFIRMATION:
                            hanldeConfirmation(chatId);
                            break;
                        case WAITING_FOR_FULL_NAME:
                            handleFullName(chatId, text, telegramId);
                            break;
                        case WAITING_FOR_PHONE_NUMBER:
                            handlePhoneNumber(chatId, text);
                            break;
                        default:
                            sendUnknownCommandMessage(chatId);
                            break;
                    }
                }

            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void initiatePassProcess(String chatId) {
        SendMessage passMessage = new SendMessage(chatId, "Согласны ли вы на обработку некоторых данных?");
        passMessage.replyMarkup(createKeyboardForContactInfo());
        telegramBot.execute(passMessage);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
    }

    private void hanldeConfirmation(String chatId) {
        SendMessage fullNameMessage = new SendMessage(chatId, "Тогда пришлите пожалуйста ФИО");
        telegramBot.execute(fullNameMessage);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
    }

    private void handleFullName(String chatId, String text, String telegramId) {
        Users user = new Users();
        user.setName(text);
        user.setTelegramId(telegramId);
        user.setVolunteer(false);
        userContactMap.put(chatId, user);

        SendMessage phoneNumberMessage = new SendMessage(chatId, "Пришлите номер телефона, в формате: +74953500505");
        telegramBot.execute(phoneNumberMessage);

        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_PHONE_NUMBER);
        logger.info("Updated state to WAITING_FOR_PHONE_NUMBER for chat {}", chatId);
    }

    private void handlePhoneNumber(String chatId, String text) {
        Users user = userContactMap.get(chatId);
        if (text.matches("^\\+7\\d{10}$") && user != null) {
            user.setPhoneNumber(text);
            SendMessage confirmationMessage = new SendMessage(chatId, "Благодарим, мы с вами свяжемся.");
            telegramBot.execute(confirmationMessage);
        } else {
            throw new RuntimeException("Введён некорретный номер телефона, попробуйте снова!");
        }




        userService.createUser(user);
        chatStateForContactInfoMap.remove(chatId);
        userContactMap.remove(chatId);
        logger.info("Removed chat {} from chatStateForContactInfoMap and userContactMap", chatId);
    }


    private void sendUnknownCommandMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Неизвестная команда. Пожалуйста, используйте клавиатуру для выбора опций.");
        telegramBot.execute(message);
    }

    private ReplyKeyboardMarkup createKeyBoardForStart() {
        KeyboardButton button1 = new KeyboardButton("Узнать информацию о приюте");
        KeyboardButton button2 = new KeyboardButton("Как взять животное из приюта");
        KeyboardButton button3 = new KeyboardButton("Прислать отчёт о питомце");
        KeyboardButton button4 = new KeyboardButton("Позвать волонтёра");

        KeyboardButton[][] keyboardButtons = {
                {button1, button2},
                {button3, button4}
        };
        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createKeyboardForShelterInfo() {
        KeyboardButton button1 = new KeyboardButton("История приюта");
        KeyboardButton button2 = new KeyboardButton("Расписание работы");
        KeyboardButton button3 = new KeyboardButton("Схема проезда");
        KeyboardButton button4 = new KeyboardButton("Помочь приюту");
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

    private ReplyKeyboardMarkup createKeyboardForContactInfo() {
        KeyboardButton button1 = new KeyboardButton("Согласен(-на)");
        KeyboardButton button2 = new KeyboardButton("Назад");
        KeyboardButton[][] keyboardButtons = {
                {button1, button2},
        };

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createBackKeyboard() {
        KeyboardButton button = new KeyboardButton("Назад");

        KeyboardButton[] keyboardButtons = {button};

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }
}