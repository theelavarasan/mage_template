-- create_template_staging.sql

-- This script creates the template_staging with the specified columns

CREATE TABLE IF NOT EXISTS template_staging (
    "template_name" VARCHAR,
    "application" VARCHAR,
    "creator" VARCHAR,
    "frozen" VARCHAR,
    "OWNER/PATH" VARCHAR,  -- Column with special characters
    "TABLE/FILE" VARCHAR,
    "table_row_count" VARCHAR,
    "action" VARCHAR,
    "scramble_scheme" VARCHAR,
    "COLUMN/PAGE" VARCHAR,
    "datatype" VARCHAR,
    "data_classification" VARCHAR,
    "anonymization_method" VARCHAR,
    "addl_info1" VARCHAR,
    "addl_info2" VARCHAR,
    "addl_info3" VARCHAR,
    "addl_info4" VARCHAR,
    "method_code" VARCHAR,
    "length" VARCHAR,
    "table_file_parameter" VARCHAR,
    "subset_enabled" VARCHAR,
    "subset_condition" VARCHAR,
    "conditional_masking_enabled" VARCHAR,
    "masking_condition" VARCHAR,
    "validation_message" VARCHAR
);

