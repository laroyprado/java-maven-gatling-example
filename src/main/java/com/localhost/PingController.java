package com.localhost;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;

@Controller("/ping")
public class PingController {

    @Get
    public String ping() {
        return "Olá";
    }

    @Post("/create")
    public HttpStatus create(@Body String data) {
        return HttpStatus.CREATED;
    }

    @Put("/update")
    public HttpStatus update(@Body String data) {
        return HttpStatus.OK;
    }
}
