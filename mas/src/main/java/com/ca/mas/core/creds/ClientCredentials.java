/*
 * Copyright (c) 2016 CA. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.ca.mas.core.creds;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.util.Pair;

import com.ca.mas.core.context.MssoContext;

import java.util.List;
import java.util.Map;

/**
 * Credentials for Client Credentials Grant Type
 */
@SuppressLint("ParcelCreator")
public class ClientCredentials implements Credentials {

    @Override
    public void clear() {

    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Map<String, List<String>> getHeaders(MssoContext context) {
        return null;
    }

    @Override
    public List<Pair<String, String>> getParams(MssoContext context) {
        return null;
    }

    @Override
    public String getGrantType() {
        return "client_credentials";
    }

    @Override
    public String getUsername() {
        return "clientName";
    }

    @Override
    public boolean isReuseable() {
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
