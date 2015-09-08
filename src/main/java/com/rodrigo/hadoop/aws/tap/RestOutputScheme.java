package com.rodrigo.hadoop.aws.tap;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.rodrigo.hadoop.aws.config.HadoopJobConfiguration;
import com.rodrigo.hadoop.aws.format.RestOutputFormat;
import com.rodrigo.hadoop.aws.operations.AwsOperation;
import com.rodrigo.hadoop.aws.operations.OperationFactory;
import com.rodrigo.hadoop.aws.operations.OperationFactory.OperationType;

import cascading.flow.FlowProcess;
import cascading.scheme.Scheme;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.tap.Tap;
import cascading.tuple.TupleEntry;

public class RestOutputScheme extends Scheme<JobConf, Void, OutputCollector<Long, Serializable>, Void, Class<? extends Serializable>> {
	
	private static final long serialVersionUID = 1L;
	private OperationFactory opsFactory = new OperationFactory();
	
	@Override
	public boolean isSource() {
		return false;
    }
	
	@Override
	public boolean source(FlowProcess<JobConf> flowProcess, SourceCall<Void, Void> sourceCall)
			throws IOException {
		return false;
	}
	
	@Override
	public void sinkPrepare( FlowProcess<JobConf> flowProcess, SinkCall<Class<? extends Serializable>, OutputCollector<Long, Serializable>> sinkCall ) throws IOException {
	}
	
	
	
	@Override
	public void sink(FlowProcess<JobConf> flowProcess,
			SinkCall<Class<? extends Serializable>, OutputCollector<Long, Serializable>> sinkCall) throws IOException {
		TupleEntry tupleEntry = sinkCall.getOutgoingEntry();
		Long position = (Long) tupleEntry.getObject(0);
		
		JobConf config = flowProcess.getConfigCopy();
		
		OperationType type = OperationFactory.OperationType.valueOf(config.get(HadoopJobConfiguration.CONTENT_MAPPER_OPERATION));
		AwsOperation operation = opsFactory.getOperation(type);
		operation.process(config, tupleEntry);
		
	    sinkCall.getOutput().collect(position, (Serializable) operation);
	}

	@Override
	public void sourceConfInit(FlowProcess<JobConf> flowProcess, Tap<JobConf, Void, OutputCollector<Long, Serializable>> tap, JobConf conf) {
		
	}

	@Override
	public void sinkConfInit(FlowProcess<JobConf> flowProcess, Tap<JobConf, Void, OutputCollector<Long, Serializable>> tap, JobConf conf) {
		conf.setOutputFormat(RestOutputFormat.class);
		
		Path outputDir = new Path("job");
		try {
			outputDir = outputDir.getFileSystem(conf).makeQualified(outputDir);
		} catch (IOException e) {
			// Throw the IOException as a RuntimeException to be compatible with MR1
			throw new RuntimeException(e);
		}
		conf.set(FileOutputFormat.OUTDIR, outputDir.toString());
	}

}
