package com.localhost.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PingSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");

    // Cenário básico com GET, POST e PUT
    ScenarioBuilder scenario = scenario("PingSimulation")
            .exec(http("get ping")
                    .get("/ping")
                    .check(status().is(200)))
            .pause(1, 3)
            .exec(http("post create")
                    .post("/ping/create")
                    .body(StringBody("{\"data\": \"test\"}")).asJson()
                    .check(status().is(201)))
            .pause(2, 5)
            .exec(http("put update")
                    .put("/ping/update")
                    .body(StringBody("{\"data\": \"update\"}")).asJson()
                    .check(status().is(200)))
            .pause(1, 2)
            .exec(http("get ping again")
                    .get("/ping")
                    .check(status().is(200)));

    // Teste de Carga (Load Test)
    ScenarioBuilder loadTestScenario = scenario("Load Test")
            .exec(http("get ping").get("/ping").check(status().is(200)))  // Adiciona uma requisição para o cenário de carga
            .pause(1, 2)
            .exec(http("post create").post("/ping/create")
                    .body(StringBody("{\"data\": \"load test\"}")).asJson()
                    .check(status().is(201)))
            .pause(1, 3);

    // Teste de Stress (Stress Test)
    ScenarioBuilder stressTestScenario = scenario("Stress Test")
            .exec(http("get ping").get("/ping").check(status().is(200)))  // Adiciona uma requisição para o cenário de stress
            .pause(0, 1)
            .exec(http("post create").post("/ping/create")
                    .body(StringBody("{\"data\": \"stress test\"}")).asJson()
                    .check(status().is(201)))
            .pause(0, 2);

    // Teste de Performance (Performance Test)
    ScenarioBuilder performanceTestScenario = scenario("Performance Test")
            .exec(http("get ping").get("/ping").check(status().is(200)))  // Adiciona uma requisição para o cenário de performance
            .pause(1, 2)
            .exec(http("post create").post("/ping/create")
                    .body(StringBody("{\"data\": \"performance test\"}")).asJson()
                    .check(status().is(201)))
            .pause(1, 3);

    {
        setUp(
                // Teste de Carga: Usuários aumentam lentamente até um pico de 100 por segundo
                loadTestScenario.injectOpen(
                        rampUsersPerSec(1).to(100).during(Duration.ofSeconds(30)) // Aumentando de 1 a 100 usuários por segundo
                ),
                // Teste de Stress: Testando com uma carga muito alta de 500 usuários por segundo durante 1 minuto
                stressTestScenario.injectOpen(
                        constantUsersPerSec(500).during(Duration.ofMinutes(1)) // 500 usuários fixos por segundo
                ),
                // Teste de Performance: Variando de 1 a 200 usuários por segundo durante 20 segundos
                performanceTestScenario.injectOpen(
                        rampUsersPerSec(1).to(200).during(Duration.ofSeconds(20)) // Crescimento gradual para 200 usuários por segundo
                )
        ).protocols(httpProtocol);
    }
}


/*
     * EXPLICAÇÃO DOS CENÁRIOS DE TESTE
     * 
     * 1. TESTE DE CARGA (Load Test)
     *    - Propósito: Avaliar como a aplicação se comporta ao aumentar gradativamente o número de usuários.
     *    - Injeção de carga: Usuários começam em 1 usuário/segundo e aumentam linearmente até 100 usuários/segundo em 30 segundos.
     *    - Resultado esperado: Identificar o ponto em que o sistema começa a apresentar atrasos significativos ou falhas.
     * 
     * 2. TESTE DE STRESS (Stress Test)
     *    - Propósito: Colocar a aplicação sob uma carga muito alta para determinar o limite de capacidade do sistema.
     *    - Injeção de carga: 500 usuários/segundo constantes durante 1 minuto.
     *    - Resultado esperado: Descobrir em que ponto o sistema falha completamente ou fica instável.
     * 
     * 3. TESTE DE PERFORMANCE (Performance Test)
     *    - Propósito: Avaliar como a aplicação responde em condições normais e com carga variada.
     *    - Injeção de carga: Usuários começam em 1 usuário/segundo e aumentam até 200 usuários/segundo em 20 segundos.
     *    - Resultado esperado: Verificar se a aplicação mantém os tempos de resposta aceitáveis sob diferentes condições de carga.
     */