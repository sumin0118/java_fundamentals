import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

public class ReadFile2 {
	public static Vector<String> readBufferedReader(String fileName)
			throws Exception {

		Vector<String> v = new Vector<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String data;
			while ((data = br.readLine()) != null) {
				v.addElement(data);
			}

		} catch (Exception e) {
			System.err.print(e.getMessage());
			throw e;
		} finally {
			if (br != null)
				br.close();

		}
		return v;
	}

}
