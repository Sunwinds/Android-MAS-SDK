/*
 *  Copyright (c) 2016 CA. All rights reserved.
 *
 *  This software may be modified and distributed under the terms
 *  of the MIT license.  See the LICENSE file for details.
 *
 */
package com.ca.mas.foundation;

import com.ca.mas.DataSource;
import com.ca.mas.GatewayDefaultDispatcher;
import com.ca.mas.MASCallbackFuture;
import com.ca.mas.MASLoginTestBase;
import com.ca.mas.core.http.ContentType;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

public class MASJwtSigningTest extends MASLoginTestBase {

    @Test
    public void testJSONDefaultTimeoutPost() throws Exception {
        JSONObject requestData = new JSONObject();
        requestData.put("jsonName", "jsonValue");
        requestData.put("jsonName2", 1234);

        MASRequest request = new MASRequest.MASRequestBuilder(new URI(GatewayDefaultDispatcher.PROTECTED_RESOURCE_PRODUCTS))
                .post(MASRequestBody.jsonBody(requestData))
                .sign()
                .build();
        MASCallbackFuture<MASResponse<JSONObject>> callback = new MASCallbackFuture<>();
        MAS.invoke(request, callback);

        callback.get();

        RecordedRequest rr = getRecordRequest(GatewayDefaultDispatcher.PROTECTED_RESOURCE_PRODUCTS);
        String magIdentifier = rr.getHeader("mag-identifier");
        DataSource.Device device = DataSource.getInstance().getDevice(magIdentifier);
        String signedDoc = rr.getBody().readUtf8();

        JWSObject signedObject = JWSObject.parse(signedDoc);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) device.getRegisteredPublicKey());
        Assert.assertTrue(signedObject.verify(verifier));
        net.minidev.json.JSONObject payload = signedObject.getPayload().toJSONObject();
        Assert.assertEquals(requestData.toString(), payload.get("content"));
        Assert.assertEquals(payload.get("aud"), "localhost");
        // Expiry time should be equal to 5 minutes plus the specified timeout
        Assert.assertEquals((long) payload.get("iat") + 300, payload.get("exp"));
        Assert.assertEquals(payload.get("content-type"), ContentType.APPLICATION_JSON.getMimeType());
        //... assert other attribute
    }

    @Test
    public void testJSONNoTimeoutPost() throws Exception {
        JSONObject requestData = new JSONObject();
        requestData.put("jsonName", "jsonValue");
        requestData.put("jsonName2", 1234);

        MASRequest request = new MASRequest.MASRequestBuilder(new URI(GatewayDefaultDispatcher.PROTECTED_RESOURCE_PRODUCTS))
                .post(MASRequestBody.jsonBody(requestData))
                //TODO .sign(0, null)
                .build();
        MASCallbackFuture<MASResponse<JSONObject>> callback = new MASCallbackFuture<>();
        MAS.invoke(request, callback);

        callback.get();

        RecordedRequest rr = getRecordRequest(GatewayDefaultDispatcher.PROTECTED_RESOURCE_PRODUCTS);
        String magIdentifier = rr.getHeader("mag-identifier");
        DataSource.Device device = DataSource.getInstance().getDevice(magIdentifier);
        String signedDoc = rr.getBody().readUtf8();

        JWSObject signedObject = JWSObject.parse(signedDoc);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) device.getRegisteredPublicKey());
        Assert.assertTrue(signedObject.verify(verifier));
        net.minidev.json.JSONObject payload = signedObject.getPayload().toJSONObject();
        Assert.assertEquals(requestData.toString(), payload.get("content"));
        // Expiry time should be non-existent since no timeout was specified
        Assert.assertEquals(payload.get("exp"), null);
        Assert.assertEquals(payload.get("aud"), "localhost");
        Assert.assertEquals(payload.get("content-type"), ContentType.APPLICATION_JSON.getMimeType());
        //... assert other attribute
    }

    @Test
    public void testJSONCustomTimeoutSecondsPost() throws Exception {
        JSONObject requestData = new JSONObject();
        requestData.put("jsonName", "jsonValue");
        requestData.put("jsonName2", 1234);

        MASRequest request = new MASRequest.MASRequestBuilder(new URI(GatewayDefaultDispatcher.PROTECTED_RESOURCE_PRODUCTS))
                .post(MASRequestBody.jsonBody(requestData))
                //TODO .sign(500, TimeUnit.SECONDS)
                .build();
        MASCallbackFuture<MASResponse<JSONObject>> callback = new MASCallbackFuture<>();
        MAS.invoke(request, callback);

        callback.get();

        RecordedRequest rr = getRecordRequest(GatewayDefaultDispatcher.PROTECTED_RESOURCE_PRODUCTS);
        String magIdentifier = rr.getHeader("mag-identifier");
        DataSource.Device device = DataSource.getInstance().getDevice(magIdentifier);
        String signedDoc = rr.getBody().readUtf8();

        JWSObject signedObject = JWSObject.parse(signedDoc);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) device.getRegisteredPublicKey());
        Assert.assertTrue(signedObject.verify(verifier));
        net.minidev.json.JSONObject payload = signedObject.getPayload().toJSONObject();
        Assert.assertEquals(requestData.toString(), payload.get("content"));
        // Expiry time should be equal to the issued time plus the specified timeout
        Assert.assertEquals((long) payload.get("iat") + 500, payload.get("exp"));
        Assert.assertEquals(payload.get("aud"), "localhost");
        Assert.assertEquals(payload.get("content-type"), ContentType.APPLICATION_JSON.getMimeType());
        //... assert other attribute
    }
}
