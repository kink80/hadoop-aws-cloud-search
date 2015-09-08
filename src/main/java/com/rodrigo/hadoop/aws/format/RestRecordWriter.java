package com.rodrigo.hadoop.aws.format;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;

import com.rodrigo.hadoop.aws.config.HadoopJobConfiguration;
import com.rodrigo.hadoop.aws.output.RestOutput;

public class RestRecordWriter<K, V> implements RecordWriter<K, V> {
	
	private RestOutput<K, V> output;
	private final JobConf config;
	
	public RestRecordWriter(JobConf config) {
		this.config = config;
	}

	@Override
	public void close(Reporter reporter) throws IOException {
		output.close();
	}

	@Override
	public void write(K key, V value) throws IOException {
		if ( output == null ) {
			init();
		}
		output.sendData(key, value);
	}
	
	@SuppressWarnings("unchecked")
	private void init() throws IOException {
		String restClass = config.get(HadoopJobConfiguration.REST_OUTPUT_CLASS);
		Class<RestOutput<K, V>> clazz = (Class<RestOutput<K, V>>) config.getClassByNameOrNull(restClass);
		if ( clazz == null ) {
			throw new IOException("Class " + restClass + " not available");
		}
		output = ReflectionUtils.newInstance(clazz, config);
	}

}
