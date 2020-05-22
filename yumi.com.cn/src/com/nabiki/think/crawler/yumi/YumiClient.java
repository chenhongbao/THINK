package com.nabiki.think.crawler.yumi;

import javax.json.bind.Jsonb;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;

import com.nabiki.think.crawler.yumi.data.QueryResponse;

public class YumiClient {
	private String url = "https://trade.yumi.com.cn/data/query/item/data/queryItemData?&startTime=&endTime=&queryId=";
	
	private final HttpClient client;
	private final Jsonb json;
	
	public YumiClient(QueryResultIO io) {
		this.client = HttpClient.newBuilder()
                .sslContext(sslContext())
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
		this.json = io.jsonb();
	}
	
	public QueryResponse query(int queryId) {
		HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create(this.url + Integer.toString(queryId)))
                .build();
		
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			var r = this.json.fromJson(response.body(), QueryResponse.class);
			// Set query id to type.
			r.result.type = Integer.toString(queryId);
			return r;
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}
	
	private SSLContext sslContext() {
        try {
            var context = SSLContext.getInstance("SSL","SunJSSE");
            context.init(null, new TrustManager[]{new X509TrustManager(){
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new java.security.SecureRandom());
            
            return context;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | KeyManagementException e) {
            return null;
        }
	}
}
