package pro.sky.animal_shelter.listener;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendPhoto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animal_shelter.chatStates.ChatStateForBackButton;
import pro.sky.animal_shelter.chatStates.ChatStateForContactInfo;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.sky.animal_shelter.content.TelegramBotContent.*;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserService userService;

    @Autowired
    private TelegramBot telegramBot;

    private final Map<String, ChatStateForBackButton> chatStateForBackButtonMap = new HashMap<>();
    private final Map<String, ChatStateForContactInfo> chatStateForContactInfoMap = new HashMap<>();
    private final Map<String, Users> userContactMap = new HashMap<>();

    @Autowired
    public TelegramBotUpdatesListener(UserService userService) {
        this.userService = userService;
    }

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

                handleUpdate(chatId, text, telegramId);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(String chatId, String text, String telegramId) {
        switch (text) {
            case "/start":
                sendWelcomeMessage(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            case "Узнать информацию о приюте":
                sendShelterInfoMenu(chatId);
                break;
            case "Позвать волонтёра":
                initiateVolunteerHelp(chatId);
                break;
            case "Как взять животное из приюта":
                sendMessageHowToTakeAPet(chatId);
                break;
            case "Правила знакомства с животным до того, как забрать его из приюта":
                sendPreAdoptionRules(chatId);
                break;
            case "Список документов для усыновления":
                sendAdoptionDocumentsList(chatId);
                break;
            case "Рекомендации по транспортировке":
                sendTransportRecommendations(chatId);
                break;
            case "Обустройство дома для щенка":
                sendPuppySetupRecommendations(chatId);
                break;
            case "Обустройство дома для взрослого животного":
                sendAdultAnimalSetupRecommendations(chatId);
                break;
            case "Обустройство дома для животного с ограниченными возможностями":
                sendSpecialNeedsAnimalSetupRecommendations(chatId);
                break;
            case "Советы кинолога по первичному общению с собакой":
                sendCynologistTips(chatId);
                break;
            case "Рекомендации по проверенным кинологам":
                sendCynologistRecommendations(chatId);
                break;
            case "Причины отказа в усыновлении":
                sendAdoptionRejectionReasons(chatId);
                break;
            case "История приюта":
                sendShelterHistory(chatId);
                break;
            case "Расписание работы":
                sendOpeningHours(chatId);
                break;
            case "Схема проезда":
                sendShelterMap(chatId);
                break;
            case "Помочь приюту":
                sendDonationInfo(chatId);
                break;
            case "Техника безопасности":
                sendSafetyMeasures(chatId);
                break;
            case "Назад":
                handleBackButton(chatId);
                break;
            case "Оставить контакты для связи":
                initiateContactInfoProcess(chatId);
                break;
            default:
                handleDefault(chatId, text, telegramId);
                break;
        }
    }

    private void handleDefault(String chatId, String text, String telegramId) {
        ChatStateForContactInfo state = chatStateForContactInfoMap.get(chatId);
        if (state != null) {
            switch (state) {
                case DROP:
                    gotPhoneNumber(chatId, telegramId, text);
                    break;
                case WAITING_FOR_CONFIRMATION:
                case WAITING_FOR_FULL_NAME:
                case WAITING_FOR_PHONE_NUMBER:
                    handleContactInfoProcess(chatId, text, telegramId);
                    break;
                default:
                    chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.NONE);
                    break;
            }
        }
    }

    private void sendWelcomeMessage(String chatId) {
        logger.info("Sending welcome message to chat {}", chatId);
        telegramBot.execute(new SendPhoto(chatId, WELCOME_PHOTO));
        telegramBot.execute(new SendMessage(chatId, WELCOME_MESSAGE));
    }

    private void sendHelpMessage(String chatId) {
        logger.info("Sending help message to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Я являюсь ботом приюта Счастье В Дом. Нажмите на кнопку ниже, чтобы узнать о нашей компании больше");
        message.replyMarkup(createKeyBoardForStart());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("main_menu", "null"));
        telegramBot.execute(message);
    }

    private void initiateVolunteerHelp(String chatId) {
        logger.info("Initiating volunteer help for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Отправьте пожалуйста номер телефона (в формате: +74953500505), который привязан к вашему Telegram. Убедитесь в настройках конфиденциальности в том, что вас можно найти по номеру телефона.");
        telegramBot.execute(message);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("volunteer_help", "main_menu"));
        message.replyMarkup(createBackKeyboard());
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.DROP);
    }

    private void gotPhoneNumber(String chatId, String telegramId, String text) {
        if (isValidPhoneNumber(text)) {
            logger.info("Received valid phone number from chat {}", chatId);
            sendVolunteerConfirmation(chatId, telegramId, text);
        } else {
            logger.warn("Received invalid phone number from chat {}", chatId);
            sendInvalidPhoneNumberMessage(chatId);
        }
    }

    private boolean isValidPhoneNumber(String text) {
        return text != null && text.trim().matches("^\\+7\\d{10}$");
    }

    private void sendVolunteerConfirmation(String chatId, String telegramId, String text) {
        SendMessage message = new SendMessage(chatId, "Благодарим, волонтёр свяжется с вами в ближайшее время");
        telegramBot.execute(message);
        message.replyMarkup(createBackKeyboard());
        askVolunteerForHelp(chatId, telegramId, text);
    }

    private void sendInvalidPhoneNumberMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Вы ввели некорректный номер телефона, попробуйте заново.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void askVolunteerForHelp(String chatId, String telegramId, String text) {
        Users volunteer = userService.findAnyVolunteerFromUsers();
        String volunteerTelegramId = volunteer.getTelegramId();
        SendMessage message = new SendMessage(volunteerTelegramId, "Просьба о волонтёрской помощи по номеру: " + text + " telegram ID: " + telegramId);
        telegramBot.execute(message);
        clearContactInfoState(chatId);
    }

    private void sendShelterInfoMenu(String chatId) {
        logger.info("Sending shelter info menu to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Что вы хотели бы узнать?");
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelter_info", "main_menu"));
        message.replyMarkup(createKeyboardForShelterInfo());
        telegramBot.execute(message);
    }

    private void sendShelterHistory(String chatId) {
        logger.info("Sending shelter history to chat {}", chatId);
        sendMessageWithBackButton(chatId, HISTORY, "shelters", "shelter_info");
    }

    private void sendOpeningHours(String chatId) {
        logger.info("Sending opening hours to chat {}", chatId);
        sendMessageWithBackButton(chatId, OPENING_HOURS, "shelters", "shelter_info");
    }

    private void sendShelterMap(String chatId) {
        logger.info("Sending shelter map to chat {}", chatId);
        SendPhoto photoMessage = new SendPhoto(chatId, SCHEMA_SHELTER);
        telegramBot.execute(photoMessage);
        sendMessageWithBackButton(chatId, ADDRES, "shelters", "shelter_info");
    }

    private void sendDonationInfo(String chatId) {
        logger.info("Sending donation info to chat {}", chatId);
        sendMessageWithBackButton(chatId, DONATE, "shelters", "shelter_info");
    }

    private void sendSafetyMeasures(String chatId) {
        logger.info("Sending safety measures to chat {}", chatId);
        sendMessageWithBackButton(chatId, SAFETY_MEASURES, "shelters", "shelter_info");
    }

    private void sendMessageHowToTakeAPet(String chatId) {
        logger.info("Sending message how to take a pet to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Что вы хотели бы узнать?");
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("take_a_pet_menu", "main_menu"));
        message.replyMarkup(createKeyboardToKnowHowToTakeAPet());
        telegramBot.execute(message);
    }

    private void sendPreAdoptionRules(String chatId) {
        logger.info("Sending pre-adoption rules to chat {}", chatId);
        sendMessageWithBackButton(chatId, PRE_ADOPTION_RULES, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendAdoptionDocumentsList(String chatId) {
        logger.info("Sending adoption documents list to chat {}", chatId);
        sendMessageWithBackButton(chatId, ADOPTION_DOCUMENTS_LIST, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendTransportRecommendations(String chatId) {
        logger.info("Sending transport recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, TRANSPORT_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendPuppySetupRecommendations(String chatId) {
        logger.info("Sending puppy setup recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, PUPPY_SETUP_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendAdultAnimalSetupRecommendations(String chatId) {
        logger.info("Sending adult animal setup recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, ADULT_ANIMAL_SETUP_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendSpecialNeedsAnimalSetupRecommendations(String chatId) {
        logger.info("Sending special needs animal setup recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, SPECIAL_NEEDS_ANIMAL_SETUP_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendCynologistTips(String chatId) {
        logger.info("Sending cynologist tips to chat {}", chatId);
        sendMessageWithBackButton(chatId, CYNOLOGIST_TIPS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendCynologistRecommendations(String chatId) {
        logger.info("Sending cynologist recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, CYNOLOGIST_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void sendAdoptionRejectionReasons(String chatId) {
        logger.info("Sending adoption rejection reasons to chat {}", chatId);
        sendMessageWithBackButton(chatId, ADOPTION_REJECTION_REASONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    private void handleBackButton(String chatId) {
        logger.info("Handling back button for chat {}", chatId);
        ChatStateForBackButton state = chatStateForBackButtonMap.get(chatId);
        if (state != null) {
            switch (state.getPreviousMenu()) {
                case "main_menu":
                    sendHelpMessage(chatId);
                    break;
                case "take_a_pet_menu":
                    sendMessageHowToTakeAPet(chatId);
                    break;
                case "shelter_info":
                    sendShelterInfoMenu(chatId);
                    break;
                default:
                    sendHelpMessage(chatId);
                    break;
            }
        } else {
            sendHelpMessage(chatId);
        }
    }

    private void initiateContactInfoProcess(String chatId) {
        logger.info("Initiating contact info process for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Как к вам можно обращаться?");
        telegramBot.execute(message);
        message.replyMarkup(createBackKeyboard());
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
    }

    private void handleContactInfoProcess(String chatId, String text, String telegramId) {
        switch (chatStateForContactInfoMap.get(chatId)) {
            case WAITING_FOR_FULL_NAME:
                processFullName(chatId, text, telegramId);
                break;
            case WAITING_FOR_PHONE_NUMBER:
                processPhoneNumber(chatId, text, telegramId);
                break;
            case WAITING_FOR_CONFIRMATION:
                processConfirmation(chatId, text, telegramId);
                break;
            default:
                break;
        }
    }

    private void processFullName(String chatId, String text, String telegramId) {
        if (isValidName(text)) {
            Users user = new Users();
            user.setName(text);
            user.setTelegramId(telegramId);
            userContactMap.put(telegramId, user);
            SendMessage message = new SendMessage(chatId, "Укажите ваш номер телефона (в формате: +74953500505)");
            telegramBot.execute(message);
            chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("contact_info", "shelter_info"));
            chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_PHONE_NUMBER);
        } else {
            sendInvalidNameMessage(chatId);
        }
    }

    private boolean isValidName(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private void sendInvalidNameMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Вы ввели некорректное имя, попробуйте заново.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void processPhoneNumber(String chatId, String text, String telegramId) {
        if (isValidPhoneNumber(text)) {
            Users user = userContactMap.get(telegramId);
            user.setPhoneNumber(text);
            userContactMap.put(telegramId, user);
            SendMessage message = new SendMessage(chatId, "Подтвердите корректность введённых данных (Да/Нет):\nИмя: " + user.getName() + "\nНомер телефона: " + user.getPhoneNumber());
            telegramBot.execute(message);
            chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
        } else {
            sendInvalidPhoneNumberMessage(chatId);
        }
    }

    private void processConfirmation(String chatId, String text, String telegramId) {
        if (text.equalsIgnoreCase("да")) {
            saveUserContactInfo(chatId, telegramId);
        } else if (text.equalsIgnoreCase("нет")) {
            retryContactInfoProcess(chatId);
        } else {
            sendInvalidConfirmationMessage(chatId);
        }
    }

    private void saveUserContactInfo(String chatId, String telegramId) {
        Users user = userContactMap.get(telegramId);
        userService.createUser(user);
        SendMessage message = new SendMessage(chatId, "Спасибо, ваши данные сохранены.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
        clearContactInfoState(chatId);
    }

    private void retryContactInfoProcess(String chatId) {
        SendMessage message = new SendMessage(chatId, "Введите ваше имя повторно.");
        telegramBot.execute(message);
        message.replyMarkup(createBackKeyboard());
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
    }

    private void sendInvalidConfirmationMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Некорректный ответ. Пожалуйста, ответьте 'Да' или 'Нет'.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void clearContactInfoState(String chatId) {
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.NONE);
    }

    private void sendMessageWithBackButton(String chatId, String text, String backButtonState, String previousState) {
        SendMessage message = new SendMessage(chatId, text);
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton(backButtonState, previousState));
    }


    /**
     * Методы по созданию кнопок
     */

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

    private ReplyKeyboardMarkup createKeyboardToKnowHowToTakeAPet() {
        KeyboardButton button1 = new KeyboardButton("Правила знакомства с животным до того, как забрать его из приюта");
        KeyboardButton button2 = new KeyboardButton("Список документов для усыновления");
        KeyboardButton button3 = new KeyboardButton("Рекомендации по транспортировке");
        KeyboardButton button4 = new KeyboardButton("Обустройство дома для щенка");
        KeyboardButton button5 = new KeyboardButton("Обустройство дома для взрослого животного");
        KeyboardButton button6 = new KeyboardButton("Обустройство дома для животного с ограниченными возможностями");
        KeyboardButton button7 = new KeyboardButton("Советы кинолога по первичному общению с собакой");
        KeyboardButton button8 = new KeyboardButton("Рекомендации по проверенным кинологам");
        KeyboardButton button9 = new KeyboardButton("Причины отказа в усыновлении");
        KeyboardButton button10 = new KeyboardButton("Назад");

        KeyboardButton[][] keyboardButtons = {
                {button1, button2},
                {button3, button4},
                {button5, button6},
                {button7, button8},
                {button9, button10},
        };

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createKeyboardForContactInfo() {
        KeyboardButton button1 = new KeyboardButton("Согласен(-на)");
        KeyboardButton button2 = new KeyboardButton("Назад");

        KeyboardButton[][] keyboardButtons = {
                {button1, button2}
        };

        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createBackKeyboard() {
        KeyboardButton button = new KeyboardButton("Назад");
        KeyboardButton[] keyboardButtons = {button};
        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

}