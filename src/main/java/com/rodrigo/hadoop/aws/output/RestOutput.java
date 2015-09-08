package com.rodrigo.hadoop.aws.output;

import java.io.IOException;

import org.apache.hadoop.conf.Configurable;

public interface RestOutput<K, V> extends Configurable {
        
        public void sendData(K key, V value) throws IOException;
        
        public int getBatchSize();
        
        public void close() throws IOException;
        
        public void signalUploadFinished(String uid);
        
}