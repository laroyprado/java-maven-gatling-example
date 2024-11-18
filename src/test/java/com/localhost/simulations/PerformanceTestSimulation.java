package com.localhost.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PerformanceTestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");


    ScenarioBuilder performanceTestScenario = scenario("Performance Test")
            .exec(http("get ping").get("/ping").check(status().is(200)))
            .pause(1, 2)
            .exec(http("post create").post("/ping/create")
                    .body(StringBody("{\"data\": \"performance test\"}")).asJson()
                    .check(status().is(201)))
            .pause(1, 3);

    {
        setUp(
                performanceTestScenario.injectOpen(
                        // Crescimento gradual para 200 usuários por segundo
                        rampUsersPerSec(1).to(200).during(Duration.ofSeconds(20)) 
                )
        ).protocols(httpProtocol);
    }
}

/* 
* 3. TESTE DE PERFORMANCE (Performance Test)
*    - Propósito: Avaliar como a aplicação responde em condições normais e com carga variada.
*    - Injeção de carga: Usuários começam em 1 usuário/segundo e aumentam até 200 usuários/segundo em 20 segundos.
*    - Resultado esperado: Verificar se a aplicação mantém os tempos de resposta aceitáveis sob diferentes condições de carga.
*/