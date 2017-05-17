/*
 * Copyright (c) 2016 CA. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.ca.mas;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
public abstract class MASTestBase {

    protected Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    protected <T> T getValue(Object instance, String attribute, Class<T> returnType) {
        Field field = null;
        try {
            field = instance.getClass().getDeclaredField(attribute);
            field.setAccessible(true);
            return returnType.cast(field.get(instance));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T invoke(Object instance, String methodName, Class<?>[] parameterTypes, Object[] args, Class<T> returnType) {
        Method method = null;
        try {
            method = instance.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return returnType.cast(method.invoke(instance, args));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


