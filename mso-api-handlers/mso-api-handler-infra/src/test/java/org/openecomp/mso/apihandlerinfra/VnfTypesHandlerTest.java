/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.mso.apihandlerinfra;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.openecomp.mso.db.catalog.CatalogDatabase;
import org.openecomp.mso.db.catalog.beans.NetworkResource;
import org.openecomp.mso.db.catalog.beans.VnfResource;

import javax.ws.rs.core.Response;

public class VnfTypesHandlerTest {
	
	VnfTypesHandler handler = new VnfTypesHandler();
	
	@Test
	public void getVnfTypesTest(){
		Response resp = handler.getVnfTypes(null, "v2");
		assertTrue(resp.getEntity().toString() != null);
	}
	
	@Test
	public void getVnfTypesTest2(){
		new MockUp<CatalogDatabase>() {
			@Mock
			public  List <VnfResource>  getAllVnfResources(){
				return null;
			}	
		};
		Response resp = handler.getVnfTypes(null, "v2");
		assertTrue(resp.getEntity().toString() != null);
	}
	
	@Test
	public void getVnfTypesTest3(){
		new MockUp<CatalogDatabase>() {
			@Mock
			public  List <VnfResource>  getAllVnfResources(){
				List <VnfResource> list = new ArrayList<VnfResource>();
				VnfResource resource = new VnfResource();
				list.add(resource);
				return list;
			}	
		};
		Response resp = handler.getVnfTypes(null, "v2");
		assertTrue(resp.getEntity().toString() != null);
	}

}
