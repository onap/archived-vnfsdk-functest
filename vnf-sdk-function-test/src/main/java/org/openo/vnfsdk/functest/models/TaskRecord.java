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

package org.openo.vnfsdk.functest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "TaskRecord")
@NamedQueries(
        {
                @NamedQuery(
                        name = "org.openo.vnfsdk.functest.models.TaskRecord.findAll",
                        query = "SELECT t FROM TaskRecord t"
                ),
                @NamedQuery(name = "org.openo.vnfsdk.functest.models.TaskRecord.findByTaskID",
                        query = "SELECT t FROM TaskRecord t WHERE t.taskID LIKE :taskID"
                )
        })
public class TaskRecord {
    @Id
    private String packageID;

    @Column(name = "taskID", nullable = false)
    private String taskID;

    @Column(name = "envID", nullable = false)
    private String envID;

    @Column(name = "uploadID", nullable = false)
    private String uploadID;

    @Column(name = "operID", nullable = false)
    private String operID;

    @Column(name = "funcID")
    private String funcID;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "operFinished", nullable = false)
    private String operFinished;

    @Column(name = "operResult", nullable = false)
    private String operResult;

    @Column(name = "operResultMessage")
    private String operResultMessage;

    public TaskRecord() {
    }

    public TaskRecord(String packageID, String taskID, String envID, String uploadID, String operID, String funcID, String status, String operFinished, String operResult, String operResultMessage) {
        this.packageID = packageID;
        this.taskID = taskID;
        this.envID = envID;
        this.uploadID = uploadID;
        this.operID = operID;
        this.funcID = funcID;
        this.status = status;
        this.operFinished = operFinished;
        this.operResult = operResult;
        this.operResultMessage = operResultMessage;
    }

    @JsonProperty
    public String getPackageID() {
        return packageID;
    }

    @JsonProperty
    public void setPackageID(String packageID) {
        this.packageID = packageID;
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
    public String getEnvID() {
        return envID;
    }

    @JsonProperty
    public void setEnvID(String envID) {
        this.envID = envID;
    }

    @JsonProperty
    public String getUploadID() {
        return uploadID;
    }

    @JsonProperty
    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }

    @JsonProperty
    public String getOperID() {
        return operID;
    }

    @JsonProperty
    public void setOperID(String operID) {
        this.operID = operID;
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
    public String getStatus() {
        return status;
    }

    @JsonProperty
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty
    public String getOperFinished() {
        return operFinished;
    }

    @JsonProperty
    public void setOperFinished(String operFinished) {
        this.operFinished = operFinished;
    }

    @JsonProperty
    public String getOperResult() {
        return operResult;
    }

    @JsonProperty
    public void setOperResult(String operResult) {
        this.operResult = operResult;
    }

    @JsonProperty
    public String getOperResultMessage() {
        return operResultMessage;
    }

    @JsonProperty
    public void setOperResultMessage(String operResultMessage) {
        this.operResultMessage = operResultMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskRecord)) {
            return false;
        }

        final TaskRecord that = (TaskRecord) o;

        return Objects.equals(this.packageID, that.packageID) &&
                Objects.equals(this.taskID, that.taskID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageID, taskID);
    }
}
