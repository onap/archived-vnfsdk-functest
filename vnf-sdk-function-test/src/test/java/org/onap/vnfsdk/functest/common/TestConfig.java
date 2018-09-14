/*
 * Copyright 2017 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.vnfsdk.functest.common;

import io.dropwizard.db.DataSourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.onap.vnfsdk.functest.VnfSdkFuncTestAppConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestConfig {

    private VnfSdkFuncTestAppConfiguration vnfSdkBean;

    @Before
    public void setUp() {
        vnfSdkBean = new VnfSdkFuncTestAppConfiguration();
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Config> constructor = Config.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testVnfSdkConfigBean() {
        vnfSdkBean.setTemplate("");
        vnfSdkBean.setServiceIp("127.0.0.1");
        vnfSdkBean.setDataSourceFactory(new DataSourceFactory());

        Config.setConfigration(vnfSdkBean);
        assertNotNull(Config.getConfigration());
    }
}
