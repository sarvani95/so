/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 Samsung
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

package org.onap.so.bpmn.infrastructure.sdnc.tasks;

import org.onap.sdnc.northbound.client.model.GenericResourceApiNetworkOperationInformation;
import org.onap.sdnc.northbound.client.model.GenericResourceApiServiceOperationInformation;
import org.onap.sdnc.northbound.client.model.GenericResourceApiVfModuleOperationInformation;
import org.onap.sdnc.northbound.client.model.GenericResourceApiVnfOperationInformation;
import org.onap.so.bpmn.common.BuildingBlockExecution;
import org.onap.so.bpmn.servicedecomposition.bbobjects.CloudRegion;
import org.onap.so.bpmn.servicedecomposition.bbobjects.Customer;
import org.onap.so.bpmn.servicedecomposition.bbobjects.GenericVnf;
import org.onap.so.bpmn.servicedecomposition.bbobjects.L3Network;
import org.onap.so.bpmn.servicedecomposition.bbobjects.ServiceInstance;
import org.onap.so.bpmn.servicedecomposition.bbobjects.VfModule;
import org.onap.so.bpmn.servicedecomposition.bbobjects.VolumeGroup;
import org.onap.so.bpmn.servicedecomposition.entities.GeneralBuildingBlock;
import org.onap.so.bpmn.servicedecomposition.entities.ResourceKey;
import org.onap.so.bpmn.servicedecomposition.generalobjects.RequestContext;
import org.onap.so.bpmn.servicedecomposition.tasks.ExtractPojosForBB;
import org.onap.so.client.exception.BBObjectNotFoundException;
import org.onap.so.client.exception.ExceptionBuilder;
import org.onap.so.client.orchestration.SDNCNetworkResources;
import org.onap.so.client.orchestration.SDNCServiceInstanceResources;
import org.onap.so.client.orchestration.SDNCVfModuleResources;
import org.onap.so.client.orchestration.SDNCVnfResources;
import org.onap.so.client.sdnc.beans.SDNCRequest;
import org.onap.so.client.sdnc.endpoint.SDNCTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/*
 * This class is used for creating the service instance, assigning vnf, assigning the Vfmodule & assigning the L3Network
 * on SDNC.
 */
@Component
public class SDNCAssignTasks extends AbstractSDNCTask {
    private static final Logger logger = LoggerFactory.getLogger(SDNCAssignTasks.class);
    public static final String SDNC_REQUEST = "SDNCRequest";
    @Autowired
    private SDNCServiceInstanceResources sdncSIResources;
    @Autowired
    private SDNCVnfResources sdncVnfResources;
    @Autowired
    private SDNCVfModuleResources sdncVfModuleResources;
    @Autowired
    private ExceptionBuilder exceptionUtil;
    @Autowired
    private ExtractPojosForBB extractPojosForBB;
    @Autowired
    private SDNCNetworkResources sdncNetworkResources;
    @Autowired
    private Environment env;

    /**
     * BPMN access method to assigning the service instance in SDNC.
     *
     * It will assign the service instance by the service instance id.
     *
     * @param execution
     */
    public void assignServiceInstance(BuildingBlockExecution execution) {
        try {
            GeneralBuildingBlock gBBInput = execution.getGeneralBuildingBlock();
            RequestContext requestContext = gBBInput.getRequestContext();
            ServiceInstance serviceInstance =
                    extractPojosForBB.extractByKey(execution, ResourceKey.SERVICE_INSTANCE_ID);
            Customer customer = gBBInput.getCustomer();
            GenericResourceApiServiceOperationInformation req =
                    sdncSIResources.assignServiceInstance(serviceInstance, customer, requestContext);
            SDNCRequest sdncRequest = new SDNCRequest();
            sdncRequest.setSDNCPayload(req);
            sdncRequest.setTopology(SDNCTopology.SERVICE);
            execution.setVariable(SDNC_REQUEST, sdncRequest);
        } catch (Exception ex) {
            logger.error("Exception occurred", ex);
            exceptionUtil.buildAndThrowWorkflowException(execution, 7000, ex);
        }
    }

