package org.mage.data_validator.controller;

//import com.deliveredtechnologies.rulebook.FactMap;
//import com.deliveredtechnologies.rulebook.NameValueReferableMap;
//import com.deliveredtechnologies.rulebook.RuleBook;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
//import org.mage.data_validator.rules.FileValidationRules;
import org.mage.data_validator.service.TemplateDataService;
import org.mage.data_validator.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
//import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
//@Api(value = "File Upload and Validation", tags = "File Upload and Validation")
public class FileUploadController {

    @Autowired
    private ValidationService validationService;
    @Autowired
    private TemplateDataService templateDataService;
    @Autowired
    private KieSession kieSession;
    @Autowired
    private KieContainer kieContainer;

    private static String UPLOAD_DIR = "uploads/";
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @PostConstruct
    public void init() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        String errorMessageStr = validationService.validateFileType(file);

        if (StringUtils.isEmpty(errorMessageStr)) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
                validationService.validateFile(file.getOriginalFilename());
                return new ResponseEntity<>("File uploaded successfully: " + file.getOriginalFilename(), HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logger.warn("File type validation failed for file: {}", file.getOriginalFilename());
            return new ResponseEntity<>(errorMessageStr, HttpStatus.BAD_REQUEST);
        }
    }
}
