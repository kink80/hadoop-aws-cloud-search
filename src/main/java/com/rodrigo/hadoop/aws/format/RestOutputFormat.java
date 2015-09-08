package com.rodrigo.hadoop.aws.format;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;

public class RestOutputFormat<K, V> implements OutputFormat<K, V> {
	
	@Override
	public RecordWriter<K, V> getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress)
			throws IOException {
		return new RestRecordWriter<K, V>(job);
	}

	@Override
	public void checkOutputSpecs(FileSystem ignored, JobConf job) throws IOException {
		
	}

}