    /**
     * BPMN access method to assigning the vnf in SDNC.
     *
     * It will assign the vnf according to the service instance id and vnf id.
     *
     * @param execution
     */
    public void assignVnf(BuildingBlockExecution execution) {
        try {
            GeneralBuildingBlock gBBInput = execution.getGeneralBuildingBlock();
            RequestContext requestContext = gBBInput.getRequestContext();
            ServiceInstance serviceInstance =
                    extractPojosForBB.extractByKey(execution, ResourceKey.SERVICE_INSTANCE_ID);
            GenericVnf vnf = extractPojosForBB.extractByKey(execution, ResourceKey.GENERIC_VNF_ID);
            Customer customer = gBBInput.getCustomer();
            CloudRegion cloudRegion = gBBInput.getCloudRegion();
            SDNCRequest sdncRequest = new SDNCRequest();
            GenericResourceApiVnfOperationInformation req =
                    sdncVnfResources.assignVnf(vnf, serviceInstance, customer, cloudRegion, requestContext,
                            Boolean.TRUE.equals(vnf.isCallHoming()), buildCallbackURI(sdncRequest));
            sdncRequest.setSDNCPayload(req);
            sdncRequest.setTopology(SDNCTopology.VNF);
            execution.setVariable(SDNC_REQUEST, sdncRequest);
        } catch (Exception ex) {
            logger.error("Exception occurred", ex);
            exceptionUtil.buildAndThrowWorkflowException(execution, 7000, ex);
        }
    }


    /**
     * BPMN access method to assigning the vfModule in SDNC.
     *
     * It will assign the VfModule by the service instance id ,Vnf id and module id.
     *
     * @param execution
     */
    public void assignVfModule(BuildingBlockExecution execution) {
        try {
            GeneralBuildingBlock gBBInput = execution.getGeneralBuildingBlock();
            RequestContext requestContext = gBBInput.getRequestContext();
            ServiceInstance serviceInstance =
                    extractPojosForBB.extractByKey(execution, ResourceKey.SERVICE_INSTANCE_ID);
            GenericVnf vnf = extractPojosForBB.extractByKey(execution, ResourceKey.GENERIC_VNF_ID);
            VfModule vfModule = extractPojosForBB.extractByKey(execution, ResourceKey.VF_MODULE_ID);
            VolumeGroup volumeGroup = null;
            try {
                volumeGroup = extractPojosForBB.extractByKey(execution, ResourceKey.VOLUME_GROUP_ID);
            } catch (BBObjectNotFoundException e) {
                logger.info("No volume group was found.");
            }
            Customer customer = gBBInput.getCustomer();
            CloudRegion cloudRegion = gBBInput.getCloudRegion();
            SDNCRequest sdncRequest = new SDNCRequest();
            GenericResourceApiVfModuleOperationInformation req =
                    sdncVfModuleResources.assignVfModule(vfModule, volumeGroup, vnf, serviceInstance, customer,
                            cloudRegion, requestContext, buildCallbackURI(sdncRequest));
            sdncRequest.setSDNCPayload(req);
            sdncRequest.setTopology(SDNCTopology.VFMODULE);
            execution.setVariable(SDNC_REQUEST, sdncRequest);
        } catch (Exception ex) {
            logger.error("Exception occurred", ex);
            exceptionUtil.buildAndThrowWorkflowException(execution, 7000, ex);
        }
    }

    /**
     * BPMN access method to perform Assign action on SDNC for L3Network
     * 
     * @param execution
     * @throws Exception
     */
    public void assignNetwork(BuildingBlockExecution execution) {
        try {
            GeneralBuildingBlock gBBInput = execution.getGeneralBuildingBlock();
            L3Network l3network = extractPojosForBB.extractByKey(execution, ResourceKey.NETWORK_ID);
            ServiceInstance serviceInstance =
                    extractPojosForBB.extractByKey(execution, ResourceKey.SERVICE_INSTANCE_ID);
            Customer customer = gBBInput.getCustomer();
            RequestContext requestContext = gBBInput.getRequestContext();
            CloudRegion cloudRegion = gBBInput.getCloudRegion();
            GenericResourceApiNetworkOperationInformation req = sdncNetworkResources.assignNetwork(l3network,
                    serviceInstance, customer, requestContext, cloudRegion);
            SDNCRequest sdncRequest = new SDNCRequest();
            sdncRequest.setSDNCPayload(req);
            sdncRequest.setTopology(SDNCTopology.NETWORK);
            execution.setVariable(SDNC_REQUEST, sdncRequest);
        } catch (Exception ex) {
            exceptionUtil.buildAndThrowWorkflowException(execution, 7000, ex);
        }
    }
}
