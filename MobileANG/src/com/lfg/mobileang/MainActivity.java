/*
 * Copyright (c) 2012-2016, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.lfg.mobileang;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;

/**
 * Main activity
 *
 * Google Cloud Messaging
 Server API Key help
 AIzaSyAwH4eYQq_AuFuGhKtK0ctS2DVJwZACoG4


 Sender ID help
 442444294657
 */
public class MainActivity extends SalesforceActivity {
	TextToSpeech t1,t2;
	TooltipWindow tipWindow;
	TextView s2t ;
    private RestClient client;
    private ArrayAdapter<String> listAdapter;
	int versionCode = BuildConfig.VERSION_CODE;
	String versionName = BuildConfig.VERSION_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);
		/*
		 * Un-comment the line below to enable push notifications in this app.
		 * Replace 'pnInterface' with your implementation of 'PushNotificationInterface'.
		 * Add your Google package ID in 'bootonfig.xml', as the value
		 * for the key 'androidPushNotificationClientId'.
		 */

		MyPushHandler pnInterface = new MyPushHandler(MainActivity.this,
			new TooltipWindow(MainActivity.this) );
		SalesforceSDKManager.getInstance().setPushNotificationReceiver(pnInterface);
	}

	public void showHelp(View v) {

		if (!tipWindow.isTooltipShown()) {
			tipWindow.showToolTip(((Button)findViewById(R.id.spkbtn)));
			//Toast.makeText(MainActivity.this,
			//		"Commands: \n1)get Bob data\n2)get Bob dashboard\n3)get Bob pending\n4)get Bob detail\n5)get Bob delegate\n6)get users [name]\n7)get webisites [name]",
			//		Toast.LENGTH_LONG).show();

		}
	}

	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);
		tipWindow = new TooltipWindow(MainActivity.this);
		((Button)findViewById(R.id.spkbtn)).setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				tipWindow.showToolTip(v);
				return false;
			}
		});
		// Create list adapter
		listAdapter = new ArrayAdapter<String>(this, R.layout.mylistview, new ArrayList<String>()){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				if (position % 2 == 1) {
					view.setBackgroundColor(Color.DKGRAY);
				} else {
					view.setBackgroundColor(Color.BLACK);
				}

				return view;
			}
		};
		((ListView) findViewById(R.id.contacts_list)).setAdapter(listAdapter);				
		
		super.onResume();
	}		
	
	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

		// Show everything
		findViewById(R.id.root).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.uname)).setText(
				versionName + ":" +client.getClientInfo().getInstanceUrlAsString() +" : "+ client.getClientInfo().username);
	}

	/**
	 * Called when "Logout" button is clicked. 
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}
	public void onANGAppClick(View v) {
		System.out.println("Goto ANGApp  Activity");
		Intent intent = new Intent(this, ANGAppActivity.class);
		startActivity(intent);
	}
	public void goMultiLingual(View v){
		System.out.println("Goto Speech  Activity");
		Intent intent = new Intent(this, SpeechActivity.class);
		startActivity(intent);
	}
	/**
	 * Called when "Clear" button is clicked. 
	 * 
	 * @param v
	 */
	public void onClearClick(View v) {
		listAdapter.clear();
		s2t = (TextView)findViewById(R.id.s2t);
		s2t.setText("");
	}	

	/**
	 * Called when "Fetch Contacts" button is clicked
	 * 
	 * @param v
	 * @throws UnsupportedEncodingException 
	 */
	public void onFetchContactsClick(View v) {
		s2t = (TextView)findViewById(R.id.s2t);
		String srchTxt = s2t.getText().toString();
		findContacts(srchTxt);
	}
	private void findContacts(String srchTxt) {
		if (srchTxt != null && srchTxt.trim().length() > 0) {
			sendRequest("SELECT Name FROM website__c where name like '%" + srchTxt + "%'");
		} else {
			sendRequest("SELECT Name FROM website__c");
		}
	}

	/**
	 * Called when "Fetch Accounts" button is clicked
	 * 
	 * @param v
	 * @throws UnsupportedEncodingException 
	 */
	public void onFetchUsersClick(View v) {
		s2t = (TextView)findViewById(R.id.s2t);
		String srchTxt = s2t.getText().toString();
		findUsers(srchTxt);
	}
	private void findUsers(String srchTxt) {
		if ( srchTxt != null && srchTxt.trim().length()>0) {
			sendRequest("SELECT Name, Email, Channel__c FROM User where firstname like '%"+srchTxt+"%' or lastname like '%"+srchTxt+"%'");
		}else {
			sendRequest("SELECT Name, Email, Channel__c FROM User");
		}
	}
	private void sendRequest(final String soql) {
		RestRequest restRequest = null;
		try {
			restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);
		} catch (UnsupportedEncodingException e) {
			listAdapter.add("Unsupported Encoding:" + soql);
			return;
		}
