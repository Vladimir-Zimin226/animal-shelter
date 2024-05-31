package pro.sky.animal_shelter.service;


import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.repository.TelegrambotRepository;

@Service
public class TelegrambotService {

    private final TelegrambotRepository telegrambotRepository;

    public TelegrambotService(TelegrambotRepository telegrambotRepository) {
        this.telegrambotRepository = telegrambotRepository;
    }

    public String addFullName(SendMessage gotMessage) {
        return gotMessage.toString();
    }

    public String addDateOfBirth(SendMessage gotMessage) {
        return gotMessage.toString();
    }

    public String addPhoneNumber(SendMessage gotMessage) {
        return gotMessage.toString();
    }


}
