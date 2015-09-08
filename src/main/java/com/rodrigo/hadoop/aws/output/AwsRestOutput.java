package com.rodrigo.hadoop.aws.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainAsyncClient;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchAsyncClient;
import com.amazonaws.services.cloudsearchv2.model.DescribeDomainsRequest;
import com.amazonaws.services.cloudsearchv2.model.DescribeDomainsResult;
import com.amazonaws.services.cloudsearchv2.model.DomainStatus;
import com.amazonaws.util.StringInputStream;
import com.google.gson.Gson;
import com.rodrigo.hadoop.aws.config.HadoopJobConfiguration;

public class AwsRestOutput<K, V> implements RestOutput<K, V> {
	
	private static Log LOG = LogFactory.getLog(AwsRestOutput.class);
	
	private AmazonCloudSearchDomainAsyncClient uploadClient;
	private Gson gson = new Gson();
	
	private String id;
	private String pass;
	private String domainName;
	private String contentType;
	private int batchSize;
	
	private Map<String, Future<UploadDocumentsResult>> runningUploads = Collections.synchronizedMap(new HashMap<String, Future<UploadDocumentsResult>>());
	
	private Configuration config;
	
	private List<V> buffer = new ArrayList<V>();
	private int size = 0;

	private void init() throws IOException {
		AmazonCloudSearchAsyncClient client = new AmazonCloudSearchAsyncClient(new BasicAWSCredentials(id, pass));
		DescribeDomainsResult domains = client.describeDomains(new DescribeDomainsRequest().withDomainNames(domainName));
		List<DomainStatus> domainsStatuses = domains.getDomainStatusList();
		
		
		if ( !domainsStatuses.isEmpty() ) {
			DomainStatus status = domainsStatuses.get(0);
			String endpoint = status.getDocService().getEndpoint();
			
			uploadClient = new AmazonCloudSearchDomainAsyncClient();
			uploadClient.setEndpoint(endpoint);
		}
		
	}
	
	@Override
	public void sendData(K key, V value) throws IOException {
		if ( uploadClient == null ) {
			init();
		}
		
		if ( uploadClient == null ) {
			throw new IOException(" AWS Cloud search client could not be established.");
		}
		String stringData = gson.toJson(value);
		size += stringData.length();
		buffer.add(value);
		
		if ( size >= getBatchSize() ) {
			sendData();
		}
	}
	
	@Override
	public int getBatchSize() {
		return batchSize;
	}

	@Override
	public void close() throws IOException {
		if ( size > 0 ) {
			// Send the last batch
			LOG.info("Sending a batch before closing the output");
			sendData();
		}
		
		waitForUploads();
		
		if ( uploadClient != null ) {
			uploadClient.shutdown();
		}
	}

	@Override
	public void setConf(org.apache.hadoop.conf.Configuration conf) {
		this.id = conf.get(HadoopJobConfiguration.AWS_ACCESS_KEY_ID);
		this.pass = conf.get(HadoopJobConfiguration.AWS_ACCESS_KEY_SECRET);
		this.domainName = conf.get(HadoopJobConfiguration.AWS_SEARCH_DOMAIN_NAME);
		this.contentType = conf.get(HadoopJobConfiguration.CONTENT_TYPE);
		this.batchSize = conf.getInt(HadoopJobConfiguration.BATCH_SIZE, 10000);
		
		config = conf;
	}

	@Override
	public Configuration getConf() {
		return config;
	}
	
	private void waitForUploads() throws IOException {
		while ( !runningUploads.isEmpty() ) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
	}
	
	private void sendData() throws IOException {
		// Make request, serialize, etc.
		String out = gson.toJson(buffer);

		try {
			UploadDocumentsRequest uploadRequest = new UploadDocumentsRequest();
			uploadRequest.setContentType(contentType);
			uploadRequest.setContentLength(Long.valueOf(out.length()));
			uploadRequest.setDocuments(new StringInputStream(out));
			
			UploadHandler handler = new UploadHandler(this);
			runningUploads.put(handler.getUid(), uploadClient.uploadDocumentsAsync(uploadRequest, handler));
		} finally {
			buffer.clear();
			size = 0;
		}
	}

	@Override
	public void signalUploadFinished(String uid) {
		runningUploads.remove(uid);
	}

}
