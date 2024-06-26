package pro.sky.animal_shelter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Такая кошка не найдена")
public class CatNotFoundException extends RuntimeException {

    public CatNotFoundException() {
        super("Кошка не найдена!");
    }
}
