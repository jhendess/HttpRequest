package org.github.bperin.http;

import android.util.Log;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class HttpRequestPlugin extends CordovaPlugin {

    private final String TAG = "HTTP";

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("execute")) {
            cordova.getThreadPool().execute(new Runnable() {

                public void run() {
                    try {

                        HttpRequest request;

                        String url = args.getString(0);
                        String method = (args.length() < 2 || args.isNull(1)) ? "get" : args.getString(1);

                        JSONObject params = (args.length() < 3 || args.isNull(2)) ? new JSONObject() : args.getJSONObject(2);
                        JSONObject options = (args.length() < 4 || args.isNull(3)) ? new JSONObject() : args.getJSONObject(3);

                        // optional params
                        boolean trust = options.has("trustAll") ? options.getBoolean("trustAll") : false;
                        JSONObject headers = options.has("headers") ? options.getJSONObject("headers") : null;
                        String payload = options.has("payload") ? options.getString("payload") : null;      // Payload is request body

                        // iterate over the supplied params, depending on
                        // request method params are either directly attached to
                        // url(GET) or as a form(POST)

                        Map<String, String> inputParams = new HashMap<String, String>();

                        if (null != params) {
                            @SuppressWarnings("unchecked")
                            Iterator<String> keys = params.keys();
                            while (keys.hasNext()) {

                                // get the key and corresponding value
                                String keyName = (String) keys.next();
                                String keyValue = params.getString(keyName);

                                inputParams.put(keyName, keyValue);
                            }
                        }

                        // Initiate either post or get request
                        // TODO: Support more methods
                        if (method.equalsIgnoreCase("post")) {
                            request = HttpRequest.post(url);
                        } else {
                            request = HttpRequest.get(url, inputParams, true);
                        }

                        if (null != headers) {
                            @SuppressWarnings("unchecked")
                            Iterator<String> keys = headers.keys();
                            while (keys.hasNext()) {
                                // get the key and corresponding value
                                String keyName = keys.next();
                                request.header(keyName, headers.getString(keyName));
                            }
                        }

                        if (trust) {
                            request.trustAllCerts();
                            request.trustAllHosts();
                        }

                        if (method.equalsIgnoreCase("post")) {
                            request.form(inputParams);
                        }

                        // Set additional request body, if payload option is set (useful for SOAP-requests)
                        if (payload != null) {
                            request.send(payload);
                        }

                        JSONObject responseHeaders = new JSONObject();

                        int code = request.code();
                        String body = request.body();
                        String msg = request.message();

                        Map<String, List<String>> headerMap = request.headers();
                        if (headerMap != null) {
                            for (String key : headerMap.keySet()) {
                                if (key == null)
                                    continue;
                                List<String> value = headerMap.get(key);
                                String joined = joinStrings(value, ", ");
                                responseHeaders.put(key, joined);
                            }
                        }

                        JSONObject response = new JSONObject();
                        response.put("code", code);
                        response.put("body", body);
                        response.put("message", msg);

                        // Export response headers
                        response.put("headers", responseHeaders);

//                      Log.d(TAG, "Response code " + url + " " + code);
//                      Log.d(TAG, "Response body " + url + " " + body);

                        if (code == 200) {
                            callbackContext.success(response);
                            return;
                        } else {
                            callbackContext.error(response);
                            return;
                        }

                    } catch (JSONException ex) {
                        Log.e(TAG, ex.getMessage());
                    } catch (HttpRequestException ex) {
                        Log.e(TAG, ex.getMessage());
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getStackTrace().toString());
                        Log.e(TAG, ex.getMessage());
                    }

                }
            });
            return true;
        }
        return false;

    }

    /**
     * Joins a Collection of strings with a given separator.
     *
     * @param inputCollection
     *         Collection of Strings
     * @param separator
     *         the separator between each string
     * @return a string joined by the given separator
     */
    private static String joinStrings(Iterable<String> inputCollection, String separator) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (String s : inputCollection) {
            sb.append(sep).append(s);
            sep = separator;
        }
        return sb.toString();
    }

}