package com.lfg.mobileang;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pterede on 8/18/2016.
 */
public class ANGAppActivity extends SalesforceActivity {

    private WebView webView;
    private RestClient client;
    Map<String, String> map = new HashMap<String, String>();
    /**
     * Method that is called after the activity resumes once we have a RestClient.
     *
     * @param client RestClient instance.
     */
    @Override
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;
        String myurl = map.get(client.getClientInfo().getInstanceUrlAsString().toLowerCase())+"/apex/home";
        System.out.println(client.getClientInfo().getInstanceUrlAsString().toLowerCase()+"=================>"+myurl+"|");
        webView.loadUrl(myurl);
        //webView.loadUrl("https://ang--sso--c.cs12.visual.force.com/apex/simplesso");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map.put("https://ang--sso.cs12.my.salesforce.com".toLowerCase(), "https://ang--sso--c.cs12.visual.force.com");
        map.put("https://ang--ANGQA.cs3.my.salesforce.com".toLowerCase(), "https://ang--angqa--c.cs3.visual.force.com");
        // Setup view
        setContentView(R.layout.angapp);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        System.out.println("=====================================");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

    }
}
