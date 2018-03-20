import java.io.*;

import com.alibaba.fastjson.JSONObject;


public class HandleCompetitors {

	public static void main(String[] args) {
		
        JSONObject j = new JSONObject();
        j.put("data", 1);
        System.out.println(j.toJSONString());
        
		
		String fileName = args[0];
		String fileOut = args[1];
//		String fileName = "D:\\11.txt";
//		String fileOut = "D:\\22.txt";
		
		System.out.println("输入文件名是： " + fileName + ", 输出文件名：" + fileOut);
		
		
		FileInputStream in = null;
        BufferedReader reader = null;
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            in = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            out = new FileOutputStream(fileOut);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            
            String line = reader.readLine();
            while (line != null) {
                
                String buf[] = line.split("\\s+");
//                System.out.println(buf[0]);
//                System.out.println(buf[1]);
//                System.out.println(buf[2]);
                String info[] = buf[2].split(",");
                for(String i : info) {
                	writer.write(buf[0] + "," + buf[1] + "," + i);
                	writer.newLine();
                	writer.flush();
                }
                line = reader.readLine();
                
            }
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    writer.close();
                }
                if (in != null) {
                    in.close();
                    out.close();
                }
            } catch (Exception ex) {
                // Ignore this exception
            }
        }
        
	}
	

}