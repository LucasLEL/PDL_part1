import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * Classe GestionnaireAPI
 * Cette classe permet d'effectuer la gestion (appels, traitement des données recus) des différentes API
 */
public class GestionnaireAPI {

	private String url;
	private String criteria;

	/**
	 * Constructeur de la classe, qui initilise les paramètres
	 * @param url l'url de l'API
	 * @param criteria le critère de recherche
	 */
	public GestionnaireAPI(String url, String criteria) {
		this.url = url;
		this.criteria = criteria;
	}

	/**
	 * 
	 * Méthode getJSON() qui effectue un appel HTTP sur une API (avec url et critère, les paramètres de la classe
	 * @return le résutat de l'appel à l'API
	 * @throws IOException
	 * @throws JSONException
	 */
	public String getJSON() throws IOException, JSONException {

		String urlcriteria = this.url;
		urlcriteria += this.criteria;

		String jsonRet = "";
		URL searchURL = new URL(urlcriteria);
		URLConnection yc = searchURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			jsonRet += inputLine;
		}
		in.close();
		return jsonRet;
	}

	/**
	 * Méthode headerJson
	 * @param urlPage : Lien vers le JSON
	 * @return
	 * @throws ParseException
	 * @throws JSONException
	 */
	public String headerJson(String urlPage, String qId) throws ParseException, JSONException {
		org.json.simple.JSONObject entitiesJsonObject = (org.json.simple.JSONObject) JSONValue
				.parseWithException(urlPage);
		String entities = entitiesJsonObject.get("entities").toString();
		org.json.simple.JSONObject idJsonObject = (org.json.simple.JSONObject) JSONValue.parseWithException(entities);
		String id = idJsonObject.get(qId).toString();
		return id;
	}

	/**
	 * 
	 * Méthode labelAccess
	 * @param entityData Niveau de hiérarchie JSON :
	 * @param r1 niveau de la racine 1
	 * @param r2 niveau de la racine 2
	 * @param r3 niveau de la racine 3
	 * @return
	 * @throws ParseException
	 * @throws JSONException
	 */
	public String labelAccess(String entityData, String r1, String r2, String r3) throws ParseException, JSONException {
		org.json.simple.JSONObject categorieJson = (org.json.simple.JSONObject) JSONValue
				.parseWithException(entityData);
		String categorie = categorieJson.get(r1).toString();
		org.json.simple.JSONObject langueJson = (org.json.simple.JSONObject) JSONValue.parseWithException(categorie);
		String langue = langueJson.get(r2).toString();
		org.json.simple.JSONObject titleJson = (org.json.simple.JSONObject) JSONValue.parseWithException(langue);
		String title = titleJson.get(r3).toString();
		return title;
	}
	
	/**
	 * Accesseur de l'attribut url
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Modificateur de l'attribut url 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Accesseur de l'attribut criteria
	 * @return criteria
	 */
	public String getCriteria() {
		return criteria;
	}

	/**
	 * Modificateur de l'attribut criteria
	 * @param criteria
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
}
