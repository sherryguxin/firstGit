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
		System.out.println("result is :\n�ļ�������" + res.getFileCount()
				+ "\n�ļ�������֮�ͣ�" + res.getFileNumSum());
	}

	private FileResult calculateFiles(String path) throws IOException {
		FileResult res = new FileResult();
		res.setFileCount(0);
		res.setFileNumSum(0);
		File file = new File(path);
		if (file.isFile()) {
			System.out.println("is a file ");

			// ��ѹ��00�µ�����tar.gz�ļ�
			this.unTarGz(file,unPath);
		} else {
			System.out.println("is not a file ");
		}
		
		// ��ȡ���ļ����������ļ�������tar.gz�ģ������ܺͺ͸���
		
		res = this.readFile(this.unPath+"/00",res);
		
		return res;
	}

	private FileResult readFile(String path,FileResult res) {
		
		File f = new File(path);
		File[] files = f.listFiles(); // �õ�f�ļ�������������ļ���  
		for (File file : files) {
		    if(file.isDirectory()) {  
		        //��ε�ǰ·�����ļ��У���ѭ����ȡ����ļ����µ������ļ�  
		        readFile(file.getAbsolutePath(),res);  
		    } else  if(file.getAbsolutePath().indexOf(".tar.gz") == -1){
				// ��һ����ͨ�ļ������ж�ȡ
				res.setFileCount(res.getFileCount()+1);
				InputStreamReader reader;
				try {
					reader = new InputStreamReader(  
					        new FileInputStream(file));
					 // ����һ������������reader  
		            BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������  
		            String line = "";  
		            line = br.readLine(); // ֻ��һ���Ǹ���������ֻ��ȡ��һ������ 
		            
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
	 * ��ѹtar.gz �ļ�
	 * 
	 * @param file
	 *            Ҫ��ѹ��tar.gz�ļ�����
	 * @param outputDir
	 *            Ҫ��ѹ��ĳ��ָ����Ŀ¼��
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

				if (entry.isDirectory()) {// ��Ŀ¼

				} else {// ���ļ�

					File tmpFile = new File(outputDir + "/" + entry.getName());

					createDirectory(tmpFile.getParent() + "/", null);// �������Ŀ¼
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
			throw new IOException("��ѹ�鵵�ļ������쳣", ex);
		} finally {
			try {
				if (tarIn != null) {
					tarIn.close();
				}
			} catch (IOException ex) {
				throw new IOException("�ر�tarFile�����쳣", ex);
			}
		}

	}

	/**
	 * ����Ŀ¼
	 * 
	 * @param outputDir
	 * @param subDir
	 */
	public File createDirectory(String outputDir, String subDir) {

		File file = new File(outputDir);

		if (!(subDir == null || subDir.trim().equals(""))) {// ��Ŀ¼��Ϊ��

			file = new File(outputDir + "/" + subDir);
		}

		if (!file.exists()) {

			file.mkdirs();
		}
		return file;
	}
}