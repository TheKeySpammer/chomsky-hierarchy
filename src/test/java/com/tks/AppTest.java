package com.tks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tks.App;

import org.junit.jupiter.api.Test;

class MyFirstJUnitJupiterTests {

    private final App app= new App();

    @Test
    void printHello() {
        assertEquals("Hello", app.sayHello());
    }
}