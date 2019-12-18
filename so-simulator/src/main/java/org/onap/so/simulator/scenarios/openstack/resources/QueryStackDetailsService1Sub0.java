package org.onap.so.simulator.scenarios.openstack.resources;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.scenario.ScenarioDesigner;

@Scenario("Query-Stack-Details-service1-Sub0")
@RequestMapping(
        value = "/sim/mockPublicUrl/stacks/tsbc0005vm002ssc001-ssc_1_subint_service1_port_0_subinterfaces-dtmxjmny7yjz-0-yghihziaf36m/b7019dd0-2ee9-4447-bdef-ac25676b205a",
        method = RequestMethod.GET)
public class QueryStackDetailsService1Sub0 extends AbstractSimulatorScenario {

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.http().receive().get();

        scenario.http().send().response(HttpStatus.OK)
                .payload(new ClassPathResource("openstack/gr_api/service1SubInterface0.json"));

    }

}