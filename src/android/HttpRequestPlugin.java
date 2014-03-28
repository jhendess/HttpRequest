package org.github.bperin.http;

import java.util.HashMap;

public class HttpRequestPlugin extends CordovaPlugin {

	private final String TAG = "HTTP";

	// private HttpRequest request;

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
								request.header(keyName, params.getString(keyName));
								//request.header("Referer", "http://www.cdc.gov/mobile/Applications/sto/feedback/feedbackGeneric.html");
							}
						}

						if (trust == true) {
							request.trustAllCerts();
							request.trustAllHosts();
						}

						if (method.equalsIgnoreCase("post")) {
							request.form(inputParams);
						}

						int code = request.code();
						String body = request.body();
						String msg = request.message();

						JSONObject response = new JSONObject();

						response.put("code", code);
						response.put("body", body);
						response.put("message", msg);

//						Log.d(TAG, "Response code " + url + " " + code);
//					    Log.d(TAG, "Response body " + url + " " + body);

						if (code == 200) {
							callbackContext.success(response);
							return;
						}
						else {
							callbackContext.error(response);
							return;
						}

					}
					catch (JSONException ex) {
						Log.e(TAG, ex.getMessage());
					}
					catch (HttpRequestException ex) {
						Log.e(TAG, ex.getMessage());
					}
					catch (Exception ex) {
						Log.e(TAG, ex.getStackTrace().toString());
						Log.e(TAG, ex.getMessage());
					}

				}
			});
			return true;
		}
		return false;

	}

}