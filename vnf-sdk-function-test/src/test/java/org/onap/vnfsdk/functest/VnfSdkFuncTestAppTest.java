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

package org.onap.vnfsdk.functest;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.onap.vnfsdk.functest.common.Config;

import static org.junit.Assert.assertNotNull;

public class VnfSdkFuncTestAppTest {

    private VnfSdkFuncTestApp vnfSdkFuncTestApp;

    private Environment environment;

    private Application<VnfSdkFuncTestAppConfiguration> vnfsdkFuncApp;

    @Before
    public void setUp() {
        vnfsdkFuncApp = new VnfSdkFuncTestApp();
    }

    @Test
    public void RunApp() {

        try {

            VnfSdkFuncTestAppConfiguration oConfig = new VnfSdkFuncTestAppConfiguration();
            Config.setConfigration(oConfig);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testGetName() {
        assertNotNull(vnfsdkFuncApp.getName());
    }

}
