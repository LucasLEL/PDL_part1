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
		System.out.println("Veuillez entrer un crit�re de s�lection :");
		GestionnaireAPI wikidataAPI = null;

		JSONArray jsonArraySearch = null;

		while (jsonArraySearch == null) {
			criterion = sc.nextLine();
			try {
				wikidataAPI = new GestionnaireAPI(url, type + URLEncoder.encode(criterion, "UTF-8"));
				jsonObj = wikidataAPI.getJSON();
				jsonArraySearch = ((JSONArray) jsonObj.get("search"));
				if (jsonArraySearch.length() == 0) {
					jsonArraySearch = null;
				}
			} catch (IOException | JSONException e2) {
				e2.printStackTrace();
			}
			if (jsonArraySearch == null)
				System.out.println("Aucun r�sultat trouv� pour le crit�re : " + criterion
						+ ", veuillez saisir un nouveau crit�re : ");
		}

		System.out.println("R�sultat(s) pour '" + criterion + "'");

		try {
			for (int i = 0; i < jsonArraySearch.length(); i++) {
				JSONObject item = jsonArraySearch.getJSONObject(i);
				map.put(new Integer(i), (String) item.get("id"));
				String display = i + " - " + (String) item.get("title");

				if (!item.isNull("description"))
					display += " -> " + item.get("description");
				System.out.println("\n" + display);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		sc = new Scanner(System.in);
		System.out.println("\nVeuillez choisir un r�sultat parmi cette liste en r�f�rant l'id d�sir� : ");
		int idChoice = sc.nextInt();

		if (map.containsKey(idChoice))
			System.out.println(map.get(idChoice));
		else
			System.out.println("L'id " + idChoice + " n'existe pas");

		String pageLink = "https://www.wikidata.org/wiki/Special:EntityData/" + map.get(idChoice) + ".json";

		try {
			@SuppressWarnings("deprecation")
			String data = IOUtils.toString(new URL(pageLink));
			// entitites --> Q142 (url)
			String entityData = wikidataAPI.headerJson(data, map.get(idChoice));
			// labels --> fr --> value
			String title = wikidataAPI.labelAccess(entityData, "labels", "fr", "value");
			System.out.println("Titre : " + title);
			String description = wikidataAPI.labelAccess(entityData, "descriptions", "fr", "value");
			System.out.println("Description : " + description);
		} catch (IOException | ParseException e) {
			// e.printStackTrace();
			System.out.println("No entity with ID " + idChoice + " was found.");
		}

	}

}
