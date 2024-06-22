package pro.sky.animal_shelter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Такой отчет не найден")
public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException() {
        super("Отчет не найден!");
    }
}
