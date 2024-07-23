package org.mage.data_validator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "template_data")
public class TemplateData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name")
    private String templateName;
    @Column(name = "application")
    private String application;
    @Column(name = "creator")
    private String creator;
    @Column(name = "frozen")
    private String frozen;
    @Column(name = "OWNER/PATH")
    private String ownerPath;
    @Column(name = "TABLE/FILE")
    private String tableFile;
    @Column(name = "table_row_count")
    private String tableRowCount;
    @Column(name = "action")
    private String action;
    @Column(name = "scramble_scheme")
    private String scrambleScheme;
    @Column(name = "COLUMN/PAGE")
    private String columnPage;
    @Column(name = "datatype")
    private String dataType;
    @Column(name = "data_classification")
    private String dataClassification;
    @Column(name = "anonymization_method")
    private String anonymizationMethod;
    @Column(name = "addl_info1")
    private String addlInfo1;
    @Column(name = "addl_info2")
    private String addlInfo2;
    @Column(name = "addl_info3")
    private String addlInfo3;
    @Column(name = "addl_info4")
    private String addlInfo4;
    @Column(name = "method_code")
    private String methodCode;
    @Column(name = "length")
    private String length;
    @Column(name = "table_file_parameter")
    private String tableFileParameter;
    @Column(name = "subset_enabled")
    private String subsetEnabled;
    @Column(name = "subset_condition")
    private String subsetCondition;
    @Column(name = "conditional_masking_enabled")
    private String conditionalMaskingEnabled;
    @Column(name = "masking_condition")
    private String maskingCondition;
    @Column(name = "validation_message")
    private String validationMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public String getOwnerPath() {
        return ownerPath;
    }

    public void setOwnerPath(String ownerPath) {
        this.ownerPath = ownerPath;
    }

    public String getTableFile() {
        return tableFile;
    }

    public void setTableFile(String tableFile) {
        this.tableFile = tableFile;
    }

    public String getTableRowCount() {
        return tableRowCount;
    }

    public void setTableRowCount(String tableRowCount) {
        this.tableRowCount = tableRowCount;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getScrambleScheme() {
        return scrambleScheme;
    }

    public void setScrambleScheme(String scrambleScheme) {
        this.scrambleScheme = scrambleScheme;
    }

    public String getColumnPage() {
        return columnPage;
    }

    public void setColumnPage(String columnPage) {
        this.columnPage = columnPage;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataClassification() {
        return dataClassification;
    }

    public void setDataClassification(String dataClassification) {
        this.dataClassification = dataClassification;
    }

    public String getAnonymizationMethod() {
        return anonymizationMethod;
    }

    public void setAnonymizationMethod(String anonymizationMethod) {
        this.anonymizationMethod = anonymizationMethod;
    }

    public String getAddlInfo1() {
        return addlInfo1;
    }

    public void setAddlInfo1(String addlInfo1) {
        this.addlInfo1 = addlInfo1;
    }

    public String getAddlInfo2() {
        return addlInfo2;
    }

    public void setAddlInfo2(String addlInfo2) {
        this.addlInfo2 = addlInfo2;
    }

    public String getAddlInfo3() {
        return addlInfo3;
    }

    public void setAddlInfo3(String addlInfo3) {
        this.addlInfo3 = addlInfo3;
    }

    public String getAddlInfo4() {
        return addlInfo4;
    }

    public void setAddlInfo4(String addlInfo4) {
        this.addlInfo4 = addlInfo4;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getTableFileParameter() {
        return tableFileParameter;
    }

    public void setTableFileParameter(String tableFileParameter) {
        this.tableFileParameter = tableFileParameter;
    }

    public String getSubsetEnabled() {
        return subsetEnabled;
    }

    public void setSubsetEnabled(String subsetEnabled) {
        this.subsetEnabled = subsetEnabled;
    }

    public String getSubsetCondition() {
        return subsetCondition;
    }

    public void setSubsetCondition(String subsetCondition) {
        this.subsetCondition = subsetCondition;
    }

    public String getConditionalMaskingEnabled() {
        return conditionalMaskingEnabled;
    }

    public void setConditionalMaskingEnabled(String conditionalMaskingEnabled) {
        this.conditionalMaskingEnabled = conditionalMaskingEnabled;
    }

    public String getMaskingCondition() {
        return maskingCondition;
    }

    public void setMaskingCondition(String maskingCondition) {
        this.maskingCondition = maskingCondition;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
}
