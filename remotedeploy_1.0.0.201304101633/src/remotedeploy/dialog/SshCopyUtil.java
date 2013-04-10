package remotedeploy.dialog;

import java.io.File;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;

public class SshCopyUtil {
	
	private static SCPClient client;
	private static SFTPv3Client sftpClient;
	
	public static String executeCommand() {
		if (ConfigDialog.host == null || ConfigDialog.user == null || ConfigDialog.passwd == null
				|| ConfigDialog.source == null || ConfigDialog.desc == null) {
			System.err.println("Can't execute SCP command. Please check \"hostname\" \"username\" and \"password\"");
			System.exit(1);
			return "check your config properties,every field must not be null";
		}
		Connection conn = new Connection(ConfigDialog.host);
		try {
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(ConfigDialog.user, ConfigDialog.passwd);
			if (isAuthenticated == false) {
				System.err.println("Authenticated false!!!");
				return "Authenticated false,check your config is right or not";
			}
			client = new SCPClient(conn);
			sftpClient = new SFTPv3Client(conn);  
			try{
				sftpClient.rmdir(ConfigDialog.desc);	
			}catch(Exception e){
//				e.printStackTrace();
			}
			try{
				sftpClient.mkdir(ConfigDialog.desc, 0755);
			}catch(Exception e){
//				e.printStackTrace();
			}
			copy(new File(ConfigDialog.source));
//			client.put(ConfigDialog.source, ConfigDialog.desc); // �����ǽ������ļ��ϴ����������˵�Ŀ¼��
//			client.get("/home/liuwei/lwtest.txt", "/home/liuwei/shelltest/"); // �����ǽ��������˵��ļ����ص����ص�Ŀ¼��
//			conn.close();
		} catch (IOException ex) {
//			ex.printStackTrace();
		}
		return "success";
	}
	
	public static void copy(File source){
		if(source.isFile()){
			put(source.getPath());
		}else{
			try {
				String remoteDir = ConfigDialog.desc + source.getPath().substring(ConfigDialog.source.length()).replace(File.separator, "/");
				sftpClient.mkdir(remoteDir, 0755);
			} catch (IOException e) {
//				e.printStackTrace();
			} //Զ���½�Ŀ¼ ,�ڶ��������Ǵ������ļ��еĶ�дȨ��
			File[] files = source.listFiles();
			for(File file: files){
				copy(file);
			}
		}
	}

	public static void put(String sourceFile){
		try {
			if(new File(sourceFile).isDirectory()){
				try {
					String remoteDir = ConfigDialog.desc + sourceFile.substring(ConfigDialog.source.length()).replace(File.separator, "/");
					sftpClient.mkdir(remoteDir, 0755);
				} catch (IOException e) {
//					e.printStackTrace();
				} //Զ���½�Ŀ¼ ,�ڶ��������Ǵ������ļ��еĶ�дȨ��
				copy(new File(sourceFile));
				return ;
			}
			String remoteFile = ConfigDialog.desc + sourceFile.substring(ConfigDialog.source.length()).replace(File.separator, "/");
			if(remoteFile.length() == ConfigDialog.desc.length()){
				client.put(sourceFile, remoteFile, ConfigDialog.filemode);
				return;
			}
			client.put(sourceFile, remoteFile.substring(0, remoteFile.lastIndexOf("/")), ConfigDialog.filemode);
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}
	
}
