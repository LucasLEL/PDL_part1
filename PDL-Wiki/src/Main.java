import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;


public class Main {

	private static Scanner sc;

	public static void main(String[] args) throws JSONException {
		
		String url = "https://www.wikidata.org/w/api.php?action=wbsearchentities&language=fr&format=json";
		String type = "&search=";
		String criterion = "";
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		JSONObject jsonObj = null;
		
		sc = new Scanner(System.in);
		System.out.println("Veuillez entrer un critère de sélection :");
		GestionnaireAPI wikidataAPI = null;
		
		JSONArray jsonArraySearch = null;
	  
		while(jsonArraySearch == null){
			criterion = sc.nextLine();
			try {
				wikidataAPI = new GestionnaireAPI(url,type + URLEncoder.encode(criterion, "UTF-8"));
				jsonObj = wikidataAPI.getJSON();
				jsonArraySearch = ((JSONArray) jsonObj.get("search"));
				if(jsonArraySearch.length() == 0){
					jsonArraySearch = null;
				}
			} catch (IOException | JSONException e2) {
				e2.printStackTrace();
			}
			if(jsonArraySearch == null) System.out.println("Aucun résultat trouvé pour le critère : " + criterion + ", veuillez saisir un nouveau critère : ");
		}
		
		System.out.println("Résultat(s) pour '" + criterion + "'");
		
		try {
			for (int i=0; i<jsonArraySearch.length(); i++) {
			    JSONObject item = jsonArraySearch.getJSONObject(i);
			    map.put(new Integer(i), (String) item.get("id"));
			    String display = i + " - " + (String) item.get("title");
			    
			    if(!item.isNull("description")) display += " -> " + item.get("description");	
			    System.out.println("\n" + display);
			} 
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		sc = new Scanner(System.in);
		System.out.println("\nVeuillez choisir un résultat parmi cette liste en référant l'id désiré : ");
		int idChoice = sc.nextInt();

		if(map.containsKey(idChoice)) System.out.println(map.get(idChoice));
		else System.out.println("L'id " + idChoice + " n'existe pas");
		
	
		String url2 = "https://www.wikidata.org/wiki/Special:EntityData/" + map.get(idChoice) + ".json";
		
		try {
			@SuppressWarnings("deprecation")
			String data = IOUtils.toString(new URL(url2));
			// entitites --> Q142 (url)
			String entityData = headerJson(data, map.get(idChoice));
			// labels --> fr --> value
			String title = labelAccess(entityData, "labels", "fr", "value");
			System.out.println("Titre : " + title);
			String description = labelAccess(entityData, "descriptions", "fr", "value");
			System.out.println("Description : " + description);
		} catch (IOException | ParseException e) {
//			e.printStackTrace();
			System.out.println("No entity with ID "+ idChoice +" was found.");
		}
		
		
		
	}
	
	/**
	 * 
	 * @param urlPage : Lien vers le JSON.
	 * @return
	 * @throws ParseException
	 * @throws JSONException 
	 */
	public static String headerJson(String urlPage, String qId) throws ParseException, JSONException {
		org.json.simple.JSONObject entitiesJsonObject = (org.json.simple.JSONObject) JSONValue.parseWithException(urlPage);
		String entities = entitiesJsonObject.get("entities").toString();
		org.json.simple.JSONObject idJsonObject = (org.json.simple.JSONObject) JSONValue.parseWithException(entities);
		String id = idJsonObject.get(qId).toString();
		return id;
	}

	/**
	 * 
	 * @param entityData
	 * Niveau de hiérarchie JSON : 
	 * @param r1 niveau de la racine 1
	 * @param r2 niveau de la racine 2
	 * @param r3 niveau de la racine 3
	 * @return
	 * @throws ParseException
	 * @throws JSONException 
	 */
	public static String labelAccess(String entityData, String r1, String r2, String r3) throws ParseException, JSONException {
		org.json.simple.JSONObject categorieJson = (org.json.simple.JSONObject) JSONValue.parseWithException(entityData);
		String categorie = categorieJson.get(r1).toString();
		org.json.simple.JSONObject langueJson = (org.json.simple.JSONObject) JSONValue.parseWithException(categorie);
		String langue = langueJson.get(r2).toString();
		org.json.simple.JSONObject titleJson = (org.json.simple.JSONObject) JSONValue.parseWithException(langue);
		String title = titleJson.get(r3).toString();
		return title;
	}

}