System.out.println("=====================>"+soql+"|");
		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							listAdapter.clear();
							JSONArray records = result.asJSONObject().getJSONArray("records");
							for (int i = 0; i < records.length(); i++) {
								if ( soql.contains("Channel__c FROM User")) {
									listAdapter.add(
											records.getJSONObject(i).getString("Name")
											+ " has Email of '" + records.getJSONObject(i).getString("Email")
											+ "' and Channel '"+ records.getJSONObject(i).getString("Channel__c") + "'"
									);
								} else {
									listAdapter.add(records.getJSONObject(i).getString("Name"));
								}
							}
							readResult(null);
						} catch (Exception e) {
							onError(e);
						}
					}
				});
			}
			
			@Override
			public void onError(final Exception exception) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MainActivity.this,
								MainActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	private void executeRESTAPI(String cmd)  {
		//Get the HTTP Client
		OkHttpClient httpClient = client.getOkHttpClient();
		new FetchDataAsync( client.getClientInfo().getInstanceUrlAsString(),
				(Button)(TextView)findViewById(R.id.readresult),
				listAdapter, cmd, getApplicationContext()).execute(httpClient);

	}

	public void startTalking(View view) {
		s2t = (TextView)findViewById(R.id.s2t);
		t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				System.out.println("=====STatus=" + status);
				if (status != TextToSpeech.ERROR) {
					t1.setLanguage(Locale.US);
					String cmd = s2t.getText().toString();
					String toSpeak = "Please specify commands: get bob data or get bob details or get dashboard or get Bob Pending";
					if (cmd == null || cmd.trim().length() < 1) {
						Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
						t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
						return;
					}
					toSpeak = "Going to Execute Command :" + cmd;listAdapter.clear();
					if (cmd.toLowerCase().matches("g.t bob dat.*")) {
						executeRESTAPI("bob");
					} else if (cmd.toLowerCase().matches("g.t bob detail.*")) {
						executeRESTAPI("bobdetail");
					} else if (cmd.toLowerCase().matches("g.t bob pending.*")) {
						executeRESTAPI("pending");
					} else if (cmd.toLowerCase().matches("g.t bob delegate.*")) {
						executeRESTAPI("delegate");
					} else if (cmd.toLowerCase().matches("g.t bob dashboard.*")) {
						executeRESTAPI("dashboard");
					} else if (cmd.toLowerCase().matches("g.t user.*")) {
						findUsers(cmd.replaceAll("g.t users?", "").trim());
					} else if (cmd.toLowerCase().matches("g.t website.*")) {
						findContacts(cmd.replaceAll("g.t websites?", "").trim());
				    } else {
						Toast.makeText(getApplicationContext(), "DO not recognize command:"+cmd, Toast.LENGTH_SHORT).show();
						t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
						return;
					}
					Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
					t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
				}
			}
		});
	}


	public void readResult(View view) {

		t2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				System.out.println("=====STatus=" + status);
				if (status != TextToSpeech.ERROR) {
					t2.setLanguage(Locale.US);
					int cnt = listAdapter.getCount();
					if ( cnt > 7 ) cnt = 7;
					t2.speak("Reading List:", TextToSpeech.QUEUE_FLUSH, null);
					for(int i = 0; i < cnt; i++) {
						String toSpeak = listAdapter.getItem(i);
						Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
						t2.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
					}
				}
			}
		});
	}

//speech recognition
	/**
	 * Showing google speech input dialog
	 * */
	public void startListening(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Say something in ENGLISH");
		try {
			startActivityForResult(intent, 100);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					"You cant talk to me like that",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == 100 && resultCode == RESULT_OK) {
			onSpeechActivity(requestCode, resultCode, data);
		}
	}
	/**
	 * Receiving speech input
	 * */

	private void onSpeechActivity(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		TextView s2t = (TextView)findViewById(R.id.s2t);
		Button runcmd = (Button)findViewById(R.id.runcmd);
		switch (requestCode) {
			case 100: {
				if (resultCode == RESULT_OK && null != data) {

					ArrayList<String> result = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					System.out.println("Possible interpretations:"+result);
					s2t.setTextSize(20f);
					s2t.setTextColor(Color.RED);
					s2t.setText(result.get(0));
					runcmd.performClick();
				}
				break;
			}
		}
	}
}
