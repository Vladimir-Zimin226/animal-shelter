package pro.sky.animal_shelter.listener;

import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetFileResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pro.sky.animal_shelter.chatStates.ChatStateForBackButton;
import pro.sky.animal_shelter.chatStates.ChatStateForContactInfo;
import pro.sky.animal_shelter.entity.Report;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.exception.UploadPhotoException;
import pro.sky.animal_shelter.exception.UserNotFoundException;
import pro.sky.animal_shelter.repository.ReportRepository;
import pro.sky.animal_shelter.repository.UsersRepository;
import pro.sky.animal_shelter.service.services.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import static pro.sky.animal_shelter.content.TelegramBotContent.*;

/**
 * Класс TelegramBotUpdatesListener обрабатывает обновления от Telegram и взаимодействует с пользователями.
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserService userService;
    private final UsersRepository usersRepository;
    private final ReportRepository reportRepository;

    @Autowired
    private TelegramBot telegramBot;

    private final Map<String, ChatStateForBackButton> chatStateForBackButtonMap = new HashMap<>();
    private final Map<String, ChatStateForContactInfo> chatStateForContactInfoMap = new HashMap<>();
    private final Map<String, Users> userContactMap = new HashMap<>();

    LocalDate currentDate = LocalDate.now();

    @Autowired
    public TelegramBotUpdatesListener(UserService userService, UsersRepository usersRepository, ReportRepository reportRepository) {
        this.userService = userService;
        this.usersRepository = usersRepository;
        this.reportRepository = reportRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron = "0 28 19 * * *")
    public void sendNotifications() {
        List<Users> usersCollection = usersRepository.findAll();
        usersCollection.forEach(userCheck -> {
            if (userCheck.getReports().size() < 31) {
                SendMessage message = new SendMessage(userCheck.getTelegramId(), "Приветсвую вас, хозяин питомца. Не забудьте сегодня прислать отчёт о питомце. Спасибо!");
                telegramBot.execute(message);
            } else if (userCheck.getReports().size() == 31) {
                SendMessage message = new SendMessage(usersRepository.findAnyVolunteerForConsultansy().getTelegramId(), "Пользотатель с telegramid: " + userCheck.getTelegramId() + ";\n" +
                        "По имени: " + userCheck.getName() + ";\n" +
                        "Уже отправил 30 отчётов. Просим проанализирова и принять решение по судьбе питомца.");
                message.replyMarkup(createKeyboardForPetDecision());
                telegramBot.execute(message);
            } else if (userCheck.getReports().size() > 31 && userCheck.getReports().size() < 45) {
                SendMessage message = new SendMessage(userCheck.getTelegramId(), "Приветсвую вас, хозяин питомца. Не забудьте сегодня прислать отчёт о питомце. Спасибо!");
                telegramBot.execute(message);
            } else if (userCheck.getReports().size() < 46) {
                SendMessage message = new SendMessage(usersRepository.findAnyVolunteerForConsultansy().getTelegramId(), "Пользотатель с telegramid: " + userCheck.getTelegramId() + ";\n" +
                        "По имени: " + userCheck.getName() + ";\n" +
                        "После продления базового количества отчётов (30 штук). Уже отправил 15 отчётов. Просим проанализировать и принять окончательное решение по судьбе питомца.");
                message.replyMarkup(createKeyboardForPetLastDecision());
                telegramBot.execute(message);
            }
        });
    }
    /**
     * Обработка списка обновлений.
     *
     * @param updates Список обновлений.
     * @return Код подтверждения обработки всех обновлений.
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                String chatId = String.valueOf(update.message().chat().id());
                String text = update.message().text();
                String telegramId = String.valueOf(update.message().from().id());

                Users user = usersRepository.findUserByTelegramId(telegramId);

                if (user == null || !user.isVolunteer()) {
                    try {
                        handleUpdate(chatId, text, telegramId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (usersRepository.findUserByTelegramId(telegramId).isVolunteer()) {
                    handleUpdateForVolunteer(chatId, text);
                }
            } else if (update.message() != null && update.message().photo() != null) {
                String chatId = String.valueOf(update.message().chat().id());

                Long userId = (update.message() != null && update.message().from() != null) ? update.message().from().id() : null;
                logger.info("ID пользователя для чата {}: {}", chatId, userId);
                if (userId == null) {
                    logger.error("Не удалось получить ID пользователя для чата {}", chatId);
                    SendMessage errorMessage = new SendMessage(chatId, "Не удалось получить ID пользователя.");
                    telegramBot.execute(errorMessage);
                    return;
                }

                Users user = usersRepository.findById(userId)
                        .orElseThrow(() -> {
                            SendMessage warningMessage = new SendMessage(chatId, "Вы не можете отправлять отчет, сначала возьмите животное из приюта");
                            telegramBot.execute(warningMessage);
                            return new UserNotFoundException();
                        });
                // Создаем или восстанавливаем отчет для текущего пользователя
                Report newReport = reportRepository.findReportByUser(user);
                logger.info("Текущий отчет для пользователя {}: {}", userId, newReport);
                if (newReport == null || newReport.getDate() != currentDate) {
                    newReport = new Report();
                    logger.info("Создан новый отчет для пользователя {}", userId);
                    newReport.setUser(user);
                    logger.info("Сохраняем ID пользователя для созданного отчета");
                    newReport.setDate(currentDate);
                    logger.info("Добавляем в отчет дату");
                }

                // Загрузить фото и установить его в отчет
                try {
                    Path photoPath = uploadPhoto(chatId, update);
                    if (photoPath != null) {
                        newReport.setPhotoOfPet(photoPath.toString());
                        logger.info("Фото получено и добавлено в отчет для пользователя {}", userId);
                        reportRepository.save(newReport); // Сохранить отчет, если фото успешно загружено
                    }
                } catch (UploadPhotoException e) {
                    logger.error("Ошибка при загрузке фото для чата {}: {}", chatId, e.getMessage());
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Обработка кнопок с обновлениями.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Текст сообщения.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
    private void handleUpdate(String chatId, String text, String telegramId) throws IOException {

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
            case "Прислать отчёт о питомце":
                initiateReport(chatId);
                break;
            case "Начать процесс":
                processBegins(chatId);
                break;
            case "Сохранить фото":
                initiateReportInfoProcess(chatId);
                break;
            default:
                handleDefault(chatId, text, telegramId);
                break;
        }
    }

    private void handleUpdateForVolunteer(String chatId, String text) {
        switch(text) {
            case "Продлить время проверки хозяина на 15 доп.отчётов":
                SendMessage message = new SendMessage(chatId, "Тестовый период был продлён на 15 доп. оотчётов");
                telegramBot.execute(message);
                break;
            case "Отдать питомца насовсем и закрыть тестовый период.":
                chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_TELEGRAM_ID_OF_NEW_PET_OWNER);
                SendMessage message1 = new SendMessage(chatId, "Отправьте telegram_id пользователя, которого вы рекомендуете как постоянного хозяина питомца.");
                telegramBot.execute(message1);
                break;
            case "Отказать в получении питомца":
                chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_TELEGRAM_ID_OF_REJECTED_PET_OWNER);
                SendMessage message2 = new SendMessage(chatId, "Отправьте telegram_id пользователя, которого вы НЕ рекомендуете как постоянного хозяина питомца.");
                telegramBot.execute(message2);
                break;
            default:
                handlePetGiveAway(chatId, text);
                break;
        }
    }

    private void handlePetGiveAway(String chatId, String text) {
        switch (chatStateForContactInfoMap.get(chatId)) {
            case WAITING_FOR_TELEGRAM_ID_OF_NEW_PET_OWNER:
                handleNewOwner(chatId, text);
                break;
            case WAITING_FOR_TELEGRAM_ID_OF_REJECTED_PET_OWNER:
                handleRejectedUser(chatId, text);
                break;
        }
    }

    private void handleNewOwner(String chatId, String text) {
        SendMessage message = new SendMessage(text.trim(), "Дорогой хозяин питомца. Поздравляем вас с прохождение тестового периода!!! В данные момент вы признаны полноправным хозяином вашего питомца. Отныне, необходимости присылать отчёты нет. Благодарим за проявленную лояльности и заботу к животному!");
        telegramBot.execute(message);
        SendMessage message1 = new SendMessage(chatId, "Пользователю по telegramId: " + text.trim() + " отправлено сообщение об одобрении опекунства и окончания тестового периода.");
        telegramBot.execute(message1);
        Users newOwner = usersRepository.findUserByTelegramId(text.trim());
        reportRepository.deleteAllByUserId(newOwner.getId());
    }

    private void handleRejectedUser(String chatId, String text) {
        SendMessage message = new SendMessage(text.trim(), "Приветствую дорогой клиент компании Счастье в дом. К сожалению, вынуждены сообщить о том, что в полноправном получении питомца и его постоянном опекунстве вам отказано. Для подробной информации обратитесь к волонтёру.");
        telegramBot.execute(message);
        SendMessage message1 = new SendMessage(chatId, "Пользователю по telegramId: " + text.trim() + " отправлено сообщение об отказе опекунства и окончания тестового периода.");
        telegramBot.execute(message1);
        Users rejectedUser = usersRepository.findUserByTelegramId(text.trim());
        reportRepository.deleteAllByUserId(rejectedUser.getId());
    }

    /**
     * Обработка сообщений по умолчанию.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Текст сообщения.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
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
                case WAITING_FOR_DIET_OF_PET:
                    reportContactInfoProcess(chatId, text);
                    break;
                case WAITING_FOR_WELLBEING_INFO:
                    addPetWellBeingInformation(chatId, text);
                    break;
                case WAITING_FOR_HABITSCHANGES_INFO:
                    addPetHabbitsChangesInformation(chatId, text);
                    break;
                default:
                    chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.NONE);
                    break;
            }
        }
    }

    /**
     * Отправка приветственного сообщения.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendWelcomeMessage(String chatId) {
        logger.info("Sending welcome message to chat {}", chatId);
        telegramBot.execute(new SendPhoto(chatId, WELCOME_PHOTO));
        telegramBot.execute(new SendMessage(chatId, WELCOME_MESSAGE));
    }

    /**
     * Отправка сообщения с основыми функциями.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendHelpMessage(String chatId) {
        logger.info("Sending help message to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Я являюсь ботом приюта Счастье В Дом. Нажмите на кнопку ниже, чтобы узнать о нашей компании больше");
        message.replyMarkup(createKeyBoardForStart());
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("main_menu", "null"));
        telegramBot.execute(message);
    }

    /**
     * Отправка информации о приюте.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendShelterInfoMenu(String chatId) {
        logger.info("Sending shelter info menu to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Что вы хотели бы узнать?");
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("shelter_info", "main_menu"));
        message.replyMarkup(createKeyboardForShelterInfo());
        telegramBot.execute(message);
    }

    /**
     * Методы по обработке кнопок с информацией о приюте.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendShelterHistory(String chatId) {
        logger.info("Sending shelter history to chat {}", chatId);
        sendMessageWithBackButton(chatId, HISTORY, "shelters", "shelter_info");
    }
    /**
     * Отправка информации о времени работы приюта.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendOpeningHours(String chatId) {
        logger.info("Sending opening hours to chat {}", chatId);
        sendMessageWithBackButton(chatId, OPENING_HOURS, "shelters", "shelter_info");
    }

    /**
     * Отправка информации о схеме располажения приюта.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendShelterMap(String chatId) {
        logger.info("Sending shelter map to chat {}", chatId);
        SendPhoto photoMessage = new SendPhoto(chatId, SCHEMA_SHELTER);
        telegramBot.execute(photoMessage);
        sendMessageWithBackButton(chatId, ADDRES, "shelters", "shelter_info");
    }

    /**
     * Отправка информации о возможности оказать финансовую помощь приюту.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendDonationInfo(String chatId) {
        logger.info("Sending donation info to chat {}", chatId);
        sendMessageWithBackButton(chatId, DONATE, "shelters", "shelter_info");
    }

    /**
     * Отправка информации о мерах безопасности при нахождении в приюте.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendSafetyMeasures(String chatId) {
        logger.info("Sending safety measures to chat {}", chatId);
        sendMessageWithBackButton(chatId, SAFETY_MEASURES, "shelters", "shelter_info");
    }

    /**
     * Инициация помощи волонтёра.
     *
     * @param chatId Идентификатор чата.
     */
    private void initiateVolunteerHelp(String chatId) {
        logger.info("Initiating volunteer help for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Отправьте пожалуйста номер телефона (в формате: +74953500505), который привязан к вашему Telegram. Убедитесь в настройках конфиденциальности в том, что вас можно найти по номеру телефона.");
        telegramBot.execute(message);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("volunteer_help", "main_menu"));
        message.replyMarkup(createBackKeyboard());
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.DROP);
    }

    /**
     * Обработка полученного номера телефона.
     *
     * @param chatId     Идентификатор чата.
     * @param telegramId Идентификатор пользователя в Telegram.
     * @param text       Текст сообщения.
     */
    private void gotPhoneNumber(String chatId, String telegramId, String text) {
        if (isValidPhoneNumber(text)) {
            logger.info("Received valid phone number from chat {}", chatId);
            sendVolunteerConfirmation(chatId, telegramId, text);
        } else {
            logger.warn("Received invalid phone number from chat {}", chatId);
            sendInvalidPhoneNumberMessage(chatId);
        }
    }

    /**
     * Проверка валидности номера телефона.
     *
     * @param text Текст сообщения.
     * @return Возвращает true, если номер телефона валиден, иначе false.
     */
    private boolean isValidPhoneNumber(String text) {
        return text != null && text.trim().matches("^\\+7\\d{10}$");
    }

    /**
     * Отправка подтверждения получения номера телефона.
     *
     * @param chatId     Идентификатор чата.
     * @param telegramId Идентификатор пользователя в Telegram.
     * @param text       Текст сообщения.
     */
    private void sendVolunteerConfirmation(String chatId, String telegramId, String text) {
        SendMessage message = new SendMessage(chatId, "Благодарим, волонтёр свяжется с вами в ближайшее время");
        telegramBot.execute(message);
        message.replyMarkup(createBackKeyboard());
        askVolunteerForHelp(chatId, telegramId, text);
    }

    /**
     * Отправка сообщения о неверном номере телефона.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendInvalidPhoneNumberMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Вы ввели некорректный номер телефона, попробуйте заново.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    /**
     * Отправка рандомному волонтёру из БД информации о необходим консультации.
     *
     * @param chatId     Идентификатор чата.
     * @param telegramId Идентификатор пользователя в Telegram.
     * @param text       Текст сообщения.
     */
    private void askVolunteerForHelp(String chatId, String telegramId, String text) {
        Users volunteer = userService.findAnyVolunteerFromUsers();
        String volunteerTelegramId = volunteer.getTelegramId();
        SendMessage message = new SendMessage(volunteerTelegramId, "Просьба о волонтёрской помощи по номеру: " + text + " telegram ID: " + telegramId);
        telegramBot.execute(message);
        clearContactInfoState(chatId);
    }

    /**
     * Создание кнопок с инофрмацием по тому, как можно взять животное из приюта.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendMessageHowToTakeAPet(String chatId) {
        logger.info("Sending message how to take a pet to chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Что вы хотели бы узнать?");
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("take_a_pet_menu", "main_menu"));
        message.replyMarkup(createKeyboardToKnowHowToTakeAPet());
        telegramBot.execute(message);
    }

    /**
     * Методы по обработке кнопок с информацией о том, как взять животное из приюта.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendPreAdoptionRules(String chatId) {
        logger.info("Sending pre-adoption rules to chat {}", chatId);
        sendMessageWithBackButton(chatId, PRE_ADOPTION_RULES, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка списка документов для усыновления.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendAdoptionDocumentsList(String chatId) {
        logger.info("Sending adoption documents list to chat {}", chatId);
        sendMessageWithBackButton(chatId, ADOPTION_DOCUMENTS_LIST, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка информации с реккомендациями по транспортировке.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendTransportRecommendations(String chatId) {
        logger.info("Sending transport recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, TRANSPORT_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка информации по обустройству дома для щенков.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendPuppySetupRecommendations(String chatId) {
        logger.info("Sending puppy setup recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, PUPPY_SETUP_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка информации с реккомендациями по обсутройству дома для взрослой собаки.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendAdultAnimalSetupRecommendations(String chatId) {
        logger.info("Sending adult animal setup recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, ADULT_ANIMAL_SETUP_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка информации по обустройству дома для животного с ограниченными возможностями.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendSpecialNeedsAnimalSetupRecommendations(String chatId) {
        logger.info("Sending special needs animal setup recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, SPECIAL_NEEDS_ANIMAL_SETUP_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка информации с советами кинолога по первичному общению с собакойи.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendCynologistTips(String chatId) {
        logger.info("Sending cynologist tips to chat {}", chatId);
        sendMessageWithBackButton(chatId, CYNOLOGIST_TIPS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка реккмоендаций с проверенными кинологами.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendCynologistRecommendations(String chatId) {
        logger.info("Sending cynologist recommendations to chat {}", chatId);
        sendMessageWithBackButton(chatId, CYNOLOGIST_RECOMMENDATIONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Отправка информации с причинами отказа в усыновлении.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendAdoptionRejectionReasons(String chatId) {
        logger.info("Sending adoption rejection reasons to chat {}", chatId);
        sendMessageWithBackButton(chatId, ADOPTION_REJECTION_REASONS, "how_to_take_a_pet", "take_a_pet_menu");
    }

    /**
     * Обработка нажатия кнопки "Назад".
     * Этот метод восстанавливает предыдущее состояние чата на основе
     * сохранённого состояния в {@code chatStateForBackButtonMap}.
     *
     * @param chatId Идентификатор чата, в котором была нажата кнопка "Назад".
     */
    private void handleBackButton(String chatId) {
        logger.info("Handling back button for chat {}", chatId);
        String state = chatStateForBackButtonMap.getOrDefault(chatId, new ChatStateForBackButton("none", "main_menu")).getPreviousMenu();
        if (state != null) {
            switch (state) {
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


    /**
     * Инициализирует процесс сбора контактной информации у пользователя.
     *
     * @param chatId Идентификатор чата.
     */
    private void initiateContactInfoProcess(String chatId) {
        logger.info("Initiating contact info process for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Как к вам можно обращаться?");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
    }

    /**
     * Обрабатывает процесс ввода контактной информации пользователем.
     * Этот метод считывает состояние чата на основе
     * сохранённого состояния в {@code chatStateForContactInfo}.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Введённый текст.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
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

    /**
     * Обрабатывает ввод полного имени пользователя.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Введённый текст.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
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

    /**
     * Инициализирует процесс сбора текстовой информации для отчета.
     *
     * @param chatId Идентификатор чата.
     */
    private void initiateReportInfoProcess(String chatId) {
        logger.info("Initiating report info process for chat {}", chatId);
        SendMessage message = new SendMessage(chatId, "Пришлите информацию о питании собаки");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_DIET_OF_PET);
    }

    /**
     * Обрабатывает процесс ввода текстовой информации пользователем для отчета.
     * Этот метод считывает состояние чата на основе
     * сохранённого состояния в {@code chatStateForContactInfo}.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Введённый текст.
     */
    private void reportContactInfoProcess(String chatId, String text) {
        switch (chatStateForContactInfoMap.get(chatId)) {
            case WAITING_FOR_DIET_OF_PET:
                addPetDietInformation(chatId, text);
                break;
            case WAITING_FOR_WELLBEING_INFO:
                addPetWellBeingInformation(chatId, text);
                break;
            case WAITING_FOR_HABITSCHANGES_INFO:
                addPetHabbitsChangesInformation(chatId, text);
                break;
            default:
                break;
        }
    }

    /**
     * Проверяет, является ли введённый текст допустимым.
     *
     * @param text Введённый текст.
     * @return {@code true}, если имя допустимо, иначе {@code false}.
     */
    private boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Проверяет, является ли введённое имя допустимым.
     *
     * @param text Введённый текст.
     * @return {@code true}, если имя допустимо, иначе {@code false}.
     */
    private boolean isValidName(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Отправляет сообщение о некорректном имени пользователю.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendInvalidNameMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Вы ввели некорректное имя, попробуйте заново.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    /**
     * Отправляет сообщение о некорректных текстовых данных.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendInvalidTextMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Вы ввели некорректные данные, попробуйте заново.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    /**
     * Обрабатывает ввод номера телефона пользователя.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Введённый текст.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
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

    /**
     * Обрабатывает подтверждение контактной информации пользователем.
     *
     * @param chatId     Идентификатор чата.
     * @param text       Введённый текст.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
    private void processConfirmation(String chatId, String text, String telegramId) {
        if (text.equalsIgnoreCase("да")) {
            saveUserContactInfo(chatId, telegramId);
        } else if (text.equalsIgnoreCase("нет")) {
            retryContactInfoProcess(chatId);
        } else {
            sendInvalidConfirmationMessage(chatId);
        }
    }

    /**
     * Сохраняет контактную информацию пользователя.
     *
     * @param chatId     Идентификатор чата.
     * @param telegramId Идентификатор пользователя в Telegram.
     */
    private void saveUserContactInfo(String chatId, String telegramId) {
        Users user = userContactMap.get(telegramId);
        userService.createUser(user);
        SendMessage message = new SendMessage(chatId, "Спасибо, ваши данные сохранены.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
        clearContactInfoState(chatId);
    }

    /**
     * Начинает процесс повторного ввода контактной информации пользователем.
     *
     * @param chatId Идентификатор чата.
     */
    private void retryContactInfoProcess(String chatId) {
        SendMessage message = new SendMessage(chatId, "Введите ваше имя повторно.");
        telegramBot.execute(message);
        message.replyMarkup(createBackKeyboard());
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
    }

    /**
     * Отправляет сообщение о некорректном подтверждении пользователю.
     *
     * @param chatId Идентификатор чата.
     */
    private void sendInvalidConfirmationMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, "Некорректный ответ. Пожалуйста, ответьте 'Да' или 'Нет'.");
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
    }

    /**
     * Очищает состояние процесса сбора контактной информации.
     *
     * @param chatId Идентификатор чата.
     */
    private void clearContactInfoState(String chatId) {
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.NONE);
    }

    /**
     * Отправляет сообщение с кнопкой "Назад", которая обрабатывается в методе
     * (code: handleBackButton).
     *
     * @param chatId          Идентификатор чата.
     * @param text            Отправляемый текст
     * @param backButtonState Состояние кнопки назад
     * @param previousState   Предыдущее меню, в котором был пользователь
     */
    private void sendMessageWithBackButton(String chatId, String text, String backButtonState, String previousState) {
        SendMessage message = new SendMessage(chatId, text);
        message.replyMarkup(createBackKeyboard());
        telegramBot.execute(message);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton(backButtonState, previousState));
    }

    /**
     * Метод для создания стартовых кнопок бота.
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

    /**
     * Метод для создания кнопок бота в меню информации о приюте.
     */
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

    /**
     * Метод для создания кнопок бота в меню с подробной информации о процессе усыновления животных.
     */
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

    /**
     * Метод для создания кнопки назад.
     */
    private ReplyKeyboardMarkup createBackKeyboard() {
        KeyboardButton button = new KeyboardButton("Назад");
        KeyboardButton[] keyboardButtons = {button};
        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    /**
     * Метод для создания кнопок для обработки отчета.
     */
    private ReplyKeyboardMarkup createKeyboardForReport() {
        KeyboardButton button = new KeyboardButton("Начать процесс");
        KeyboardButton button1 = new KeyboardButton("Назад");

        KeyboardButton[] buttons = {button, button1};

        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    /**
     * Метод для создания кнопок при обработке фотографии.
     */
    private ReplyKeyboardMarkup createKeyboardForPhoto() {
        KeyboardButton button = new KeyboardButton("Сохранить фото");
        KeyboardButton button1 = new KeyboardButton("Назад");

        KeyboardButton[] buttons = {button, button1};

        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    /**
     * Метод по инициации процесса по отправке отчёта о питомце
     *
     * @param chatId Идентификатор чата.
     */
    private void initiateReport(String chatId) {
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("otchet_o_pitomce", "main_menu"));
        SendMessage message = new SendMessage(chatId, OTCHET_O_PITOMCE);
        message.replyMarkup(createKeyboardForReport());
        telegramBot.execute(message);
    }

    /**
     * Сохраняет информацию о питании собаки для отчета.
     *
     * @param chatId     Идентификатор чата.
     * @param text Идентификатор пользователя в Telegram.
     */
    private void addPetDietInformation(String chatId, String text) {
        if (isValidText(text)) {
        Report newReport = reportRepository.findReportByDate(currentDate);
        newReport.setDiet(text);
        reportRepository.save(newReport);

        SendMessage message = new SendMessage(chatId, "Пришлите информацию о самочувствии питомца");
        telegramBot.execute(message);
        chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("add_diet", "begin_report_process"));
        chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_WELLBEING_INFO);
        } else {
            sendInvalidTextMessage(chatId);
        }
    }

    /**
     * Сохраняет информацию о самочувствии собаки для отчета.
     *
     * @param chatId     Идентификатор чата.
     * @param text Идентификатор пользователя в Telegram.
     */
    private void addPetWellBeingInformation(String chatId, String text) {
        if (isValidText(text)) {
            Report newReport = reportRepository.findReportByDate(currentDate);
            newReport.setWellBeing(text);
            reportRepository.save(newReport);

            SendMessage message = new SendMessage(chatId, "Пришлите информацию о возможных изменениях в поведении");
            telegramBot.execute(message);
            chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("add_wellbeing", "add_diet"));
            chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.WAITING_FOR_HABITSCHANGES_INFO);
        } else {
            sendInvalidTextMessage(chatId);
        }
    }

    /**
     * Сохраняет информацию об изменениях в поведении животного.
     *
     * @param chatId     Идентификатор чата.
     * @param text Идентификатор пользователя в Telegram.
     */
    private void addPetHabbitsChangesInformation(String chatId, String text) {
        if (isValidText(text)) {
            Report newReport = reportRepository.findReportByDate(currentDate);
            newReport.setBehaviorChanges(text);
            reportRepository.save(newReport);

            SendMessage message = new SendMessage(chatId, "Спасибо за предоставленную информацию! Не забывайте присылать отчеты ежедневно");
            telegramBot.execute(message);
            chatStateForBackButtonMap.put(chatId, new ChatStateForBackButton("add_habbits_changes", "add_wellbeing"));
            chatStateForContactInfoMap.put(chatId, ChatStateForContactInfo.NONE);
        } else {
            sendInvalidTextMessage(chatId);
        }
    }

    /**
     * Тригер метод для инициализации процесса сбора текстовой информации для отчета.
     *
     * @param chatId     Идентификатор чата.
     */
    private void processBegins(String chatId) {
        SendMessage pleasePhotomessage = new SendMessage(chatId, "Пришлите фото питомца");
        pleasePhotomessage.replyMarkup(createKeyboardForPhoto());
        telegramBot.execute(pleasePhotomessage);
    }

    /**
     * Метод для обработки фотографии, преобразовании ее в байт-код.
     * После преобразования, сохраняет фотографию, записывает путь в перменную
     *
     * @param chatId     Идентификатор чата.
     * @param update Идентификатор пользователя в Telegram.
     *
     * @return Path write
     */
    private Path uploadPhoto(String chatId, Update update) throws UploadPhotoException {
        if (update.message() != null && update.message().photo() != null && update.message().photo().length > 0) {
            logger.info("Фото получено для чата {}. Начинается обработка фото.", chatId);
            PhotoSize telegramPhoto = update.message().photo()[update.message().photo().length - 1];
            GetFileResponse getFileResponse = telegramBot.execute(new GetFile(telegramPhoto.fileId()));
            logger.info("Ответ на получение файла: {}", getFileResponse);

            if (getFileResponse.isOk()) {
                try {
                    String extension = StringUtils.getFilenameExtension(getFileResponse.file().filePath());
                    byte[] image = telegramBot.getFileContent(getFileResponse.file());
                    Path write = Files.write(Paths.get(UUID.randomUUID() + "." + extension), image);
                    logger.info("Фото успешно сохранено для чата {}.", chatId);
                    // Вернуть путь к файлу после успешного сохранения
                    return write;
                } catch (IOException e) {
                    logger.error("Ошибка при обработке фото для чата {}: {}", chatId, e.getMessage());
                    SendMessage message = new SendMessage(chatId, "Простите, произошла ошибка при обработке фото. Пожалуйста, попробуйте еще раз.");
                    telegramBot.execute(message);
                }
            } else {
                logger.error("Не удалось получить файл фото: {}", getFileResponse.errorCode());
                SendMessage errorMessage = new SendMessage(chatId, "Произошла ошибка при получении файла фото. Попробуйте еще раз.");
                telegramBot.execute(errorMessage);
            }
        } else {
            logger.info("Сообщение не содержит фото для чата {}", chatId);
            SendMessage errorMessage = new SendMessage(chatId, "Допустимо только фото. Попробуйте еще раз.");
            telegramBot.execute(errorMessage);
        }
        return null;  // Вернуть null, если что-то пошло не так
    }


    private ReplyKeyboardMarkup createKeyboardForPetDecision() {
        KeyboardButton button = new KeyboardButton("Продлить время проверки хозяина на 15 доп.отчётов");
        KeyboardButton button1 = new KeyboardButton("Отдать питомца насовсем и закрыть тестовый период.");

        KeyboardButton[][] keyboardButtons =
                {{button},
                {button1}};


        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private ReplyKeyboardMarkup createKeyboardForPetLastDecision() {
        KeyboardButton button = new KeyboardButton("Отказать в получении питомца");
        KeyboardButton button1 = new KeyboardButton("Отдать питомца насовсем и закрыть тестовый период.");

        KeyboardButton[][] keyboardButtons =
                {{button},
                {button1}};


        return new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true).oneTimeKeyboard(true);
    }




}
