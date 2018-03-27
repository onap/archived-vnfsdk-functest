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

package org.openo.vnfsdkfunctest.responsehandler;

import org.junit.Before;
import org.junit.Test;
import org.openo.vnfsdk.functest.responsehandler.TestResultParser;

import java.lang.reflect.Method;
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
        try {
            Class<?> resParser = Class.forName("TestResultParser");
            Object serviceRegObj = resParser.newInstance();
            Method m = ((Class<?>) serviceRegObj).getDeclaredMethod("threadSleep", new Class[]{String.class});
            m.setAccessible(true);
            m.invoke(serviceRegObj, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
