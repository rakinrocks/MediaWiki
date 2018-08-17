package com.ranganesh.mediawiki;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.ranganesh.mediawiki.adapter.MediaWikiAdapter;
import com.ranganesh.mediawiki.app.AppController;
import com.ranganesh.mediawiki.model.MediaWiki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    // Progress dialog
    private ProgressDialog pDialog;
    private List<MediaWiki> mediaWikiList = new ArrayList<>();
    private MediaWikiAdapter mediaWikiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); // We hide Action Bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        final ListView listView = (ListView) findViewById(R.id.list_view);
        final EditText searchView = (EditText) findViewById(R.id.search_field);
        final TextView searchX = (TextView) findViewById(R.id.search_x);
        searchX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
                if(mediaWikiList != null)
                    mediaWikiList.clear();
                if(mediaWikiAdapter != null)
                    mediaWikiAdapter.notifyDataSetChanged();
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                if (searchText.length() == 0) {
                    searchX.setVisibility(View.INVISIBLE);
                    if(mediaWikiList != null)
                        mediaWikiList.clear();
                    if(mediaWikiAdapter != null)
                        mediaWikiAdapter.notifyDataSetChanged();
                }

            }
        });
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = searchView.getText().toString().trim();
                    if (searchText.length() == 0) {
                        Toast.makeText(getApplicationContext(),
                                "Please enter min one char", Toast.LENGTH_SHORT).show();
                        searchX.setVisibility(View.INVISIBLE);
                        if(mediaWikiList != null)
                            mediaWikiList.clear();
                        if(mediaWikiAdapter != null)
                            mediaWikiAdapter.notifyDataSetChanged();
                    } else {
                        searchX.setVisibility(View.VISIBLE);
                        mediaWikiJsonObjectRequest(searchText);
                    }
                    return true;
                }
                return false;
            }
        });
        mediaWikiAdapter = new MediaWikiAdapter(this,
                mediaWikiList);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mediaWikiAdapter);
        swingBottomInAnimationAdapter.setAbsListView(listView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8, r.getDisplayMetrics());
        listView.setDividerHeight(px);
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                r.getDisplayMetrics());
        listView.setPadding(px, px, px, px);
        listView.setAdapter(swingBottomInAnimationAdapter);
    }

    /**
     * Method to make json object request where json response starts with {
     * */
    private void mediaWikiJsonObjectRequest(String searchText) {

        showpDialog();

        String urlJsonObj = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages%7Cpageterms&format=json&formatversion=2&piprop=thumbnail&pithumbsize=640&pilimit=50&generator=prefixsearch&wbptterms=description&gpssearch=";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                urlJsonObj + searchText, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                if(mediaWikiList != null)
                    mediaWikiList.clear();

                try {
                    // Parsing json object response
                    // response will be a json object

                    // Query is json object
                    JSONObject queryObj = response.optJSONObject("query");
                    if(queryObj != null) {
                        // Page is json array
                        JSONArray pageArr = queryObj.getJSONArray("pages");
                        for (int i = 0; i < pageArr.length(); i++) {
                            MediaWiki mediaWiki = new MediaWiki();
                            JSONObject pagesObj = (JSONObject) pageArr.get(i);
                            mediaWiki.setTitle(pagesObj.optString("title"));
                            // Thumbnail is json object
                            JSONObject thumbnailObj = pagesObj.optJSONObject("thumbnail");
                            if (thumbnailObj != null)
                                mediaWiki.setThumbnailUrl(thumbnailObj.optString("source"));
                            else
                                mediaWiki.setThumbnailUrl("");
                            // Terms is json object
                            JSONObject termsObj = pagesObj.optJSONObject("terms");
                            if (termsObj != null) {
                                // Desctiption is json array
                                JSONArray descArr = termsObj.getJSONArray("description");
                                mediaWiki.setDescription(descArr.get(0).toString());
                            } else {
                                mediaWiki.setDescription("");
                            }
                            //adding media to media array
                            mediaWikiList.add(mediaWiki);
                        }
                    }else{
                        if(mediaWikiList != null)
                            mediaWikiList.clear();
                        if(mediaWikiAdapter != null)
                            mediaWikiAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),
                                "No Data Available", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
                mediaWikiAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
