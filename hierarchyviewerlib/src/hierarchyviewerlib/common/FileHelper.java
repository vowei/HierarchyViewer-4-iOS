package hierarchyviewerlib.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class FileHelper {
	 public static byte[] readFileToByte(String path) throws IOException
     {
     	byte[] b=null;
     	RandomAccessFile f = new RandomAccessFile(path, "r");
		b=new byte[(int)f.length()];
		f.read(b);
     	return b;
     }
	 
	 public static String readFileToString(String path) throws IOException
	 {
		 byte[] bs=readFileToByte(path);
		 return new String(bs);
	 }
	 
	 public static void writeStringToFile(String fileName,String content) throws IOException
	 {
		 BufferedWriter out = new BufferedWriter(new FileWriter(fileName,false));
		 out.write(content);
		 out.close();
	 }
	 
	 public static boolean rename(String path, String oldName, String newName)
	 {
		 File oldFile =new File(path+File.separator+oldName);
		 File newFile =new File(path+File.separator+newName);
		 return oldFile.renameTo(newFile);
	 }
	 
	 public static boolean move(String oldPath, String newPath, String filefolderName)
	 {
		 File oldFile=new File(oldPath+File.separator+filefolderName);
		 File newFIle = new File(newPath+File.separator+filefolderName);
		 return oldFile.renameTo(newFIle);
	 }
	 
	 public static boolean createDirection(String dirPath)
	 {
		 File dirFile = new File(dirPath);  
		 return dirFile.mkdirs(); 
	 }
	 
	 public static void replace(String path, String oldText,String newText) throws IOException
	 {
		 File file = new File(path);
         BufferedReader reader = new BufferedReader(new FileReader(file));
         String line="";
         String content="";
         while((line = reader.readLine()) != null)
         {
        	 content += line + "\r\n";
         }
         reader.close();
         content = content.replaceAll(oldText, newText);
         FileWriter writer = new FileWriter(path);
         writer.write(content);
         writer.close();
	 }
	 
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				//int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("error  ");
			e.printStackTrace();
		}
	}
	
	  // 处理目录  
    static public void copyDirectory(String source, String target) {
    	File sourceFile = new File(source);
        File[] file = sourceFile.listFiles();// 得到源文件下的文件项目  
        for (int i = 0; i < file.length; i++) {  
        	String subFileSource = source + "/" + file[i].getName();  
            String subFileTarget = target + "/" + file[i].getName();  
            
            if (file[i].isFile()) {// 判断是文件  
                copyFile(subFileSource, subFileTarget);  
            }  
            if (file[i].isDirectory()) {// 判断是文件夹  
                File subFileTargetFile =new File(subFileTarget);
                subFileTargetFile.mkdirs();// 建立文件夹  
                copyDirectory(subFileSource, subFileTarget);  
            }  
        }
    }
    
    static public void setFolderPermission(String folderPath,String permission)
    {
    	File folderFile = new File(folderPath);
        File[] file = folderFile.listFiles();// 得到源文件下的文件项目  
        for (int i = 0; i < file.length; i++) {  
        	String subFile = folderPath + "/" + file[i].getName();  
            
            if (file[i].isFile()) {// 判断是文件  
            	setFilePermission(subFile,permission);
            }  
            if (file[i].isDirectory()) {// 判断是文件夹  
                setFolderPermission(subFile, permission);  
            }  
        }
    }
    
     static public void setFilePermission(String filePath, String permission)
     {
		try {
			String[] cmd = { "chmod", permission, filePath };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
}
