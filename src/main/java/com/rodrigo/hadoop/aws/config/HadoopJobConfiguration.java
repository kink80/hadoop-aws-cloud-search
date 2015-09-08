package com.rodrigo.hadoop.aws.config;

public interface HadoopJobConfiguration {
	
	String AWS_ACCESS_KEY_ID = "aws.cloudsearch.awsAccessKeyId";
	String AWS_ACCESS_KEY_SECRET = "aws.cloudsearch.awsSecretAccessKey";
	String AWS_SEARCH_DOMAIN_NAME = "aws.cloudsearch.domain.name";
	
	String BATCH_SIZE = "accumulator.batch.size";
	
	String INPUT_FILE = "input.file.path";
	
	String CONTENT_TYPE = "rest.content.type";
	
	String REST_OUTPUT_CLASS = "rest.output.class";
	String CONTENT_MAPPER_OPERATION = "rest.mapper.operation";
	String CONTENT_MAPPER_FIELDS = "rest.mapper.fields";
}
