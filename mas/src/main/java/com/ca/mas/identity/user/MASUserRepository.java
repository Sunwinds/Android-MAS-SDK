/*
 * Copyright (c) 2016 CA. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.ca.mas.identity.user;

import android.graphics.Bitmap;

import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.identity.common.MASFilteredRequest;

import java.util.List;

/**
 * The <b>MASUserIdentity</b> interface enables the Identity Management feature for the authenticated user.
 */
public interface MASUserRepository extends MASUserIdentity {

    void me(MASCallback<MASUser> callback);

}
