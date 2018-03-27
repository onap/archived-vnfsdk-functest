/*
 * Copyright (c) 2018 Intel Corporation Intellectual Property
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

package org.onap.vnfsdk.functest.models;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CaseRecordTest {

    private CaseRecord caseRecord = null;

    private String taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a1";
    private String funcID = "";
    private String testID = "INTEL";
    private String testResult = "SUCCESS";
    private String testDescription = "";

    @Before
    public void setUp() {
        caseRecord = new CaseRecord();
    }

    @Test
    public void testCaseRecord() {
        caseRecord.setTaskID(taskID);
        caseRecord.setFuncID(funcID);
        caseRecord.setTestID(testID);
        caseRecord.setTestResult(testResult);
        caseRecord.setTestDescription(testDescription);

        assertNotNull(caseRecord);
        assertNotNull(caseRecord.getTestID());
        assertThat(caseRecord.getTestID(), is("INTEL"));
        assertNotNull(caseRecord.getTestResult(), is("SUCCESS"));
        assertNotNull(caseRecord.getTestDescription(), is(""));
    }
}
