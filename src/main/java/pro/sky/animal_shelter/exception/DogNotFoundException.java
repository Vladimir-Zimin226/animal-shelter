package pro.sky.animal_shelter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Такая собака не найдена")
public class DogNotFoundException extends RuntimeException {

    public DogNotFoundException() {
        super("Собака не найдена!");
    }
}
