import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Classe MainWikidata
 * Cette classe est le lanceur de l'application pour tester et utiliser les APIs de Wikidata.
 *
 */
public class MainWikidata {

	private static Scanner sc;

	/**
	 * Main lanceur de l'application
	 * @param args arguments du main
	 * @throws JSONException exception qui gère les exception JSON du main
	 */
	public static void main(String[] args) throws JSONException {

		String url = "https://www.wikidata.org/w/api.php?action=wbsearchentities&language=fr&format=json";
		String type = "&search=";
		String criteria = "";
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		JSONObject jsonObj = null;
		int numberCriterion = 0;
		String headerCsv="Titre,Description\n";
		String dataCsv="";
		String titleMatrix = "Export-Matrice";

		
		System.out.println("Veuillez entrer le nombre de critères désirés :");
		sc = new Scanner(System.in);
		
		try{
			numberCriterion = sc.nextInt();
		}
		 catch (Exception e) {
			 numberCriterion = 0;
		}
		
		while(numberCriterion <= 0){
			System.out.println("Nombre de critères incorrecte, ressaisissez un nombre :");
			sc = new Scanner(System.in);
			try{
				numberCriterion = sc.nextInt();
			}
			 catch (Exception e) {
				 numberCriterion = 0;
			}
		}
		
		for(int j = 0; j < numberCriterion; j++){
			System.out.println("##################################################");
			System.out.println("Critère " + (j+1) + " Veuillez entrer un critère de sélection :");
			sc = new Scanner(System.in);
			
			GestionnaireAPI wikidataAPI = null;

			JSONArray jsonArraySearch = null;

			while (jsonArraySearch == null) {
				criteria = sc.nextLine();
				try {
					wikidataAPI = new GestionnaireAPI(url, type + URLEncoder.encode(criteria, "UTF-8"));
					String stringResult = wikidataAPI.getJSON();
					jsonObj = new JSONObject(stringResult);
					jsonArraySearch = ((JSONArray) jsonObj.get("search"));
					if (jsonArraySearch.length() == 0) {
						jsonArraySearch = null;
					}
				} catch (IOException | JSONException e2) {
					e2.printStackTrace();
				}
				if (jsonArraySearch == null)
					System.out.println("Aucun résultat trouvé pour le critère : " + criteria
							+ ", veuillez saisir un nouveau critère : ");
			}

			System.out.println("\nRésultat(s) pour '" + criteria + "' : ");

			try {
				for (int i = 0; i < jsonArraySearch.length(); i++) {
					JSONObject item = jsonArraySearch.getJSONObject(i);
					map.put(new Integer(i), (String) item.get("id"));
					String display = i + " - " + (String) item.get("title");

					if (!item.isNull("description"))
						display += " -> " + item.get("description");
					System.out.println(display);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			System.out.println("\nVeuillez choisir un résultat parmi cette liste en référant l'id désiré : ");
			
			int idChoice = -1; //-1 -> id non valide
			try{
				idChoice = sc.nextInt();
			}
			 catch (Exception e) {
			}

			while(!map.containsKey(idChoice) || idChoice == -1){
				if(idChoice != -1){
					System.out.println("L'id " + idChoice + " n'existe pas, veuillez entrer un id valide :");
					idChoice = -1; //id non valide
				}
				
				try{
					idChoice = sc.nextInt();
				}
				 catch (Exception e) {
					 System.out.println("La valeur entrée '" + sc.nextLine() + "' n'est pas du type entier, veuillez réessayer : ");
				}
			}
			System.out.println(map.get(idChoice));

			String pageLink = "https://www.wikidata.org/wiki/Special:EntityData/" + map.get(idChoice) + ".json";

			try {
				//Changement des attributs dans le GestionnaireAPI pour maintenant récupérer l'entity correpondante
				wikidataAPI.setUrl(pageLink);
				wikidataAPI.setCriteria("");
				
				//Appels de la méthode getJSON() pour effectuer l'appel � l'API Wikidata
				String data = wikidataAPI.getJSON();
				
				// entitites --> Q142 (url)
				String entityData = wikidataAPI.headerJson(data, map.get(idChoice));
				
				// labels --> fr --> value
				String title = wikidataAPI.labelAccess(entityData, "labels", "fr", "value");
				System.out.println("Titre : " + title);
				String description = wikidataAPI.labelAccess(entityData, "descriptions", "fr", "value");
				System.out.println("Description : " + description);
				
				//Génération CSV
				dataCsv += title + "," + description + "\n";			
				
			} catch (IOException | ParseException e) {
				// e.printStackTrace();
				System.out.println("Aucune entité trouvée pour l'ID " + idChoice);
			}
		}
		GestionnaireCSV gestionnaire = new GestionnaireCSV();
		try{
			gestionnaire.addHeader(titleMatrix, headerCsv, dataCsv);
			System.out.println("##################################################");
			System.out.println("Génération du csv '" + titleMatrix + ".csv' OK");
		} catch (Exception e){
			System.out.println("\nErreur lors de la génération du CSV");
		}
	}

}
