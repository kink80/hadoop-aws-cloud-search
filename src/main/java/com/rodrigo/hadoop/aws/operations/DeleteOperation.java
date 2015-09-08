package com.rodrigo.hadoop.aws.operations;

import java.util.Collection;

import org.apache.hadoop.mapred.JobConf;

import com.rodrigo.hadoop.aws.config.HadoopJobConfiguration;

import cascading.tuple.TupleEntry;

public class DeleteOperation implements AwsOperation {
	
	private final String type;
	private String id;
	
	public DeleteOperation() {
		type = "delete";
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void process(JobConf config, TupleEntry tupleEntry) {
		Collection<String> mapping = config.getStringCollection(HadoopJobConfiguration.CONTENT_MAPPER_FIELDS);
		for ( String fieldMap: mapping ) {
			String[] fieldMapping = fieldMap.split(":");
			int pos = Integer.valueOf(fieldMapping[0]);
			
			id = String.valueOf(tupleEntry.getObject(pos));
		}
	}

}
