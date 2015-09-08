package com.rodrigo.hadoop.aws;

import org.junit.Before;
import org.junit.Test;

import com.rodrigo.hadoop.aws.config.ConfigReader;
import com.rodrigo.hadoop.aws.config.HadoopJobConfiguration;
import com.rodrigo.hadoop.aws.operations.AddOperation;
import com.rodrigo.hadoop.aws.tap.RestOutputTap;

import cascading.flow.FlowDef;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.pipe.Pipe;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.Tap;
import cascading.tap.hadoop.Lfs;

import java.io.IOException;
import java.util.Properties;

public class ProxyIndexTest {

    @Before
    public void doNotCareAboutOsStuff() {
        System.setProperty("line.separator", "\n");
    }

    @Test
    public void testMain() throws IOException {
        Properties properties = new ConfigReader().renderProperties(ProxyIndexTest.class);
        FlowDef flowDef = buildProxyDef(properties);

        new HadoopFlowConnector(properties).connect(flowDef).complete();
    }
    
    public static FlowDef buildProxyDef(Properties properties){
        String inPath =  properties.getProperty(HadoopJobConfiguration.INPUT_FILE);
        Tap source = new Lfs(new TextLine(), inPath);

        // create the "sink" (output) tap that will export the data to AWS Cloud Search
        Tap sink = new RestOutputTap<AddOperation>();

        //Build the Cascading Flow Definition
        return createCommonCrawlFlowDefWet(source, sink);
    }
    
    public static FlowDef createCommonCrawlFlowDefWet(Tap source, Tap sink) {
        Pipe parsePipe = new Pipe( "testPipe" );
        
        return FlowDef.flowDef()
                .addSource( parsePipe, source )
                .addTailSink( parsePipe, sink );
    }

}

