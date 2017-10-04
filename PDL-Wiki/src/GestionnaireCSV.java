import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe GestionnaireCSV
 * Cette classe permet d'effectuer la gestion des fichiers CSV
 */
public class GestionnaireCSV {
	
	public void addHeader(String titreMatrice, String header, String data) throws IOException
	{
		FileWriter fileWriter = new FileWriter(titreMatrice+".csv");
		fileWriter.append(header);
		addDatas(data, fileWriter);
	}
	
	public void addDatas(String data, FileWriter fileWriter) throws IOException
	{
		fileWriter.append(data);
		fileWriter.close();
	}
}
