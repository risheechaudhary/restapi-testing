package com.neosoft.test;

import com.neosoft.restapi.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest{

    Calculator calculator;

    @BeforeEach
    public void setUp(){
        calculator=new Calculator();
    }

    @Test
    public void testMultiply(){
        assertEquals(20,calculator.multiply(4,5));
        assertEquals(10,calculator.multiply(2,5));

    }

    @Test
    public void testDivide(){
        assertEquals(2,calculator.divide(4,2));
//        assertEquals(2,calculator.divide(4,0));
    }
}
