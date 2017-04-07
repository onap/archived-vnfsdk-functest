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

package org.openo.vnfsdk.functests;

import org.junit.Test;

import org.openo.vnfsdk.functest.VnfSdkFuncTestApp;
import org.openo.vnfsdk.functest.VnfSdkFuncTestAppConfiguration;
import org.openo.vnfsdk.functest.common.Config;
import org.openo.vnfsdk.functest.common.ServiceRegistration;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;

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
            oConfig.setMsbServerAddr("http://127.0.0.1");
            Config.setConfigration(oConfig);
            Thread registerExtsysService = new Thread(new ServiceRegistration());
            registerExtsysService.setName("Register vnfsdk-functionTest service to Microservice Bus");
            registerExtsysService.start();

        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    @Test
    public void testGetName() {   
    	assertNotNull( vnfsdkFuncApp.getName() );
    }
    
    
}
