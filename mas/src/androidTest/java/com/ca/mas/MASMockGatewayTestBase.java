/*
 * Copyright (c) 2016 CA. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.ca.mas;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ca.mas.core.io.IoUtils;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASConnectionListener;
import com.squareup.okhttp.internal.SslContextBuilder;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

@RunWith(AndroidJUnit4.class)
public abstract class MASMockGatewayTestBase extends MASTestBase {

    private static MockWebServer ssg;
    private HashMap<String, RecordedRequest> recordedRequests = new HashMap<>();
    private HashMap<String, RecordedRequest> recordRequestWithQueryParameters = new HashMap<>();
    private int requestTaken = 0;
    private GatewayDefaultDispatcher gatewayDefaultDispatcher;

    @Before
    public void startServer() throws Exception {

        ssg = new MockWebServer();
        gatewayDefaultDispatcher = new GatewayDefaultDispatcher();
        ssg.setDispatcher(gatewayDefaultDispatcher);
        ssg.useHttps(SslContextBuilder.localhost().getSocketFactory(), false);
        ssg.start(41979);

        //Turn on debug by default
        MAS.debug();

        MAS.setConnectionListener(new MASConnectionListener() {
            @Override
            public void onObtained(HttpURLConnection connection) {
                //If connect to localhost
                if (connection.getURL().getHost().equals("localhost")) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(SslContextBuilder.localhost().getSocketFactory());
                }
            }

            @Override
            public void onConnected(HttpURLConnection connection) {

            }
        });
    }

    @After
    public void shutDownServer() throws Exception {
        if (ssg != null) {
            ssg.shutdown();
        }
        recordedRequests.clear();
        recordRequestWithQueryParameters.clear();
        requestTaken = 0;
    }

    private void flushRequest() throws InterruptedException {
        int count = ssg.getRequestCount();
        count = count - requestTaken;
        for (int i = 0; i < count; i++) {
            RecordedRequest rr = ssg.takeRequest();
            Uri uri = Uri.parse(rr.getPath());
            recordedRequests.put(uri.getPath(), rr);
            recordRequestWithQueryParameters.put(rr.getPath(), rr);
        }
        requestTaken = ssg.getRequestCount();
    }

    protected RecordedRequest getRecordRequest(String path) throws InterruptedException {
        flushRequest();
        return recordedRequests.get(path);
    }

    protected RecordedRequest getRecordRequestWithQueryParameter(String url) throws InterruptedException {
        flushRequest();
        return recordRequestWithQueryParameters.get(url);
    }


    protected void setDispatcher(Dispatcher dispatcher) {
        ssg.setDispatcher(dispatcher);
    }

    protected int getPort() {
        return 41979;
    }

    protected String getHost() {
        return "localhost";
    }

}


