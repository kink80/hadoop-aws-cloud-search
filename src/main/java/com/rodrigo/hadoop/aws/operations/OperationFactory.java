package com.rodrigo.hadoop.aws.operations;

import java.io.Serializable;

import com.rodrigo.hadoop.aws.operations.AddOperation;
import com.rodrigo.hadoop.aws.operations.AwsOperation;

public class OperationFactory implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum OperationType {
		ADD, DELETE
	}
	
	public AwsOperation getOperation(OperationType type) {
		switch ( type ) {
			case ADD: return new AddOperation();
			case DELETE: return new DeleteOperation();
		}
		
		return null;
	}

}
