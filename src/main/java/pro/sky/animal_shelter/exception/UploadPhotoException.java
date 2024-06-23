package pro.sky.animal_shelter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Фото не загружается")
public class UploadPhotoException extends RuntimeException {
    public UploadPhotoException() {
        super("Проблемы с загрузкой фотографии");
    }
}
