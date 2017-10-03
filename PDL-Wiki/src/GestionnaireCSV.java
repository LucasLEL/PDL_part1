import java.io.FileWriter;
import java.io.IOException;

public class GestionnaireCSV {
	
	public void addHeader(String header, String data) throws IOException
	{
		FileWriter fileWriter = new FileWriter("MonFichier.csv");
		fileWriter.append(header);
		addDatas(data, fileWriter);
	}
	
	public void addDatas(String data, FileWriter fileWriter) throws IOException
	{
		fileWriter.append(data);
		fileWriter.close();
	}
}
