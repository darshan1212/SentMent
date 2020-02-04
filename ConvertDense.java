import java.io.*;
import java.util.*;

public class ConvertDense{

	public static void main(String args[]) throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(new File("tfidf_l1.tsv")));
		BufferedWriter bw = new BufferedWriter(new FileWriter("tfidf_l2.tsv"));

		int rows=0;
		StringBuffer sbuf = new StringBuffer();
		while(br.ready()){
			String line[] = br.readLine().split("\t");

			for(int cols=0; cols<line.length; cols++){
				sbuf.append(rows);
				sbuf.append("\t");
				sbuf.append(cols);
				sbuf.append("\t");
				sbuf.append(line[cols]);
				sbuf.append("\n");
			}

			if(rows % 100 == 0){
				bw.write(sbuf.toString());
				sbuf = new StringBuffer("");
			}

			rows++;
		}

		if(!sbuf.toString().equals(""))
			bw.write(sbuf.toString());

		bw.close();
		br.close();
	}
}
