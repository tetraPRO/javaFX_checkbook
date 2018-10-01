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
        String postParams = http.getFormParams(page, new String(pass.getPass()), new String(pass.getPass()));

        //construct above post content and send a POST request
        http.sendPost(postURL, postParams);

        //success then go to bank
        String result = http.getPageContent(url + "/tob/live/usp-core/app/postLogin");
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
         * BIGipServersdp-sl1.prd1.dca.diginsite.net_8080	3617023498.36895.0000
         * FMISSESSIONID	5BBD93B9F5CA5032D5BF6101AF1193D9.node6
         * JSESSIONID	C133DF9F93BF13FE7B095BE487C13289.usp2-sl1-prd1-dca
         * MAF_IB_aa138f16d6db11e79708005056af77d2	AOjdBNyPpK6jxHtOAdv5z1enx40At7f0rHGMMbXcpcdNPfl
         * rftoken	3503cceb-7c30-42d9-acbc-eede22ed810e
         * RT	"nu=https://www.tucsonfcusecure.com/tob/live/usp-core/app/logout?reason=userlogout&cl=1538349094025&r=https://www.tucsonfcusecure.com/tob/live/usp-core/app/home&ul=1538349094176&hd=1538349094490"
         */


        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", "https://tucsonfcu.com/");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
        conn.setRequestProperty("Cookie", "BIGipServersdp-sl1.prd1.dca.diginsite.net_8080=3617023498.36895.0000");
        conn.setRequestProperty("Cookie", "FMISSESSIONID=08BB302D279A00EA9B15925741E67C08.node6");
        conn.setRequestProperty("Cookie", "JSESSIONID=231D5D734A3AE5C11BA5F18500596CF2.usp2-sl1-prd1-dca");
        conn.setRequestProperty("Cookie", "MAF_IB_aa138f16d6db11e79708005056af77d2=AOjdBNyPpK6jxHtOAQd4pm55p9rWgQUCMnjPzXBnVBUqluc");
        conn.setRequestProperty("Cookie", "rftoken=b162d0e4-9fbe-44c9-8723-58c63a269591");
        conn.setRequestProperty("Cookie", "RT=nu=https://www.tucsonfcusecure.com/tob/live/usp-core/app/logout?reason=userlogout&cl=1538349094025&r=https://www.tucsonfcusecure.com/tob/live/usp-core/app/home&ul=1538349094176&hd=1538349094490");

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
            }else if(key.equals("testcookie")){
                value = "true";
            }else if(key.equals("testjs")){
                value = "true";
            }else if(key.equals("dscheck")){
                value = "1";
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