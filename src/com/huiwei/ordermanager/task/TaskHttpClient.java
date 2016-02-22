package com.huiwei.ordermanager.task;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class TaskHttpClient {
    public HttpClient client;

    public TaskHttpClient() {
    	client = new DefaultHttpClient();
    	client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
    }
}