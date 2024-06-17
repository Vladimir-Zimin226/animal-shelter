    package pro.sky.animal_shelter.service.implementations;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.web.multipart.MultipartFile;
    import pro.sky.animal_shelter.entity.PhotoOfPet;
    import pro.sky.animal_shelter.entity.Report;
    import pro.sky.animal_shelter.repository.PhotoOfPetRepository;
    import pro.sky.animal_shelter.service.services.ReportService;

    import javax.imageio.ImageIO;
    import java.awt.*;
    import java.awt.image.BufferedImage;
    import java.io.*;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.Collection;
    import java.util.Objects;

    import static java.nio.file.StandardOpenOption.CREATE_NEW;

    public class PhotoOfPetService {
        @Value("photos")
        private String photosDir;

        private final ReportService reportService;
        private final PhotoOfPetRepository photoOfPetRepository;

        public PhotoOfPetService(ReportService reportService, PhotoOfPetRepository photoOfPetRepository) {
            this.reportService = reportService;
            this.photoOfPetRepository = photoOfPetRepository;
        }

        public void uploadAvatar(String userId, MultipartFile file) throws IOException {
            Report report = reportService.findReportByUserId(Long.valueOf(userId));

            Path filePath = Path.of(photosDir, userId + "." + getExtension(Objects.requireNonNull(file.getOriginalFilename())));
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);

            try (InputStream is = file.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                 BufferedInputStream bis = new BufferedInputStream(is, 1024);
                 BufferedOutputStream bos = new BufferedOutputStream(os, 1024);

            ) {
                bis.transferTo(bos);
            }

            PhotoOfPet photoOfPet = new PhotoOfPet();
            photoOfPet.setReport(report);
            photoOfPet.setFilePath(filePath.toString());
            photoOfPet.setFileSize(file.getSize());
            photoOfPet.setMediaType(file.getContentType());
            photoOfPet.setPhotoData(generateDataForBase(filePath));
            photoOfPetRepository.save(photoOfPet);
        }


        public byte[] generateDataForBase(Path filePath) throws IOException {
            try (
                    InputStream is = Files.newInputStream(filePath);
                    BufferedInputStream bis = new BufferedInputStream(is, 1024);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                BufferedImage image = ImageIO.read(bis);
                int hieght = image.getHeight() / (image.getWidth() / 100);
                BufferedImage preview = new BufferedImage(100, hieght, image.getType());
                Graphics2D graphics2D = preview.createGraphics();
                graphics2D.drawImage(image, 0, 0, 100, hieght, null);
                graphics2D.dispose();

                ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
                return baos.toByteArray();
            }
        }



        public Collection<PhotoOfPet> getAllPhotos(Integer pageNumber, Integer pageSize) {
            PageRequest page = PageRequest.of(pageNumber-1,pageSize);
            return photoOfPetRepository.findAll(page).getContent();
        }


        public String getExtension(String fileName) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }

    }
