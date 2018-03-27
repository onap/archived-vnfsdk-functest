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

package org.openo.vnfsdk.functest.db;

import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openo.vnfsdk.functest.models.CaseRecord;

import javax.persistence.PersistenceException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMgrCaseTblDAOTest {

    @Rule
    public DAOTestRule daoTestRule = DAOTestRule.newBuilder()
            .addEntityClass(CaseRecord.class)
            .build();
    private String taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a1";
    private String funcID = "";
    private String testID = "INTEL";
    private String testResult = "SUCCESS";
    private String testDescription = "";
    private TaskMgrCaseTblDAO taskMgrCaseTblDAO;

    @Before
    public void setUp() throws Exception {
        taskMgrCaseTblDAO = new TaskMgrCaseTblDAO(daoTestRule.getSessionFactory());
    }

    @Test
    public void testSaveOrUpdate() {
        final CaseRecord caseRecord = daoTestRule.inTransaction(() -> taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription)));
        assertThat(caseRecord.getTaskID()).isEqualTo("c0a1a373-8635-484d-bc6c-307a606cb8a1");
        assertThat(caseRecord.getTestID()).isEqualTo("INTEL");
        assertThat(caseRecord.getTestResult()).isEqualTo("SUCCESS");
        assertThat(taskMgrCaseTblDAO.findByTaskID(caseRecord.getTaskID()).equals(caseRecord));
    }

    @Test
    public void findAll() {
        daoTestRule.inTransaction(() -> {
            taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription));
            taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a2";
            testResult = "NOT FOUND";
            taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription));
            taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a3";
            testResult = "FAILURE";
            taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription));
        });

        final List<CaseRecord> caseRecordList = taskMgrCaseTblDAO.findAll();
        assertThat(caseRecordList).extracting("taskID").containsOnly("c0a1a373-8635-484d-bc6c-307a606cb8a1", "c0a1a373-8635-484d-bc6c-307a606cb8a2", "c0a1a373-8635-484d-bc6c-307a606cb8a3");
        assertThat(caseRecordList).extracting("testResult").containsOnly("SUCCESS", "NOT FOUND", "FAILURE");
    }

    @Test(expected = PersistenceException.class)
    public void handlesNullTaskID() {
        testID = null;
        daoTestRule.inTransaction(() -> taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription)));
    }
}
