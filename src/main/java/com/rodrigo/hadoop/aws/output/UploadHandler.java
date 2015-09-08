package com.rodrigo.hadoop.aws.output;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;

public class UploadHandler implements AsyncHandler<UploadDocumentsRequest, UploadDocumentsResult> {
	
	private static Log LOG = LogFactory.getLog(UploadHandler.class);
	private String uid = UUID.randomUUID().toString();
	
	private WeakReference<RestOutput<?, ?>> ref;
	
	public UploadHandler(RestOutput<?, ?> output) {
		ref = new WeakReference<RestOutput<?, ?>>(output);
	}
	
	@Override
	public void onError(Exception exception) {
		LOG.error("Update has failed", exception);
		uploadFinished();
	}

	@Override
	public void onSuccess(UploadDocumentsRequest request, UploadDocumentsResult result) {
		LOG.info(result.getAdds() + " documents added.");
		uploadFinished();
	}
	
	private void uploadFinished() {
		ref.get().signalUploadFinished(getUid());
	}
	
	public String getUid() {
		return uid;
	}
	
}
