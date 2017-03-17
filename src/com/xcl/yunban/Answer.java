package com.xcl.yunban;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.xcl.utils.Native2AsciiUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Answer {
	
	public static void main(String[] args) throws Exception {
		String text = parseContent("男孩");
		System.out.println(text);
	}
	
	/**
     * 从数据接口获取到数据
     * @return
     * @throws Exception
     */
    public static String getSimiliarInfo(String ques) throws Exception {
        String json = "";
        //url中不可以出现空格，空格全部用%20替换
        String url = "http://120.76.46.23/getSimiliarInfo?question="+ques+"&userId=12&pageNow=1&pageSize=1"; 
        System.out.println("url "+url);
        URL urls = new URL(url);  
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection)urls.openConnection();  
        //因为服务器的安全设置不接受Java程序作为客户端访问，解决方案是设置客户端的User Agent
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setDoOutput(true);  
        //只可以设置为GET方式，不可以使用POST方式
        //conn.setRequestMethod("POST");
        conn.setRequestMethod("GET");
        //得到输入流
        InputStream inputStream = conn.getInputStream(); 
        //从输入流中获取数据
        BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream));
        String line=null;
        while((line=bf.readLine())!=null){//一行一行的读
            json = json + line;
        }
        if(inputStream!=null){  
            inputStream.close();  
        }
        System.out.println("getSimiliarInfo()..."+json);
        return json;
    }

    public static String getContentFormJson(String ques) throws Exception{
    	String jsonString = getSimiliarInfo(ques);
    	JSONObject obj = JSONObject.fromObject(jsonString);  
    	JSONObject successInfo = obj.getJSONObject("successInfo");
    	
    	if(successInfo.isEmpty()){
    		return "抱歉，暂时没有此问题的答案...";
    	}
    	
    	JSONArray jsonArray = obj.getJSONArray("informationInfoList");
    	String content = (String) jsonArray.getJSONObject(0).get("content");
    	System.out.println("getContentFormJson()" + content);
    	return content;
    }
    public static String parseContent(String ques) throws Exception{
    	String input = Native2AsciiUtils.native2Ascii(ques);
    	String content = getContentFormJson(input);
    	System.out.println(content);
    	
    	Document doc = Jsoup.parse(content);//解析HTML字符串返回一个Document实现
    	String text = doc.body().text();
    	System.out.println("parseContent()..." + text);
    	return text;
    }
}
