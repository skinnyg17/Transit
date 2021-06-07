package com.example.transit;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class customerScanTest {
    @Mock
    customerScanHelper mock;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mock = Mockito.mock(customerScanHelper.class);
    }

    @Test
    public void testKeys() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lol",1);
        when(mock.getKeys(json)).thenReturn(json.keys());
        mock.getKeys(json);
        verify(mock).getKeys(json);
    }

    @Test
    public void testJSONRes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lol",1);
        when(mock.getJSONData(json, "lol")).thenReturn((String) json.get("lol"));
        mock.getJSONData(json,"lol");
        verify(mock).getJSONData(json,"lol");
    }

    @Test
    public void testIter() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lol", 1);
        json.put("lol1", 2);
        when(mock.getIter(json)).thenReturn(json.keys());
        mock.getIter(json);
        verify(mock).getIter(json);
    }
}
