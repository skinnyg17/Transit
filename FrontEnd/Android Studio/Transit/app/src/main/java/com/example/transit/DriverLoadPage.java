package com.example.transit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DriverLoadPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button capture, scan, nextBox, doneLoading;
    private ImageView imageCaptured;
    private TextView result;
    private EditText title, description;
    private String userName, customer,jobID,boxSize;
    private JSONObject boxInfo;
    private static final int REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST = 200;
    private Bitmap bitmapCaptured;
    private Bundle bundle;
    private Spinner sizeSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_load_page);

        userName = getIntent().getStringExtra("username");
        customer = getIntent().getStringExtra("customer");
        jobID = getIntent().getStringExtra("jobID");
        if(boxInfo == null){
            boxInfo = new JSONObject();
        }
        if (bundle != null) {
            try {
                boxInfo = new JSONObject((String) bundle.get("json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            bundle = new Bundle();
        }

        capture = findViewById(R.id.DriverLoad_Capture);
        scan = findViewById(R.id.Scan);
        imageCaptured = findViewById(R.id.DriverLoadImage);
        result = findViewById(R.id.DriverLoadResult);
        nextBox = findViewById(R.id.DriverLoad_NextBox);
        doneLoading = findViewById(R.id.DriverLoad_DoneLoading);
        description = findViewById(R.id.DriverLoad_Description);
        title = findViewById(R.id.DriverLoad_BoxTitle);
        sizeSpinner = findViewById(R.id.DriverLoad_Size);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.DriverLoadSize,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(adapter);
        sizeSpinner.setOnItemSelectedListener(this);

        try {
            getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverLoadPage.this, GeneralScanPage.class);
                intent.putExtra("role", "driver");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        nextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addToBoxInfoJSON();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        doneLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    finishLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapte, View view, int i, long l) {
        boxSize = adapte.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void finishLoading() throws JSONException {
        addToBoxInfoJSON();

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView input1 = new TextView(this);
        input1.setText("Box Count : " + boxInfo.length());
        input1.setGravity(Gravity.CENTER);
        linearLayout.addView(input1);

        Iterator<String> keys = boxInfo.keys();
        final List<CheckBox> checkBoxList = new ArrayList<>();
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject tempBox = new JSONObject(String.valueOf(boxInfo.get(key)));
            final CheckBox input = new CheckBox(this);
            String boxDisplayInfo = tempBox.get("Title") + " : " + tempBox.get("Description");
            input.setText(boxDisplayInfo);
            input.setChecked(true);
            linearLayout.addView(input);
            checkBoxList.add(input);
        }

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Confirm Loading")
                .setCancelable(false)
                .setMessage("Please verify the boxes you want to load.").setView(linearLayout).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNeutralButton("Remove Boxes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();

        builder.show();

        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss if box count is empty
                if(boxInfo.length()==0){
                    builder.dismiss();
                }
                else{
                    // send to backend
                    // add to data base under customer: info needed will include requestID, userName(Customer), boxInfo
                    JSONObject toSend = new JSONObject();
                    for(int i=0;i<checkBoxList.size();i++){
                        CheckBox checkBox = checkBoxList.get(i);
                        if(checkBox.isChecked()){
                            try {
                                JSONObject realBox = new JSONObject(String.valueOf(boxInfo.get(String.valueOf(i))));
                                toSend.put(String.valueOf(realBox.get("BoxID")),realBox.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    JSONObject container = new JSONObject();
                    try {
                        container.put("BoxData", toSend.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendPostBoxInfo("requestinfo",jobID,container.toString());
                    // go back to previous activity with a code of 11 indicating success
                    // update progress status in request info in database
                }
                builder.dismiss();
            }
        });

        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // empty the boxInfo in bundle
                boxInfo = new JSONObject();
                onSaveInstanceState(bundle);
                builder.dismiss();
            }
        });

        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing to add
                builder.dismiss();
            }
        });
    }

    private void addToBoxInfoJSON() throws JSONException {
        if(description.getText().equals("")){

        }
        else if(title.getText().equals("")){

        }
        else if(result.getText().equals("Box ID") || result.getText().equals("")){

        }
        else if(bitmapCaptured==null){
        }
        else{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapCaptured.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String imageB64 = Base64.encodeToString(data, Base64.DEFAULT);
            JSONObject newBox = new JSONObject();
            newBox.put("Title",title.getText());
            newBox.put("Description",description.getText());
            newBox.put("Size",boxSize);
            newBox.put("BoxID",result.getText());
            newBox.put("Image",imageB64);
            boxInfo.put(String.valueOf(boxInfo.length()),newBox.toString());

            title.setText("");
            imageCaptured.setImageDrawable(null);
            result.setText("Box ID");
            description.setText("");
            Toast.makeText(this,"Box added", Toast.LENGTH_LONG).show();
            onSaveInstanceState(bundle);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmapCaptured = bitmap;
            imageCaptured.setImageBitmap(bitmap);
            try {
                sendImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                result.setText(barcode.displayValue);
            }
        }
    }

    private void sendImage(final Bitmap bitmap) throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        //The password's TextInputLayout
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/storeImage";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverLoadPage.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                String imageB64 = Base64.encodeToString(data, Base64.DEFAULT);

                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName);
                params.put("image", imageB64);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void getImage() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        //The password's TextInputLayout
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/getImage";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        byte[] data = Base64.decode(response, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        imageCaptured.setImageBitmap(bmp);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("json", boxInfo.toString());
    }

    private void sendPostBoxInfo(final String tableName, final String object, final String jsonString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/updateFireBase";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverLoadPage.this, "Successfully uploaded Box Data", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("code", "11");

                        setResult(11, intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverLoadPage.this, "Failed to upload Box Data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonString", jsonString);
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

}
