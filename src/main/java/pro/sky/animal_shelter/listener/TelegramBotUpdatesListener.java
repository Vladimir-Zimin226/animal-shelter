package pro.sky.animal_shelter.listener;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendPhoto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animal_shelter.chatStates.ChatStateForBackButton;
import pro.sky.animal_shelter.chatStates.ChatStateForContactInfo;
import pro.sky.animal_shelter.entity.Dogs;
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
            case "Узнать информацию о приюте":
                sendShelterInfoMenu(chatId);
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
                handleContactInfoProcess(chatId, text, telegramId);
                break;
        }
    }


    /**
     * Методы по работе с отправкой приветсвующих и информациюонных сообщений
     */

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


    /**
     * Методы по работе с кнопкой "Узнать информацию о приюте" и вытекающими из неё кнопками
     */
    private void sendShelterInfoMenu(String chatId) {
        logger.info("Sending shelter info menu to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Что вы хотели бы узнать?");
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelter_info", "main_menu"));
        message.replyMarkup(createKeyboardForShelterInfo());
        telegramBot.execute(message);
    }

    private void sendShelterHistory(String chatId) {
        logger.info("Sending shelter history to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, HISTORY);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void sendOpeningHours(String chatId) {
        logger.info("Sending opening hours to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, OPENING_HOURS);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void sendShelterMap(String chatId) {
        logger.info("Sending shelter map to chat {}", chatId);
        telegramBot.execute(new SendPhoto(chatId, SCHEMA_SHELTER));
        SendMessage message = new SendMessage(chatId, ADDRES);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void sendDonationInfo(String chatId) {
        logger.info("Sending donation info to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, DONATE);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void sendSafetyMeasures(String chatId) {
        logger.info("Sending safety measures to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, SAFETY_MEASURES);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelters", "shelter_info"));
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    private void initiateContactInfoProcess(String chatId) {
        logger.info("Initiating contact info process for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Согласны ли вы на обработку некоторых данных?");
        message.replyMarkup(createKeyboardForContactInfo());
        telegramBot.execute(message);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
    }

    private void handleContactInfoProcess(String chatId, String text, String telegramId) {
        ChatStateForContactInfo state = chatStateForContactInfoMap.getOrDefault(chatId, ChatStateForContactInfo.NONE);
        switch (state) {
            case WAITING_FOR_CONFIRMATION:
                if ("Согласен(-на)".equals(text)) {
                    handleConfirmation(chatId);
                }
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

    private void handleConfirmation(String chatId) {
        logger.info("Handling confirmation for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Тогда пришлите пожалуйста ФИО");
        telegramBot.execute(message);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
    }

    private void handleFullName(String chatId, String text, String telegramId) {
        logger.info("Handling full name for chat {}", chatId);
        Users user = new Users();
        user.setName(text);
        user.setTelegramId(telegramId);
        user.setVolunteer(false);
        userContactMap.put(chatId, user);

        SendMessage message = new SendMessage(chatId, "Пришлите номер телефона, в формате: +74953500505");
        telegramBot.execute(message);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_PHONE_NUMBER);
    }

    private void handlePhoneNumber(String chatId, String text) {
        Users user = userContactMap.get(chatId);
        if (user != null && text.matches("^\\+7\\d{10}$")) {
            logger.info("Handling phone number for chat {}", chatId);
            user.setPhoneNumber(text);
            SendMessage message = new SendMessage(chatId, "Благодарим, мы с вами свяжемся.");
            telegramBot.execute(message);
            userService.createUser(user);
            clearContactInfoState(chatId);
        } else {
            logger.warn("Invalid phone number or user not found for chat {}", chatId);
            SendMessage message = new SendMessage(chatId, "Вы ввели некорректный номер телефона, попробуйте заново.");
            telegramBot.execute(message);
        }
    }

    private void clearContactInfoState(String chatId) {
        chatStateForContactInfoMap.remove(chatId);
        userContactMap.remove(chatId);
        logger.info("Cleared contact info state for chat {}", chatId);
    }


    /**
     * Методы по работе с кнопкой "Как взять животное из приюта" и вытекающими из неё кнопками
     */

    private void sendMessageHowToTakeAPet(String chatId) {
        logger.info("Sending information about how to take a pet {}", chatId);
        SendMessage message = new SendMessage(chatId, "Нажмите на кнопку, чтобы мы поняли, что конкретно вы хотели бы узнать");
        message.replyMarkup(createKeyboardToKnowHowToTakeAPet());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("hot_to_take_a_pet_main", "main_menu"));
        telegramBot.execute(message);
    }

    private void sendPreAdoptionRules(String chatId) {
        logger.info("Sending pre-adoption rules to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, PRE_ADOPTION_RULES);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendAdoptionDocumentsList(String chatId) {
        logger.info("Sending adoption documents list to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, ADOPTION_DOCUMENTS_LIST);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendTransportRecommendations(String chatId) {
        logger.info("Sending transport recommendations to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, TRANSPORT_RECOMMENDATIONS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendPuppySetupRecommendations(String chatId) {
        logger.info("Sending puppy setup recommendations to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, PUPPY_SETUP_RECOMMENDATIONS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendAdultAnimalSetupRecommendations(String chatId) {
        logger.info("Sending adult animal setup recommendations to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, ADULT_ANIMAL_SETUP_RECOMMENDATIONS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendSpecialNeedsAnimalSetupRecommendations(String chatId) {
        logger.info("Sending special needs animal setup recommendations to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, SPECIAL_NEEDS_ANIMAL_SETUP_RECOMMENDATIONS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendCynologistTips(String chatId) {
        logger.info("Sending cynologist tips to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, CYNOLOGIST_TIPS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendCynologistRecommendations(String chatId) {
        logger.info("Sending cynologist recommendations to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, CYNOLOGIST_RECOMMENDATIONS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }

    private void sendAdoptionRejectionReasons(String chatId) {
        logger.info("Sending adoption rejection reasons to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, ADOPTION_REJECTION_REASONS);
        message.replyMarkup(createBackKeyboard());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("how_to_take_a_pet", "hot_to_take_a_pet_main"));
        telegramBot.execute(message);
    }


    /**
     * Методы по работе с кнопкой "Назад" и вытекающими из неё кнопками
     */

    private void handleBackButton(String chatId) {
        logger.info("Handling back button for chat {}", chatId);
        ChatStateForBackButton state = chatStateForBackButtonMap.get(chatId);
        if (state != null) {
            switch (state.getPreviousMenu()) {
                case "main_menu":
                    sendBackToMainMenu(chatId, state);
                    break;
                case "shelter_info":
                    sendBackToShelterInfo(chatId, state);
                    break;
                case "hot_to_take_a_pet_main":
                    sendBackToHowToTakeAPet(chatId, state);
                    break;
                default:
                    logger.warn("Unknown previous menu: {}", state.getPreviousMenu());
                    sendUnknownCommandMessage(chatId);
                    break;
            }
        } else {
            logger.warn("No state found for chat {}", chatId);
            sendUnknownCommandMessage(chatId);
        }
    }

    private void sendBackToMainMenu(String chatId, ChatStateForBackButton state) {
        SendMessage message = new SendMessage(chatId, "Возвращаемся");
        message.replyMarkup(createKeyBoardForStart());
        telegramBot.execute(message);
        state.setCurrentMenu("main_menu", "null");
    }

    private void sendBackToShelterInfo(String chatId, ChatStateForBackButton state) {
        SendMessage message = new SendMessage(chatId, "Возвращаемся");
        message.replyMarkup(createKeyboardToKnowHowToTakeAPet());
        telegramBot.execute(message);
        state.setCurrentMenu("shelter_info", "main_menu");
    }

    private void sendBackToHowToTakeAPet(String chatId, ChatStateForBackButton state) {
        SendMessage message = new SendMessage(chatId, "Возвращаемся");
        message.replyMarkup(createKeyboardToKnowHowToTakeAPet());
        telegramBot.execute(message);
        state.setCurrentMenu("hot_to_take_a_pet_main", "main_menu");
    }

    private void sendUnknownCommandMessage(String chatId) {
        logger.warn("Unknown command received for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Неизвестная команда. Пожалуйста, используйте клавиатуру для выбора опций.");
        telegramBot.execute(message);
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