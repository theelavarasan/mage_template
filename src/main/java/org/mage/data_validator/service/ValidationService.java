package org.mage.data_validator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
//import org.jeasy.rules.api.Rules;
import org.kie.api.runtime.KieSession;
import org.mage.data_validator.prop.ErrorMessage;
import org.mage.data_validator.prop.ValidationHeaderFact;
import org.mage.data_validator.rules.ValidationHeaderRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ValidationService {
//    private final Rules rules;

    private static final File UPLOAD_DIRECTORY = new File("uploads/");
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

//    public ValidationService() {
//        rules = new Rules();
//    }

    @Value("${expected.headers}")
    private String expectedHeadersString;

    private List<String> expectedHeaders;

    @Autowired
    private KieSession kieSession;
    @Autowired
    private TemplateDataService templateDataService;

    @PostConstruct
    public void init() {
        this.expectedHeaders = Arrays.asList(StringUtils.commaDelimitedListToStringArray(expectedHeadersString));
    }

    public String validateFileType(MultipartFile file) {
        String errorMessageStr = null;
        ErrorMessage errorMessage = new ErrorMessage();
        kieSession.setGlobal("errorMessage", errorMessage);
        kieSession.insert(file);
//        kieSession.fireAllRules();
        kieSession.getAgenda().getAgendaGroup("fileTypeValidation").setFocus();
        kieSession.fireAllRules();
        errorMessageStr = errorMessage.getMessage();
        return errorMessageStr;
    }

//    public boolean validateFileType(MultipartFile file) {
//        ValidateFileType validateFileType = new ValidateFileType(file);
//        rules.register(validateFileType);
//        RulesEngine rulesEngine = new DefaultRulesEngine();
//        Facts facts = new Facts();
//        rulesEngine.fire(rules, facts);
//        return validateFileType.isValid();
//    }

    public void validateExcelHeaders(String fileName) {
        Path path = Paths.get(UPLOAD_DIRECTORY.getPath(), fileName);
        File file = path.toFile();
        List<Map<String, String>> dataList = new ArrayList<>();

        try (Workbook workbook = readExcelFile(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            List<String> headers = new ArrayList<>();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim().toLowerCase());
            }

            ValidationHeaderRule validationHeaderRule = new ValidationHeaderRule(expectedHeaders);
            kieSession.setGlobal("validationHeaderRule", validationHeaderRule);
            ValidationHeaderFact validationHeaderFact = new ValidationHeaderFact(headers);
            kieSession.setGlobal("validationHeaderFact", validationHeaderFact);
            kieSession.insert(validationHeaderFact);
            kieSession.getAgenda().getAgendaGroup("validateHeaders").setFocus();
            kieSession.fireAllRules();

            if (!StringUtils.isEmpty(validationHeaderFact.getValidationMessage())) {
                logger.error(validationHeaderFact.getValidationMessage());
                throw new RuntimeException(validationHeaderFact.getValidationMessage());
            } else {
                logger.info("All required headers are present and no additional headers are included.");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowData.put(headers.get(j), cell.toString().trim());
                    }
                    dataList.add(rowData);
                }
            }

            templateDataService.insertDataIntoStaging(dataList);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading Excel file", e);
        }
    }

    public void validateCsvHeaders(String fileName) {
        CSVReader csvReader = readCSVFile(fileName);
        List<Map<String, String>> mapList = new ArrayList<>();
        try {
            List<String[]> rows = csvReader.readAll();
//            String[] headers = rows.get(0);

            String[] headers = Arrays.stream(rows.get(0))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toArray(String[]::new);


            if (headers == null) {
                throw new RuntimeException("CSV file is empty or does not have a header row.");
            }

            List<String> headerList = Arrays.stream(headers)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            ValidationHeaderRule validationHeaderRule = new ValidationHeaderRule(expectedHeaders);
            kieSession.setGlobal("validationHeaderRule", validationHeaderRule);
            ValidationHeaderFact validationHeaderFact = new ValidationHeaderFact(headerList);
            kieSession.setGlobal("validationHeaderFact", validationHeaderFact);
            kieSession.insert(validationHeaderFact);
            kieSession.getAgenda().getAgendaGroup("validateHeaders").setFocus();
            kieSession.fireAllRules();

            if (!StringUtils.isEmpty(validationHeaderFact.getValidationMessage())) {
                logger.error(validationHeaderFact.getValidationMessage());
                throw new RuntimeException(validationHeaderFact.getValidationMessage());
            } else {
                logger.info("All required headers are present and no additional headers are included.");
            }

            for (int i = 1; i < rows.size(); i++) {
                String[] values = rows.get(i);
                Map<String, String> resultMap = new HashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    if (j < values.length) {
                        resultMap.put(headers[j], values[j]);
                    }
                }
                mapList.add(resultMap);
            }

            templateDataService.insertDataIntoStaging(mapList);

        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateJsonFile(String fileName) {
        List<Map<String, String>> resultMap = new ArrayList<>();
        try {
            FileReader reader = readJsonFile(fileName);
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> dataList = mapper.readValue(reader, List.class);

            if (dataList.isEmpty()) {
                throw new RuntimeException("JSON file is empty or has invalid content.");
            }

            Set<String> headerSet = new HashSet<>();
            for (Map<String, Object> data : dataList) {
                headerSet.addAll(data.keySet());
            }
            List<String> headers = new ArrayList<>(headerSet);

            ValidationHeaderRule validationHeaderRule = new ValidationHeaderRule(expectedHeaders);
            kieSession.setGlobal("validationHeaderRule", validationHeaderRule);
            ValidationHeaderFact validationHeaderFact = new ValidationHeaderFact(headers);
            kieSession.setGlobal("validationHeaderFact", validationHeaderFact);
            kieSession.insert(validationHeaderFact);
            kieSession.getAgenda().getAgendaGroup("validateHeaders").setFocus();
            kieSession.fireAllRules();

            if (!StringUtils.isEmpty(validationHeaderFact.getValidationMessage())) {
                logger.error(validationHeaderFact.getValidationMessage());
                throw new RuntimeException(validationHeaderFact.getValidationMessage());
            } else {
                logger.info("All required headers are present and no additional headers are included.");
            }

            for (Map<String, Object> data : dataList) {
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (String header : headers) {
                    Object value = data.get(header);
                    rowMap.put(header.toLowerCase(), value != null ? value.toString() : "");
                }
                resultMap.add(rowMap);
            }
            templateDataService.saveTemplateData(resultMap);
//            templateDataService.insertDataIntoStaging(resultMap);
        } catch (Exception e) {
            throw new RuntimeException("Error in validating Json File: " + e.getMessage());
        }
    }

    public CSVReader readCSVFile(String fileName) {
        try {
            File file = new File(UPLOAD_DIRECTORY, fileName);
            FileReader fileReader = new FileReader(file);
            return new CSVReader(fileReader);
        } catch (IOException e) {
            throw new RuntimeException("Error in reading csv file: " + e.getMessage());
        }
    }

    public FileReader readJsonFile(String fileName) {
        try {
            File file = new File(UPLOAD_DIRECTORY, fileName);
            return new FileReader(file);
        } catch (IOException e) {
            throw new RuntimeException("Error in reading json file: " + e.getMessage());
        }
    }

    private Workbook readExcelFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return WorkbookFactory.create(fis);
        } catch (IOException e) {
            throw new RuntimeException("Error in reading Excel File: " + e.getMessage());
        }
    }

    public void validateFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "csv":
                validateCsvHeaders(fileName);
                break;
            case "xlsx":
                validateExcelHeaders(fileName);
                break;
            case "json":
                validateJsonFile(fileName);
                break;
            default:
                logger.info("UnKnown File Type...");
                break;
        }
    }
}
