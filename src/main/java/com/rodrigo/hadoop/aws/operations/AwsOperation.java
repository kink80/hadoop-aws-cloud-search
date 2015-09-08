package com.rodrigo.hadoop.aws.operations;

import org.apache.hadoop.mapred.JobConf;

import cascading.tuple.TupleEntry;

public interface AwsOperation {
	
	public String getType();
	
	public String getId();
	
	public void process(JobConf config, TupleEntry tupleEntry);
	
}
