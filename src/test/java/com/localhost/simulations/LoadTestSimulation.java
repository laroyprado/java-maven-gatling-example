package com.localhost.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadTestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");

    // Cenário de Teste de Carga
    ScenarioBuilder loadTestScenario = scenario("Load Test")
            .exec(http("get ping").get("/ping").check(status().is(200)))
            .pause(1, 2)
            .exec(http("post create").post("/ping/create")
                    .body(StringBody("{\"data\": \"load test\"}")).asJson()
                    .check(status().is(201)))
            .pause(1, 3);

    {
        setUp(
                loadTestScenario.injectOpen(
                        // Aumentando de 1 a 100 usuários por segundo
                        rampUsersPerSec(1).to(100).during(Duration.ofSeconds(30)) 
                )
        ).protocols(httpProtocol);
    }
}

/*
* 1. TESTE DE CARGA (Load Test)
     *    - Propósito: Avaliar como a aplicação se comporta ao aumentar gradativamente o número de usuários.
     *    - Injeção de carga: Usuários começam em 1 usuário/segundo e aumentam linearmente até 100 usuários/segundo em 30 segundos.
     *    - Resultado esperado: Identificar o ponto em que o sistema começa a apresentar atrasos significativos ou falhas.
     * */