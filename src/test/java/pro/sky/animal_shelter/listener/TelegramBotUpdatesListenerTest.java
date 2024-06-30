package pro.sky.animal_shelter.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import pro.sky.animal_shelter.chatStates.ChatStateForBackButton;
import pro.sky.animal_shelter.chatStates.ChatStateForContactInfo;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.services.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static pro.sky.animal_shelter.content.TelegramBotContent.*;

@SpringBootTest

public class TelegramBotUpdatesListenerTest {

    @Autowired
    TelegramBotUpdatesListener listener;

    @MockBean
    TelegramBot telegramBot;

    @MockBean
    UserService userService;

    @Captor
    ArgumentCaptor<SendMessage> messageCaptor;

    Map<String, ChatStateForBackButton> chatStateForBackButtonMap = new HashMap<>();
    Map<String, ChatStateForContactInfo> chatStateForContactInfoMap = new HashMap<>();
    Map<String, Users> userContactMap  = new HashMap<>();
    /**
     * Тестируем метод sendWelcomeMessage.
     */

    @Test
    public void sendWelcomeMessageTest() {
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "/start"
                          }
                        }
                        """,
                Update.class
        );
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(WELCOME_MESSAGE, text);

    }

    /**
     * Тестируем метод sendShelterMap. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendShelterMapTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("shelters", "shelter_info"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Схема проезда"
                          }
                        }
                        """,
                Update.class
        );
        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(ADDRES, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendShelterHistory. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendShelterHistoryTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("shelters", "shelter_info"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "История приюта"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(HISTORY, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendOpeningHours. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendOpeningHoursTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("shelters", "shelter_info"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Расписание работы"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(OPENING_HOURS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendDonationInfo. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendDonationInfoTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("shelters", "shelter_info"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Помочь приюту"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(DONATE, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendSafetyMeasures. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendSafetyMeasuresTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("shelters", "shelter_info"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Техника безопасности"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(SAFETY_MEASURES, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем - отрабатывает ли метод с инициацией помощи волонтера (initiateVolunteerHelp).
     */

    @Test
    public void initiateVolunteerHelpTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.NONE);
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("volunteer_help", "main_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Позвать волонтёра"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Отправьте пожалуйста номер телефона (в формате: +74953500505), который привязан к вашему Telegram. " +
                "Убедитесь в настройках конфиденциальности в том, что вас можно найти по номеру телефона.", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.DROP);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод gotPhoneNumber. Проверяем - вызывается и отрабатывает ли метод sendInvalidPhoneNumberMessage
     * при некорректном вводе номера телефона пользователем.
     */

    @Test
    public void gotPhoneNumberTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.DROP);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "871234567890"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Вы ввели некорректный номер телефона, попробуйте заново.", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.DROP);
        Assertions.assertNotNull(replyKeyboardMarkup);

    }

    /**
     * Тестируем метод sendVolunteerConfirmation. Проверяем - вызывается и отрабатывает ли метод askVolunteerForHelp
     * при корректном вводе номера телефона пользователем.
     */

    @Test
    public void askVolunteerForHelpTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.DROP);
        Users volunteer = new Users();
        volunteer.setVolunteer(true);
        volunteer.setTelegramId("@volunteer");
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "+71234567890"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        Mockito.when(userService.findAnyVolunteerFromUsers()).thenReturn(volunteer);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(2)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("@volunteer", chatId);
        Assertions.assertEquals("Просьба о волонтёрской помощи по номеру: +71234567890 telegram ID: 123", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.NONE);

    }

    /**
     * Тестируем метод sendPreAdoptionRules. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendPreAdoptionRulesTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Правила знакомства с животным до того, как забрать его из приюта"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(PRE_ADOPTION_RULES, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendAdoptionDocumentsList. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendAdoptionDocumentsListTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Список документов для усыновления"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(ADOPTION_DOCUMENTS_LIST, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendTransportRecommendations. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendTransportRecommendationsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Рекомендации по транспортировке"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(TRANSPORT_RECOMMENDATIONS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendPuppySetupRecommendations. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendPuppySetupRecommendationsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Обустройство дома для щенка"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(PUPPY_SETUP_RECOMMENDATIONS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendAdultAnimalSetupRecommendations. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendAdultAnimalSetupRecommendationsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Обустройство дома для взрослого животного"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(ADULT_ANIMAL_SETUP_RECOMMENDATIONS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendSpecialNeedsAnimalSetupRecommendations. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendSpecialNeedsAnimalSetupRecommendationsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Обустройство дома для животного с ограниченными возможностями"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(SPECIAL_NEEDS_ANIMAL_SETUP_RECOMMENDATIONS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendCynologistTips. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendCynologistTipsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Советы кинолога по первичному общению с собакой"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(CYNOLOGIST_TIPS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendCynologistRecommendations. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendCynologistRecommendationsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Рекомендации по проверенным кинологам"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(CYNOLOGIST_RECOMMENDATIONS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Тестируем метод sendAdoptionRejectionReasons. Проверяем - вызывается и отрабатывает ли метод sendMessageWithBackButton.
     */

    @Test
    public void sendAdoptionRejectionReasonsTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("how_to_take_a_pet", "take_a_pet_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Причины отказа в усыновлении"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals(ADOPTION_REJECTION_REASONS, text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем работу метода handleBackButton в случае, если переменная равна null
     * и цикл SWITCH не вызывается
     */

    @Test
    public void handleBackButtonTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, null));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("main_menu", "null"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Назад"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Я являюсь ботом приюта Счастье В Дом. Нажмите на кнопку ниже, чтобы узнать о нашей компании больше", text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем работу метода sendHelpMessageTest, и правильно ли отработает метод handleBackButton в случае, если переменная
     * в цикле SWITCH не подходит не под одно из значений CASE
     */

    @Test
    public void sendHelpMessageTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, "volunteer_help"));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("main_menu", "null"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Назад"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Я являюсь ботом приюта Счастье В Дом. Нажмите на кнопку ниже, чтобы узнать о нашей компании больше", text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверка работы метода sendShelterInfoMenu
     */

    @Test
    public void sendShelterInfoMenuTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, "shelter_info"));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("shelter_info", "main_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Назад"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Что вы хотели бы узнать?", text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем правильно ли отработает метод sendMessageHowToTakeAPet
     */

    @Test
    public void sendMessageHowToTakeAPetTest() {
        chatStateForBackButtonMap.put("123", new ChatStateForBackButton(null, "take_a_pet_menu"));
        Map<String, ChatStateForBackButton> expectedMap = new HashMap<>();
        expectedMap.put("123", new ChatStateForBackButton("take_a_pet_menu", "main_menu"));
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Назад"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Что вы хотели бы узнать?", text);
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getPreviousMenu(),expectedMap.get("123").getPreviousMenu());
        Assertions.assertEquals(chatStateForBackButtonMap.get(chatId).getCurrentMenu(),expectedMap.get("123").getCurrentMenu());
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем, отрабатывает ли бот процесс инициализации сбора информации
     */

    @Test
    public void initiateContactInfoProcessTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.NONE);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Оставить контакты для связи"
                          }
                        }
                        """,
                Update.class
        );
        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Как к вам можно обращаться?", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
 * Проверяем, как метод processFullName обрабатывает процесс корректного ввода имени пользователя
 */

    @Test
    public void processFullNameTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
        Users user = new Users();
        userContactMap.put("123",user);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Иван"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "userContactMap", userContactMap);
        ReflectionTestUtils.setField(listener, "chatStateForBackButtonMap", chatStateForBackButtonMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Укажите ваш номер телефона (в формате: +74953500505)", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.WAITING_FOR_PHONE_NUMBER);
        Assertions.assertNotNull(chatStateForBackButtonMap.get("123"));

    }

    /**
     * Проверяем - как отработает метод processFullName, если пользователь введет некорректное имя
     */

    @Test
    public void sendInvalidNameMessageTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": ""
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Вы ввели некорректное имя, попробуйте заново.", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
        Assertions.assertNotNull(replyKeyboardMarkup);
    }
/**
 * Проверяем - правильно ли отработает метод при вводе невалидного номера телефона пользователем
 */

    @Test
    public void isValidPhoneNumberTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_PHONE_NUMBER);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "+7(123)123-4567"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Вы ввели некорректный номер телефона, попробуйте заново.", text);
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем - правильно ли отработает метод processPhoneNumber при корректном вводе данных
     */

    @Test
    public void processPhoneNumberTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_PHONE_NUMBER);
        Users user = new Users();
        user.setName("John");
        userContactMap.put("123",user);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "+71231234567"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "userContactMap", userContactMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Подтвердите корректность введённых данных (Да/Нет):\nИмя: " + user.getName() + "\nНомер телефона: " + user.getPhoneNumber(), text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);

    }

    /**
     * Проверяем - правильно ли отработает метод, запрашивающий - верно ли введена контактная информация от пользователя,
     * если ответ на вопрос введен некорректно
     */

    @Test
    public void sendInvalidConfirmationMessageTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
        Users user = new Users();
        userContactMap.put("123",user);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "Верно"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "userContactMap", userContactMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Некорректный ответ. Пожалуйста, ответьте 'Да' или 'Нет'.", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем, правильно ли отработает бот. Если на вопрос корректности ввода имени пользователь ответил "нет"
     */

    @Test
    public void retryContactInfoProcessTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
        Users user = new Users();
        userContactMap.put("123",user);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "нет"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "userContactMap", userContactMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Введите ваше имя повторно.", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.WAITING_FOR_FULL_NAME);
        Assertions.assertNotNull(replyKeyboardMarkup);
    }

    /**
     * Проверяем - правильно ли отработает бот при утвердительном ответе пользователя на запрос корректности введенного имени.
     * Запускается ли метод saveUserContactInfo при ответе - да.
     */

    @Test
    public void saveUserContactInfoTest() {
        chatStateForContactInfoMap.put("123",ChatStateForContactInfo.WAITING_FOR_CONFIRMATION);
        Users user = new Users();
        userContactMap.put("123",user);
        Update update = BotUtils.fromJson(
                """
                        {
                          "message": {
                            "from": {
                              "id": 123
                            },
                            "chat": {
                              "id": 123
                            },
                            "text": "да"
                          }
                        }
                        """,
                Update.class
        );

        ReflectionTestUtils.setField(listener, "chatStateForContactInfoMap", chatStateForContactInfoMap);
        ReflectionTestUtils.setField(listener, "userContactMap", userContactMap);
        listener.process(Collections.singletonList(update));
        Mockito.verify(telegramBot, Mockito.times(1)).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        ReplyKeyboardMarkup replyKeyboardMarkup = (ReplyKeyboardMarkup) message.getParameters().get("reply_markup");
        String chatId = (String) message.getParameters().get("chat_id");
        String text = (String) message.getParameters().get("text");

        Assertions.assertEquals("123", chatId);
        Assertions.assertEquals("Спасибо, ваши данные сохранены.", text);
        Assertions.assertEquals(chatStateForContactInfoMap.get("123"), ChatStateForContactInfo.NONE);
        Assertions.assertNotNull(replyKeyboardMarkup);
    }
}
