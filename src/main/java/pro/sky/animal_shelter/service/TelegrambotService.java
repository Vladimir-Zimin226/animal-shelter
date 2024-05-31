package pro.sky.animal_shelter.service;


import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Pass;
import pro.sky.animal_shelter.repository.TelegrambotRepository;

@Service
public class TelegrambotService {

    private final TelegrambotRepository telegrambotRepository;

    public TelegrambotService(TelegrambotRepository telegrambotRepository) {
        this.telegrambotRepository = telegrambotRepository;
    }

    public Pass add(Pass pass) {
        return telegrambotRepository.save(pass);
    }


}
