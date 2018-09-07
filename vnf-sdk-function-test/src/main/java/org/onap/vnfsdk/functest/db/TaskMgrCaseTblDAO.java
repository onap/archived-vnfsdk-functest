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

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.onap.vnfsdk.functest.models.CaseRecord;

import java.util.List;

public class TaskMgrCaseTblDAO extends AbstractDAO<CaseRecord> {
    public TaskMgrCaseTblDAO(SessionFactory factory) {
        super(factory);
    }

    public CaseRecord findByTaskID(String taskID) {
        return get(taskID);
    }

    public CaseRecord saveOrUpdate(CaseRecord caseRecord) {
        return persist(caseRecord);
    }

    public List<CaseRecord> findAll() {
        return list(namedQuery("org.onap.vnfsdk.functest.models.CaseRecord.findAll"));
    }

}
