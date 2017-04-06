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

package org.openo.vnfsdk.functest.externalservice.entity;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class EnvironmentTest {
	private Environment environment = null;
	
	@Before
	public void setUp() {
		environment = new Environment();
	}	
	
	@Test
	public void testEnvironment() {
		environment.setRemoteIp( "192.168.4.47" );
		environment.setUserName( "root" );
		environment.setPassword( "root123" );
		environment.setPath( "src\\test\\resources" );
		
		assertNotNull( environment );
		assertNotNull( environment.getRemoteIp() );
		assertNotNull( environment.getUserName() );
		assertNotNull( environment.getPassword() );
		assertNotNull( environment.getPath() );
	}	
}
