package com.example.transit;

import com.example.transit.UserHelper.Driver;

import static junit.framework.TestCase.assertEquals;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DriverTest {

    @Mock
    Driver driver;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        driver = Mockito.mock(Driver.class);
    }


    @Test
    public void mockTest() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lol","lol");
        when(driver.getJobHistoryJSON("ha")).thenReturn(jsonObject);
        JSONObject newobj  = driver.getJobHistoryJSON("ha");
        verify(driver).getJobHistoryJSON("ha");
        assertEquals(newobj,jsonObject);
    }

    @Test
    public void mockTest2() {
        double check=0.0;
        when(driver.getCost(1,1,1,1,1)).thenReturn(0.0);
        double dob  = driver.getCost(1,1,1,1,1);
        verify(driver).getCost(1,1,1,1,1);
        assertEquals(check,dob);
    }

}

