package com.cs.inje.of;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
public class HttpClient
{
    private String id;
    private String password;
    private String htmlString;

    public void run() throws Exception
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("https://stud.inje.ac.kr/AuthUser.aspx/");
        List < NameValuePair > npvs = new ArrayList < NameValuePair > ();
        npvs.add(new BasicNameValuePair("IjisUserID", id));
        npvs.add(new BasicNameValuePair("IjisPassword", password));
        httpPost.setEntity(new UrlEncodedFormEntity(npvs));
        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        System.out.println(response2.getStatusLine());
        HttpEntity entity2 = response2.getEntity();
        EntityUtils.consume(entity2);

        String httpurl = new String("https://stud.inje.ac.kr/REGI/REGI01011WS.aspx?MenuID=0003301");
        OutputStream ostream;
        HttpGet get = new HttpGet(httpurl);
        CloseableHttpResponse response;
        response = httpclient.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader
            (response.getEntity().getContent(), "UTF-8"));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null)
        {
            result.append(line);
            result.append("\r\n");
        }
        rd.close();
        response.close();
        response2.close();

        htmlString = result.toString();

    }
    public HttpClient(String id, String password)
    {
        this.id = id;
        this.password = password;
    }
    public HttpClient()
    {

	}
    public String parsing(String tagName)
    {
        Document doc = Jsoup.parse(htmlString,"EUC-KR");
        Elements contents;
        contents = doc.select(tagName);
        return contents.text();
    }
}
