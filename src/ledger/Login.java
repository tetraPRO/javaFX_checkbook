package ledger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Login {

    private List<String> cookies;
    private HttpsURLConnection conn;

    private final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:62.0) Gecko/20100101 Firefox/62.0";

    public static void main(String[] args) throws Exception{

        Password pass = new Password();

        String url = "https://www.tucsonfcusecure.com";
        String postURL = url + "/tob/live/usp-core/app/initialLogin";

        Login http = new Login();

        CookieHandler.setDefault(new CookieManager());

        //send GET request
        String page = http.getPageContent(url);
        String postParams = http.getFormParams(page, "caputo1713!", new String(pass.getPass()));

        //construct above post content and send a POST request
        http.sendPost(postURL, postParams);

        //success then go to bank
        String result = http.getPageContent(url + "/tob/live/usp-core/app/home");
        System.out.println(result);
    }

    private void sendPost(String url, String postParams) throws Exception{
        URL obj = new URL(url);
        conn = (HttpsURLConnection)obj.openConnection();

        //acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "tucsonfcusecure.com");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");

        if(cookies != null){
            for(String cookie : this.cookies){
                conn.addRequestProperty("Cookie", cookie.split(";",1)[0]);
            }
        }
        /**Cookies
         * JSESSIONID=5A117E1E24B5F3222E7649B60A18EFA7.usp2-sl1-prd1-dca;
         * RT="r=https%3A%2F%2Fwww.tucsonfcusecure.com%2Ftob%2Flive%2Fusp-core%2Fapp%2Fhome&ul=1537920805357&hd=1537920805364";
         * MAF_IB_aa138f16d6db11e79708005056af77d2=AZLkB5xHLIdgpTofZRyArLPZMHm8gkEyNH87CTQgEpMUDW0;
         * rftoken=5ef3c633-8d80-49bc-bb0f-908f1e66bb88;
         * BIGipServersdp-sl1.prd1.dca.diginsite.net_8080=3617023498.36895.0000;
         * FMISSESSIONID=A74547DF60222625A86FEF9E52234D8D.node6
         */


        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", "https://tucsonfcu.com/");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);

        //send POST request
        DataOutputStream write = new DataOutputStream(conn.getOutputStream());
        write.writeBytes(postParams);
        write.flush();
        write.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL :" + url);
        System.out.println("Post parameters :" + postParams);
        System.out.println("Response code :" + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
    }

    private String getFormParams(String html, String username, String password) throws UnsupportedEncodingException{
        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        Element loginForm = doc.getElementById("Login");

        Elements inputElements = loginForm.getElementsByTag("input");

        List<String> paramList = new ArrayList<>();

        for(Element inputElement : inputElements){
            String key = inputElement.attr("name");
            String value  = inputElement.attr("value");

            if(key.equals("userid")){
                value = username;
            }else if(key.equals("password")){
                value = password;
            }
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        //build parameters list
        StringBuilder builder = new StringBuilder();

        for(String param : paramList){
            if(builder.length() == 0){
                builder.append(param);
            }else {
                builder.append("&" + param);
            }
        }
        return builder.toString();
    }

    private String getPageContent(String url) throws Exception{
        URL obj = new URL(url);
        conn = (HttpsURLConnection)obj.openConnection();

        conn.setRequestMethod("GET");
        conn.setUseCaches(false);

        //act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        if(cookies != null){
            for(String cookie : this.cookies){
                conn.addRequestProperty("Cookie", cookie.split(";",1)[0]);
            }
        }

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();

        //get response cookies
        setCookies(conn.getHeaderFields().get("Set-Cookie"));

        return response.toString();
    }

    public List<String> getCookies(){
        return cookies;
    }

    public void setCookies(List<String> cookies) {
       this.cookies = cookies;
    }

}