package com.localhost.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class StressTestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");

    // Cenário de Teste de Stress
    ScenarioBuilder stressTestScenario = scenario("Stress Test")
            .exec(http("get ping").get("/ping").check(status().is(200)))
            .pause(0, 1)
            .exec(http("post create").post("/ping/create")
                    .body(StringBody("{\"data\": \"stress test\"}")).asJson()
                    .check(status().is(201)))
            .pause(0, 2);

    {
        setUp(
                stressTestScenario.injectOpen(
                        // 500 usuários fixos por segundo
                        constantUsersPerSec(500).during(Duration.ofMinutes(1)) 
                )
        ).protocols(httpProtocol);
    }
}

/* 
* 2. TESTE DE STRESS (Stress Test)
     *    - Propósito: Colocar a aplicação sob uma carga muito alta para determinar o limite de capacidade do sistema.
     *    - Injeção de carga: 500 usuários/segundo constantes durante 1 minuto.
     *    - Resultado esperado: Descobrir em que ponto o sistema falha completamente ou fica instável.
     * */