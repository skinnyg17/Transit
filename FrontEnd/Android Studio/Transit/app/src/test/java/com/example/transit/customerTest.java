package com.example.transit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class customerTest {
    @Mock
    customerHelper mock;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mock = Mockito.mock(customerHelper.class);
    }

    @Test
    public void testCost() {
        when(mock.getRoughCost(1,0)).thenReturn(-1.00);
        double i = mock.getRoughCost(1,0);
        verify(mock).getRoughCost(1,0);
        assertEquals(-1.00,i);
    }

    @Test
    public void testJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lol",1);
        when(mock.getJSONFromString(json)).thenReturn(json.getJSONArray("lol"));
        mock.getJSONFromString(json);
        verify(mock).getJSONFromString(json);
    }
}
