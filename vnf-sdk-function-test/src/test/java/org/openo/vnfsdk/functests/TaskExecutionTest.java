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

package org.openo.vnfsdk.functests;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import org.openo.vnfsdk.functest.TaskExecution;
import org.openo.vnfsdk.functest.externalservice.entity.Environment;

import org.openo.vnfsdk.functest.externalservice.entity.EnvironmentMap;

import mockit.Mock;
import mockit.MockUp;

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
	private UUID uploadId = UUID.randomUUID();
	private UUID executeId = UUID.randomUUID();
	private String frameworkType = "robotframework";
	
	
	@Before
	public void setUp() {
		testExecution = new TaskExecution();
		functestEnv = new Environment();
	}
	
	@Test
	public void testExecuteScript() {
		try {
			testExecution.executeScript( dirPath, uniqueKey );
		} catch( Exception e ) {
			e.printStackTrace();
		}		
	}
	
	@Test
	public void testExecuteRobotScript() {
		new MockUp<EnvironmentMap>() {
            @Mock
            public synchronized Environment getEnv( UUID uuid ) {   
            	functestEnv.setRemoteIp( remoteIP );
            	functestEnv.setUserName( userName );
            	functestEnv.setPassword( password );
            	functestEnv.setPath( path );
                return functestEnv;           	
            }
        };
        try {
        	testExecution.executeRobotScript(envId, uploadId, executeId, frameworkType );
        } catch( Exception e ) {
        	e.printStackTrace();
        }        
	}	
	
	@Test
	public void testUploadScript() {
		 new MockUp<EnvironmentMap>() {
	            @Mock
	            public synchronized Environment getEnv( UUID uuid ) {   
	            	functestEnv.setRemoteIp( remoteIP );
	            	functestEnv.setUserName( userName );
	            	functestEnv.setPassword( password );
	            	functestEnv.setPath( path );
	                return functestEnv;           	
	            }
	        };
	        try {
	        	testExecution.uploadScript( dirPath, UUIDEnv, UUIDUpload );
	        } catch( Exception e ) {
	        	e.printStackTrace();
	        }	
	}
		
	
}
