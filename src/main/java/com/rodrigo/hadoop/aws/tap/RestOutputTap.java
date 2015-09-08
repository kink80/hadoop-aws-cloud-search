package com.rodrigo.hadoop.aws.tap;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;

import cascading.flow.FlowProcess;
import cascading.flow.SliceCounters;
import cascading.tap.SinkMode;
import cascading.tap.SinkTap;
import cascading.tap.hadoop.util.MeasuredOutputCollector;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntrySchemeCollector;

public class RestOutputTap<TargetType> extends SinkTap<JobConf, OutputCollector<Long, Serializable>> {
	
	private static final long serialVersionUID = 1L;
	private String identifier;
	
	public RestOutputTap() {
		super(new RestOutputScheme(), SinkMode.UPDATE);
		identifier = "accumulatingTap";
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}


	@Override
	public TupleEntryCollector openForWrite(FlowProcess<JobConf> flowProcess,
			OutputCollector<Long, Serializable> output) throws IOException {
		
		MeasuredOutputCollector collector = new MeasuredOutputCollector( flowProcess, SliceCounters.Write_Duration );
	    collector.setOutputCollector( output );

		return new TupleEntrySchemeCollector<JobConf, OutputCollector<?, ?>>( flowProcess, getScheme(), collector);
	}


	@Override
	public boolean createResource(JobConf conf) throws IOException {
		return true;
	}


	@Override
	public boolean deleteResource(JobConf conf) throws IOException {
		return false;
	}


	@Override
	public boolean resourceExists(JobConf conf) throws IOException {
		return true;
	}


	@Override
	public long getModifiedTime(JobConf conf) throws IOException {
		return 0;
	}
	

}
