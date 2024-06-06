package pro.sky.animal_shelter.service;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.UserContact;
import pro.sky.animal_shelter.repository.UserContactRepository;


@Service
public class UserContactService {
    private final UserContactRepository userContactRepository;

    public UserContactService(UserContactRepository userContactRepository) {
        this.userContactRepository = userContactRepository;
    }

    public UserContact add(UserContact userContact) {
        return userContactRepository.save(userContact);
    }
}
