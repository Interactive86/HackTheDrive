package ix86.hackthedrive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class HackTheDrive extends Activity {
    public AQuery aq;
    public final String TAG = "ASDF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack_the_drive);

        aq = new AQuery(this);
        aq.id(R.id.button).clicked(this, "buttonClicked");
    }

    public void asyncJson(){

        //perform a Google search in just a few lines of code

        String url = "http://api.hackthedrive.com/vehicles/WBY1Z4C53EV273080/trunk/";
        aq.ajax(url, JSONObject.class, this, "jsonCallback");

    }

    public void jsonCallback(String url, JSONObject json, AjaxStatus status){
        if(json != null){
            //successful ajax call
            aq.id(R.id.test).text(json.toString());
        }else{
            //ajax error
        }
    }

    public void buttonClicked(View button) throws IOException {
        //asyncJson();
        if(button.getId()==R.id.notif) {
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.ic_launcher)
//                            .setContentTitle("My notification")
//                            .setContentText("Hello World!");
        }
        else {
            aq.id(R.id.test).clear();
            new DownloadTask().execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hack_the_drive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String result = null;

            try {
                long lStartTime = System.currentTimeMillis();
                result = getData(37.425758, -122.097807);
                long lEndTime = System.currentTimeMillis();
                long difference = lEndTime - lStartTime;
                Log.d(TAG, "SOAP download time (ms): " + difference);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            TextView test = (TextView)findViewById(R.id.test);
            test.setText(result.substring(0, 1000));
        }

        public String getData(double lat, double lng) throws MalformedURLException, IOException {
            //Code to make a webservice HTTP request
            String responseString = "";
            StringBuilder output = new StringBuilder();
            String user = "b207195d0b1684270db5aeae7970408c5179ce9f5a4dc1366937247";
            String pass = "167fb3e18980d8622f6a19fbbda3e01d";

            //String wsURL = "http://www.deeptraining.com/webservices/weather.asmx";
            String wsURL = "https://webservices.chargepoint.com/webservices/chargepoint/services/4.1";
            URL url = new URL(wsURL);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection)connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            String xmlInput =
                    "  <soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://litwinconsulting.com/webservices/\">\n" +
                            "	<soap:Header>\n" +
                            "		<wsse:Security xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd' soap:mustUnderstand='1'>\n" +
                            "			<wsse:UsernameToken xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd' wsu:Id='UsernameToken-261'>\n" +
                            "				<wsse:Username>" + user + "</wsse:Username>\n" +
                            "				<wsse:Password Type='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'>" + pass + "</wsse:Password>\n" +
                            "			</wsse:UsernameToken>\n" +
                            "		</wsse:Security>\n" +
                            "   </soap:Header>\n" +
                            "   <soap:Body>\n" +
                            "		<ns2:getPublicStations xmlns:ns2='http://www.example.org/coulombservices/'>\n" +
                            "			<searchQuery>\n" +
                            "				<Proximity>10</Proximity>\n" +
                            "				<proximityUnit>M</proximityUnit>\n" +
                            "				<Geo>\n" +
                            "					<Lat>" + lat  + "</Lat>\n" +
                            "					<Long>" + lng + "</Long>\n" +
                            "				</Geo>\n" +
                            "			</searchQuery>\n" +
                            "		</ns2:getPublicStations>\n" +
                            "   </soap:Body>\n" +
                            "  </soap:Envelope>\n";

            byte[] buffer = new byte[xmlInput.length()];
            buffer = xmlInput.getBytes();
            bout.write(buffer);
            byte[] b = bout.toByteArray();

            //String SOAPAction = "http://litwinconsulting.com/webservices/GetWeather";
            String SOAPAction = "urn:provider/interface/chargepointservices/getPublicStations";

            // Set the appropriate HTTP parameters.
            httpConn.setRequestProperty("Content-Length",
                    String.valueOf(b.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            OutputStream out = httpConn.getOutputStream();
            //Write the content of the request to the outputstream of the HTTP Connection.
            out.write(b);
            out.close();
            //Ready with sending the request.

            //Read the response.
            InputStreamReader isr =
                    new InputStreamReader(httpConn.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            //Write the SOAP message response to a String.
            while ((responseString = in.readLine()) != null) {
                output.append(responseString);
            }
            return output.toString();
        }
    }
}
