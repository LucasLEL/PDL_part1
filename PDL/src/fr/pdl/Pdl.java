package fr.pdl;

import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.json.simple.*;
import org.json.simple.parser.ParseException;

public class Pdl {

	/**
	 * Prends en entrée l'id de la recherche souhaitée.
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		String qId = "Q142"; //Q142 : France.
		String url = "https://www.wikidata.org/wiki/Special:EntityData/" + qId + ".json";
		try {
			String data = IOUtils.toString(new URL(url));
			// entitites --> Q142 (url)
			String entityData = headerJson(data, qId);
			// labels --> fr --> value
			String title = labelAccess(entityData, "labels", "fr", "value");
			System.out.println("Titre : " + title);
			String description = labelAccess(entityData, "descriptions", "fr", "value");
			System.out.println("Description : " + description);
		} catch (IOException | ParseException e) {
//			e.printStackTrace();
			System.out.println("No entity with ID "+ qId +" was found.");
		}
	}

	/**
	 * 
	 * @param urlPage : Lien vers le JSON.
	 * @return
	 * @throws ParseException
	 */
	public static String headerJson(String urlPage, String qId) throws ParseException {
		JSONObject entitiesJsonObject = (JSONObject) JSONValue.parseWithException(urlPage);
		String entities = entitiesJsonObject.get("entities").toString();
		JSONObject idJsonObject = (JSONObject) JSONValue.parseWithException(entities);
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
	 */
	public static String labelAccess(String entityData, String r1, String r2, String r3) throws ParseException {
		JSONObject categorieJson = (JSONObject) JSONValue.parseWithException(entityData);
		String categorie = categorieJson.get(r1).toString();
		JSONObject langueJson = (JSONObject) JSONValue.parseWithException(categorie);
		String langue = langueJson.get(r2).toString();
		JSONObject titleJson = (JSONObject) JSONValue.parseWithException(langue);
		String title = titleJson.get(r3).toString();
		return title;
	}

}
