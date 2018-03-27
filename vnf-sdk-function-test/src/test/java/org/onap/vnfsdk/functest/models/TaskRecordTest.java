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

public class TaskRecordTest {

    private TaskRecord taskRecord = null;

    private String packageID = "1234567890";
    private String taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a1";
    private String envID = "0034b3f3-caea-4138-b186-e260a0cc509c";
    private String uploadID = "3dbf7978-0e5d-4fa8-ade7-bf6450b54a9b";
    private String operID = "3dbf7978-0e5d-4fa8-ade7-bf6450b54a9b";
    private String funcID = "";
    private String status = "CREATED";
    private String operFinished = "True";
    private String operResult = "SUCCESS";
    private String operResultMessage = "";

    @Before
    public void setUp() {
        taskRecord = new TaskRecord();
    }

    @Test
    public void testTaskRecord() {
        taskRecord.setPackageID(packageID);
        taskRecord.setTaskID(taskID);
        taskRecord.setEnvID(envID);
        taskRecord.setUploadID(uploadID);
        taskRecord.setOperID(operID);
        taskRecord.setFuncID(funcID);
        taskRecord.setStatus(status);
        taskRecord.setOperFinished(operFinished);
        taskRecord.setOperResult(operResult);
        taskRecord.setOperResultMessage(operResultMessage);

        assertNotNull(taskRecord);
        assertNotNull(taskRecord.getPackageID());
        assertThat(taskRecord.getTaskID(), is("c0a1a373-8635-484d-bc6c-307a606cb8a1"));
        assertNotNull(taskRecord.getOperFinished(), is("True"));
        assertNotNull(taskRecord.getOperResult(), is("SUCCESS"));
    }
}
