import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;

public class GestionnaireAPI {
	
	private String url;
	private String criterion;
	
	public GestionnaireAPI(String url, String criterion){
		this.url = url;
		this.criterion = criterion;
	}

	public JSONObject getJSON() throws IOException, JSONException{

		String urlCriterion = this.url;
		urlCriterion += this.criterion;
		
		String jsonRet ="";
		URL searchURL = new URL(urlCriterion);
		URLConnection yc = searchURL.openConnection();
		BufferedReader in = new BufferedReader(
		new InputStreamReader(
		yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null){
			jsonRet +=inputLine;
		}
		in.close();
		return new JSONObject(jsonRet);
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCriterion() {
		return criterion;
	}

	public void setCriterion(String criterion) {
		this.criterion = criterion;
	}
}
