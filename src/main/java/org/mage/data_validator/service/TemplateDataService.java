package org.mage.data_validator.service;

import jakarta.annotation.PostConstruct;
import org.mage.data_validator.entity.TemplateData;
import org.mage.data_validator.repository.TemplateDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TemplateDataService {

    @Value("${postgres.url}")
    private String postgresUrl;
    @Value("${postgres.user}")
    private String postgresUser;
    @Value("${postgres.password}")
    private String postgresPassword;
    @Value("${odd.postgres.url}")
    private String oddPostgresUrl;
    @Value("${odd.postgres.user}")
    private String oddPostgresUser;
    @Value("${odd.postgres.password}")
    private String oddPostgresPassword;
    @Value("${file.template_staging}")
    private String templateStagingFilePath;
    @Value("${file.insert.query}")
    private String insertQueryFilePath;
    @Value("${expected.headers}")
    private String expectedHeadersString;

    private List<String> columnOrder;
    private static final File UPLOAD_DIRECTORY = new File("uploads/");
    private static final Logger logger = LoggerFactory.getLogger(TemplateDataService.class);

    @Autowired
    private TemplateDataRepository templateDataRepository;

    @PostConstruct
    public void init() {
        this.columnOrder = Arrays.stream(StringUtils.commaDelimitedListToStringArray(expectedHeadersString))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public void saveTemplateData(List<Map<String, String>> dataList) {
        List<TemplateData> templateDataList = new ArrayList<>();
        for (Map<String, String> map : dataList) {
            TemplateData templateData = new TemplateData();
            templateData.setTemplateName(map.get("template_name"));
            templateData.setApplication(map.get("application"));
            templateData.setCreator(map.get("creator"));
            templateData.setFrozen(map.get("frozen"));
            templateData.setOwnerPath(map.get("owner/path"));
            templateData.setTableFile(map.get("table/file"));
            templateData.setTableRowCount(map.get("table_row_count"));
            templateData.setAction(map.get("action"));
            templateData.setScrambleScheme(map.get("scramble_scheme"));
            templateData.setColumnPage(map.get("column/page"));
            templateData.setDataType(map.get("datatype"));
            templateData.setDataClassification(map.get("data_classification"));
            templateData.setAnonymizationMethod(map.get("anonymization_method"));
            templateData.setAddlInfo1(map.get("addl_info1"));
            templateData.setAddlInfo2(map.get("addl_info2"));
            templateData.setAddlInfo3(map.get("addl_info3"));
            templateData.setAddlInfo4(map.get("addl_info4"));
            templateData.setMethodCode(map.get("method_code"));
            templateData.setLength(map.get("length"));
            templateData.setTableFileParameter(map.get("table_file_parameter"));
            templateData.setSubsetEnabled(map.get("subset_enabled"));
            templateData.setSubsetCondition(map.get("subset_condition"));
            templateData.setConditionalMaskingEnabled(map.get("conditional_masking_enabled"));
            templateData.setMaskingCondition(map.get("masking_condition"));
            templateDataList.add(templateData);
        }
        templateDataRepository.saveAll(templateDataList);
        logger.info("Template saved successfully");
    }


    public void connectOddDatabase() {
        String query = "SELECT schema_name FROM information_schema.schemata";
        try (Connection connection = DriverManager.getConnection(oddPostgresUrl, oddPostgresUser, oddPostgresPassword);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                logger.info(resultSet.getString("schema_name"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }



    public void ExecuteSQLFile() {
        try (Connection conn = DriverManager.getConnection(postgresUrl, postgresUser, postgresPassword);
             Statement stmt = conn.createStatement()) {

            String sql = new String(Files.readAllBytes(Paths.get(templateStagingFilePath)));
            stmt.execute(sql);

            logger.info("Table created successfully.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void insertDataIntoStaging(List<Map<String, String>> resultMap) {
        String insertSql = readSqlFromFile(insertQueryFilePath);
        if (insertSql == null || insertSql.isEmpty()) {
            System.err.println("Failed to read SQL statement from file.");
            return;
        }
        try (Connection connection = DriverManager.getConnection(postgresUrl, postgresUser, postgresPassword);
        ) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                for (Map<String, String> rowMap : resultMap) {
                    int index = 1;
                    for (String columnName : columnOrder) {
                        String value = rowMap.getOrDefault(columnName, "");
                        preparedStatement.setString(index++, value);
                    }
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String readSqlFromFile(String filePath) {
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sql.toString().trim();
    }
}
