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

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "CaseRecord")
@NamedQueries(
        {
                @NamedQuery(
                        name = "org.onap.vnfsdk.functest.models.CaseRecord.findAll",
                        query = "SELECT c FROM CaseRecord c"
                )
        })
public class CaseRecord {
    @Id
    private String taskID;

    @Column(name = "funcID")
    private String funcID;

    @Column(name = "testID", nullable = false)
    private String testID;

    @Column(name = "testResult", nullable = false)
    private String testResult;

    @Column(name = "testDescription", nullable = false)
    private String testDescription;

    public CaseRecord() {
    }

    public CaseRecord(String taskID, String funcID, String testID, String testResult, String testDescription) {
        this.taskID = taskID;
        this.funcID = funcID;
        this.testID = testID;
        this.testResult = testResult;
        this.testDescription = testDescription;
    }

    @JsonProperty
    public String getTaskID() {
        return taskID;
    }

    @JsonProperty
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    @JsonProperty
    public String getFuncID() {
        return funcID;
    }

    @JsonProperty
    public void setFuncID(String funcID) {
        this.funcID = funcID;
    }

    @JsonProperty
    public String getTestID() {
        return testID;
    }

    @JsonProperty
    public void setTestID(String testID) {
        this.testID = testID;
    }

    @JsonProperty
    public String getTestResult() {
        return testResult;
    }

    @JsonProperty
    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    @JsonProperty
    public String getTestDescription() {
        return testDescription;
    }

    @JsonProperty
    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaseRecord)) {
            return false;
        }

        final CaseRecord that = (CaseRecord) o;

        return Objects.equals(this.taskID, that.taskID);
    }
}
