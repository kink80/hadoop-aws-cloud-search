package com.rodrigo.hadoop.aws.operations;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.hadoop.mapred.JobConf;

import com.rodrigo.hadoop.aws.config.HadoopJobConfiguration;

import cascading.tuple.TupleEntry;

public class AddOperation implements Serializable, AwsOperation {
	
	private static final long serialVersionUID = 1L;
	private final String type;
	private final String id;
	
	private Map<String, Object> fields = new HashMap<String, Object>();
	
	public AddOperation() {
		type = "add";
		id = UUID.randomUUID().toString();
	}
	
	public void addField(String name, Object value) {
		fields.put(name, value);
	}
	
	public Map<String, Object> getFields() {
		return Collections.unmodifiableMap(fields);
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
			String fieldName = fieldMapping[1];
			
			fields.put(fieldName, tupleEntry.getObject(pos));
		}
	}

}
