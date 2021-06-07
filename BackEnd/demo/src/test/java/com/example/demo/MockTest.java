package com.example.demo;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@WebMvcTest(DriverController.class)
@SpringBootTest
public class MockTest {

    @Mock
    EmailHelper emailHelper;

    @InjectMocks
    DriverController driverController;
    @InjectMocks
    FirebaseHelper firebaseHelper;
    @Autowired
    MockMvc mvc;
    @MockBean
    DriverController driver;

    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
    }

    @Test
    public void mocking1() throws Exception {
        driverController = mock(DriverController.class);

        MockHttpServletRequest mockHttpServletRequestTrue = new MockHttpServletRequest();
        mockHttpServletRequestTrue.addParameter("name","k");
        mockHttpServletRequestTrue.addParameter("check","true");
        when(driverController.updateDriverJobSearch(mockHttpServletRequestTrue)).thenReturn("Job Search is now enabled!");

        String viewName = driverController.updateDriverJobSearch(mockHttpServletRequestTrue);
        assertEquals("Job Search is now enabled!", viewName);

        MockHttpServletRequest mockHttpServletRequestFalse = null;
        assertEquals(null, mockHttpServletRequestFalse);

    }

    @Test
    public void mocking2() throws Exception {
        driverController = mock(DriverController.class);

        MockHttpServletRequest mockHttpServletRequestTrue = new MockHttpServletRequest();
        mockHttpServletRequestTrue.addParameter("name","k");
        JSONObject toReturn = new JSONObject();
        toReturn.put("JobSearch", "jobSearch");
        toReturn.put("JobID", "jobID");
        toReturn.put("destination", "destination");
        toReturn.put("pickUp", "pickup");
        toReturn.put("boxCount", "boxCount");
        toReturn.put("phone", "phone");
        toReturn.put("recieverName", "recieverName");
        toReturn.put("progress", "progress");
        when(driverController.getUserSettings(mockHttpServletRequestTrue)).thenReturn(toReturn.toString());

        String viewName = driverController.getUserSettings(mockHttpServletRequestTrue);
        assertEquals(toReturn.toString(), viewName);

        MockHttpServletRequest mockHttpServletRequestFalse = null;
        when(driverController.getUserSettings(mockHttpServletRequestTrue)).thenReturn(null);
        assertEquals(null, mockHttpServletRequestFalse);

    }

    @Test
    public void mocking3() throws Exception {

        firebaseHelper = mock(FirebaseHelper.class);
//        firebaseHelper = spy( (new FirebaseHelper()));
        when(firebaseHelper.getFirebaseData("requestinfo","101","JourneyCoordinates")).thenReturn("{Journey}");
        System.out.println(firebaseHelper.getFirebaseData("requestinfo","101","JourneyCoordinates"));
        driverController = mock(DriverController.class);
//        driverController = spy( (new DriverController()));

        MockHttpServletRequest mockHttpServletRequestTrue = new MockHttpServletRequest();
        mockHttpServletRequestTrue.addParameter("search","JourneyCoordinates");
        mockHttpServletRequestTrue.addParameter("tableName","requestinfo");
        mockHttpServletRequestTrue.addParameter("object","101");

        JSONObject toReturn = new JSONObject();
        toReturn.put("JobSearch", "jobSearch");
        toReturn.put("JobID", "jobID");
        toReturn.put("destination", "destination");
        toReturn.put("pickUp", "pickup");
        toReturn.put("boxCount", "boxCount");
        toReturn.put("phone", "phone");
        toReturn.put("recieverName", "recieverName");
        toReturn.put("progress", "progress");

        String toReallyReturn = "{Journey}";
        Mockito.when(driverController.getJourneyCoordinates(mockHttpServletRequestTrue)).thenReturn(toReallyReturn.toString());

        String viewName = driverController.getJourneyCoordinates(mockHttpServletRequestTrue);
        assertNotEquals(toReturn.toString(),viewName);
        assertEquals(toReallyReturn.toString(), viewName);
    }

    @Test
    public void mocking4() throws Exception {

//        firebaseHelper = mock(FirebaseHelper.class);
//        Mockito.when(firebaseHelper.getFirebaseData("requestinfo","101","JourneyCoordinates")).thenReturn("{Journey}");

        driverController = mock(DriverController.class);

        MockHttpServletRequest mockHttpServletRequestTrue = new MockHttpServletRequest();
        mockHttpServletRequestTrue.addParameter("name","driverboi");
        mockHttpServletRequestTrue.addParameter("check","true");

        JSONObject toReturn = new JSONObject();
        toReturn.put("JobSearch", "jobSearch");
        toReturn.put("JobID", "jobID");
        toReturn.put("destination", "destination");
        toReturn.put("pickUp", "pickup");
        toReturn.put("boxCount", "boxCount");
        toReturn.put("phone", "phone");
        toReturn.put("recieverName", "recieverName");
        toReturn.put("progress", "progress");

        String toAlsoNotReturn = "Job Search is now disabled";
        String toReallyReturn = "Job Search is now enabled!";
        when(driverController.updateDriverJobSearch(mockHttpServletRequestTrue)).thenReturn(toReallyReturn.toString());

        String viewName = driverController.updateDriverJobSearch(mockHttpServletRequestTrue);
        assertNotEquals(toReturn.toString(),viewName);
        assertNotEquals(toAlsoNotReturn.toString(),viewName);
        assertEquals(toReallyReturn.toString(), viewName);
    }
}
