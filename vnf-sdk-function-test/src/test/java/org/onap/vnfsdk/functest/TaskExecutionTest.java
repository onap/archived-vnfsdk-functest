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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.vnfsdk.functest.externalservice.entity.Environment;
import org.onap.vnfsdk.functest.externalservice.entity.EnvironmentMap;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.UUID;

@RunWith(PowerMockRunner.class)
public class TaskExecutionTest {

    private TaskExecution testExecution = null;
    private Environment functestEnv = null;

    private String dirPath = "src\\test\\resources\\RobotScript";
    private UUID UUIDEnv = UUID.randomUUID();
    private UUID UUIDUpload = UUID.randomUUID();
    private UUID uniqueKey = UUID.randomUUID();
    private String remoteIP = "192.168.4.47";
    private String userName = "root";
    private String password = "root123";
    private String path = "src\\test\\resources";
    private UUID envId = UUID.randomUUID();
    private UUID executeId = UUID.randomUUID();

    @Before
    public void setUp() {
        testExecution = new TaskExecution();
        functestEnv = new Environment();
    }

    @Test
    public void testExecuteScript() {
        try {
            testExecution.executeScript(dirPath, uniqueKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @PrepareForTest(EnvironmentMap.class)
    public void testExecuteRobotScript() {
        EnvironmentMap mockEnvironmentMap = PowerMockito.mock(EnvironmentMap.class);
        Whitebox.setInternalState(EnvironmentMap.class, "oInstance", mockEnvironmentMap);
        functestEnv.setRemoteIp(remoteIP);
        functestEnv.setUserName(userName);
        functestEnv.setPassword(password);
        functestEnv.setPath(path);
        PowerMockito.when(mockEnvironmentMap.getEnv(Mockito.any())).thenReturn(functestEnv);

        try {
            testExecution.executeRobotScript(envId, executeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @PrepareForTest(EnvironmentMap.class)
    public void testUploadScript() {
        EnvironmentMap mockEnvironmentMap = PowerMockito.mock(EnvironmentMap.class);
        Whitebox.setInternalState(EnvironmentMap.class, "oInstance", mockEnvironmentMap);
        functestEnv.setRemoteIp(remoteIP);
        functestEnv.setUserName(userName);
        functestEnv.setPassword(password);
        functestEnv.setPath(path);
        PowerMockito.when(mockEnvironmentMap.getEnv(Mockito.any())).thenReturn(functestEnv);

        try {
            testExecution.uploadScript(dirPath, UUIDEnv, UUIDUpload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
