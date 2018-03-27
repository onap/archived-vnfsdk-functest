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

package org.onap.vnfsdk.functest.responsehandler;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class TestResultParserTest {

    private TestResultParser testResParser = null;
    private UUID UUIDTask = UUID.randomUUID();

    @Before
    public void setUp() {
        testResParser = new TestResultParser();
    }

    @Test
    public void testPopulateResultList() {
        assertNotNull(testResParser.populateResultList(UUIDTask.toString(), "src/test/resources/sample.xml"));
    }

    @Test
    public void testParseResultData() {
        String taskID = "12919afc-5975-4da9-bd41-c050b305262c";
        String xmlFile = "src/test/resources/sample.xml";
        List<TestResult> resultData = new ArrayList<>();
        try {
            Class<?> resParser = Class.forName("org.onap.vnfsdk.functest.responsehandler.TestResultParser");
            Object resParserObj = resParser.newInstance();
            Method m = resParserObj.getClass().getDeclaredMethod("parseResultData", new Class[]{String.class, String.class, List.class});
            m.setAccessible(true);
            m.invoke(resParserObj, taskID, xmlFile, resultData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
