package nikola.pavicevic.shoppinglist;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private static final int SUCCESS = 200;

    public int registerUser(String urlString , String username , String password , String email){
        String postData = "username="+username+"&password=" + password+"&email="+email;
        JSONObject jsonObject = new JSONObject();

        int responseCode=404;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] postDataBytes = postData.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();


            responseCode = connection.getResponseCode();

            connection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }
    public int loginUser(String urlString,String username,String password){
        String getData = "username="+username+"&password=" + password;
        int responseCode=404;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] getDataBytes = getData.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(getDataBytes.length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(getDataBytes);
            outputStream.flush();
            outputStream.close();


            responseCode = connection.getResponseCode();

            connection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }
    public int createList(String urlString , String name , String username){
        String postData = "name="+name+"&creator=" + username+"&shared=true";
        JSONObject jsonObject = new JSONObject();

        int responseCode=404;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] postDataBytes = postData.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();


            responseCode = connection.getResponseCode();

            connection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }
    public List[] getSharedLists(String urlString)throws IOException, JSONException{
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        /*header fields*/
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        try {
            urlConnection.connect();
        } catch (IOException e) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        JSONArray array = new JSONArray(sb.toString());
        Log.d("jsonArray",sb.toString());
        Log.d("jsonArray",String.valueOf(array.length()));
        List[] lists = new List[array.length()];
        for(int i=0;i<array.length();i++){
            JSONObject jsonObject = array.getJSONObject(i);
            List list = new List(jsonObject.getString("name"),jsonObject.getBoolean("shared"));
            list.setUsername(jsonObject.getString("creator"));
            lists[i] = list;
        }
        urlConnection.disconnect();
        return lists;
    }
    public int deleteList(String adress,String name , String username) throws IOException, JSONException{
        String urlString = adress + "lists/"+ username +"/" + name;
        HttpURLConnection urlConnection = null;
        java.net.URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestProperty("Accept","application/json");
        try {
            urlConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = urlConnection.getResponseCode();

        urlConnection.disconnect();
        return responseCode;
    }
    public Task[] getTasks(String adress , String name )throws IOException, JSONException{
        HttpURLConnection urlConnection = null;
        String urlString = adress + name;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        /*header fields*/
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        try {
            urlConnection.connect();
        } catch (IOException e) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        JSONArray array = new JSONArray(sb.toString());
        Task[] tasks = new Task[array.length()];
        for(int i=0;i<array.length();i++){
            JSONObject jsonObject = array.getJSONObject(i);
            Task task = new Task(jsonObject.getString("name"),jsonObject.getBoolean("done"),Integer.parseInt(jsonObject.getString("taskId")));
            tasks[i] = task;
        }
        Log.d("getTasks",String.valueOf(urlConnection.getResponseCode()));
        urlConnection.disconnect();
        return tasks;
    }
    public int addTask(String address,String name , String listName , boolean done , String taskId){
        String postData = "name="+name+"&list=" + listName+"&done=" + Boolean.toString(done) + "&taskId=" + taskId;

        int responseCode=404;
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] postDataBytes = postData.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();


            responseCode = connection.getResponseCode();

            connection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }
    public int deleteTask(String address,String id)throws IOException, JSONException{
        String urlString = address + "tasks/"+ id;
        HttpURLConnection urlConnection = null;
        java.net.URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestProperty("Accept","application/json");
        try {
            urlConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = urlConnection.getResponseCode();

        urlConnection.disconnect();
        return responseCode;
    }
    public int updateTask(String address , String id , boolean done){
        String postData = "done=" + done;
        String urlString = address + id;
        Log.d("update",urlString);
        int responseCode=404;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] postDataBytes = postData.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();


            responseCode = connection.getResponseCode();

            connection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }
    public String findId(String adress,String listName,int taskID)throws IOException, JSONException{
        String httpId="";
        HttpURLConnection urlConnection = null;
        String urlString = adress + listName;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        /*header fields*/
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        try {
            urlConnection.connect();
        } catch (IOException e) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        JSONArray array = new JSONArray(sb.toString());
        Task[] tasks = new Task[array.length()];
        for(int i=0;i<array.length();i++){
            JSONObject jsonObject = array.getJSONObject(i);
            if(Integer.parseInt(jsonObject.getString("taskId")) == taskID)
                httpId = jsonObject.getString("_id");

        }
        Log.d("getTasks",String.valueOf(urlConnection.getResponseCode()));
        urlConnection.disconnect();
        return httpId;
    }
}
