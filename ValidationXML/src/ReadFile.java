import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class ReadFile {
	public static Vector readCharStream(String fileName) throws Exception {
		Vector<StringBuffer> v = new Vector<StringBuffer>();
		FileReader fr = null;
		try {
			fr = new FileReader(fileName);
			int data = 0;
			StringBuffer sb = new StringBuffer();
			while ((data = fr.read()) != -1) {
				if (data == '\n' || data == '\r') {
					v.addElement(sb);
					sb = new StringBuffer();
				} else {
					sb.append((char) data);
				}

			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw e;

		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw e;
		} finally {
			if (fr != null)
				fr.close();

		}
		return v;

	}
	public static void main (String args[]) throws Exception{
		String fileName = "c:\\asdf";
		//StopWatch sw = new StopWatch();
	//	sw.start();
		Vector v1 = ReadFile.readCharStream(fileName);
	//	System.out.println(sw);
		System.out.println(v1.size());
		
		
	}

}
