package com.example.transit;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class customerScanHelper {

    public Iterator<String>  getKeys(JSONObject ret) {
        Iterator<String> result = ret.keys();
        return result;
    }

    public String getJSONData(JSONObject a, String key) throws JSONException {
        String result = (String) a.get(key);
        return result;
    }

    public Iterator<String> getIter(JSONObject a) {
        Iterator<String> res = a.keys();
        return  res;
    }
}
