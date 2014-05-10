package algorithm.ucas.ac;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class CalculateNum {

	private String unPath = "/Users/guxin/Downloads/";

	public static void main(String[] args) throws IOException {

		CalculateNum cal = new CalculateNum();
		String path = "/Users/guxin/Downloads/00.tar.gz";
		FileResult res = cal.calculateFiles(path);
		System.out.println("result is :\n文件总数：" + res.getFileCount()
				+ "\n文件中数据之和：" + res.getFileNumSum());
	}

	private FileResult calculateFiles(String path) throws IOException {
		FileResult res = new FileResult();
		res.setFileCount(0);
		res.setFileNumSum(0);
		File file = new File(path);
		if (file.isFile()) {
			System.out.println("is a file ");

			// 解压缩00下得所有tar.gz文件
			this.unTarGz(file,unPath);
		} else {
			System.out.println("is not a file ");
		}
		
		// 读取新文件夹下所有文件，不是tar.gz的，计算总和和个数
		
		res = this.readFile(this.unPath+"/00",res);
		
		return res;
	}

	private FileResult readFile(String path,FileResult res) {
		
		File f = new File(path);
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。  
		for (File file : files) {
		    if(file.isDirectory()) {  
		        //如何当前路劲是文件夹，则循环读取这个文件夹下的所有文件  
		        readFile(file.getAbsolutePath(),res);  
		    } else  if(file.getAbsolutePath().indexOf(".tar.gz") == -1){
				// 是一个普通文件，进行读取
				res.setFileCount(res.getFileCount()+1);
				InputStreamReader reader;
				try {
					reader = new InputStreamReader(  
					        new FileInputStream(file));
					 // 建立一个输入流对象reader  
		            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
		            String line = "";  
		            line = br.readLine(); // 只有一个非负整数所以只读取第一行数据 
		            
		            System.out.println("line is "+line);
		            Integer sum = res.getFileNumSum();
		            sum = Integer.valueOf(line)+sum;
		            res.setFileNumSum(sum);
		            System.out.println("count is "+res.getFileCount()+"-- sum is "+res.getFileNumSum());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return res;
		
		
		
	}

	/**
	 * 解压tar.gz 文件
	 * 
	 * @param file
	 *            要解压的tar.gz文件对象
	 * @param outputDir
	 *            要解压到某个指定的目录下
	 * @throws IOException
	 */
	private void unTarGz(File file, String outputDir) throws IOException {

		TarArchiveInputStream tarIn = null;

		try {

			tarIn = new TarArchiveInputStream(new GZIPInputStream(
					new BufferedInputStream(new FileInputStream(file))),
					1024 * 2);

			TarArchiveEntry entry = null;

			while ((entry = tarIn.getNextTarEntry()) != null) {

				if (entry.isDirectory()) {// 是目录

				} else {// 是文件

					File tmpFile = new File(outputDir + "/" + entry.getName());

					createDirectory(tmpFile.getParent() + "/", null);// 创建输出目录
					OutputStream out = null;

					try {

						out = new FileOutputStream(tmpFile);

						int length = 0;

						byte[] b = new byte[2048];

						while ((length = tarIn.read(b)) != -1) {
							out.write(b, 0, length);
						}
						if (entry.getName().indexOf(".tar.gz")!=-1){

							String newPath = tmpFile.getAbsolutePath().replace(".tar.gz", "");
							newPath = newPath.substring(0,newPath.lastIndexOf("/"));
							System.out.println("newPath is "+newPath);
							System.out.println("path is "+tmpFile.getAbsolutePath()+"--"+newPath);
								
							this.unTarGz(tmpFile, newPath);	
						}
						
					} catch (IOException ex) {
						throw ex;
					} finally {

						if (out != null)
							out.close();
					}

				}
			}

		} catch (IOException ex) {
			throw new IOException("解压归档文件出现异常", ex);
		} finally {
			try {
				if (tarIn != null) {
					tarIn.close();
				}
			} catch (IOException ex) {
				throw new IOException("关闭tarFile出现异常", ex);
			}
		}

	}

	/**
	 * 构建目录
	 * 
	 * @param outputDir
	 * @param subDir
	 */
	public File createDirectory(String outputDir, String subDir) {

		File file = new File(outputDir);

		if (!(subDir == null || subDir.trim().equals(""))) {// 子目录不为空

			file = new File(outputDir + "/" + subDir);
		}

		if (!file.exists()) {

			file.mkdirs();
		}
		return file;
	}
}