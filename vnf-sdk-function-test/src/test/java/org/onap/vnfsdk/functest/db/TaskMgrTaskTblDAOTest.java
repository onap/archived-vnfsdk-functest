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

package org.onap.vnfsdk.functest.db;

import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.onap.vnfsdk.functest.models.TaskRecord;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMgrTaskTblDAOTest {
    @Rule
    public DAOTestRule daoTestRule = DAOTestRule.newBuilder()
            .addEntityClass(TaskRecord.class)
            .build();
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
    private TaskMgrTaskTblDAO taskMgrTaskTblDAO;

    @Before
    public void setUp() throws Exception {
        taskMgrTaskTblDAO = new TaskMgrTaskTblDAO(daoTestRule.getSessionFactory());
    }

    @Test
    public void testSaveOrUpdate() {
        final TaskRecord taskRecord = daoTestRule.inTransaction(() -> taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage)));
        assertThat(taskRecord.getTaskID()).isEqualTo("c0a1a373-8635-484d-bc6c-307a606cb8a1");
        assertThat(taskRecord.getStatus()).isEqualTo("CREATED");
        assertThat(taskRecord.getOperResult()).isEqualTo("SUCCESS");
        assertThat(taskMgrTaskTblDAO.findByPackageID(taskRecord.getPackageID())).isEqualTo(Optional.of(taskRecord));
        assertThat(taskMgrTaskTblDAO.findByTaskID(taskRecord.getTaskID()).get(0).equals(taskRecord)).isTrue();
    }

    @Test
    public void findAll() {
        daoTestRule.inTransaction(() -> {
            taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage));
            packageID = "1234567891";
            taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a2";
            status = "NOT FOUND";
            taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage));
            packageID = "1234567892";
            taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a3";
            status = "DONE";
            taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage));
        });

        final List<TaskRecord> taskRecordList = taskMgrTaskTblDAO.findAll();
        assertThat(taskRecordList).extracting("taskID").containsOnly("c0a1a373-8635-484d-bc6c-307a606cb8a1", "c0a1a373-8635-484d-bc6c-307a606cb8a2", "c0a1a373-8635-484d-bc6c-307a606cb8a3");
        assertThat(taskRecordList).extracting("status").containsOnly("CREATED", "NOT FOUND", "DONE");
    }

    @Test(expected = PersistenceException.class)
    public void handlesNullTaskID() {
        taskID = null;
        daoTestRule.inTransaction(() -> taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage)));
    }
}
