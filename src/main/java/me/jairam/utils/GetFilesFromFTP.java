package me.jairam.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.net.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Class that can get entire directories from FTP Sites
 * 
 * If passed URL is directory, entire directory is downloaded
 * If passed URL is a file, the file will be downloaded
 * 
 * v1 skips subfolders. 
 *
 */
public class GetFilesFromFTP 
{
	FTPClient ftp;

	private boolean connect(URL url)
	{
		return connect(url, null, null);
	}

	private boolean connect(URL url, String username, String password)
	{
		if(ftp==null)
		{
			ftp = new FTPClient();
		}
		boolean success = true;
		try 
		{
			int reply;
			ftp.connect(url.getHost());
			System.out.println("Connecting to " + url.getHost() + "...");
			System.out.print(ftp.getReplyString());

			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();

			if(!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.err.println("FTP server refused connection.");
				throw new Exception("Connection failed");
			}

			if(username != null && password != null)
			{
				System.out.println("Logging in with the given usename and password...");
				if(!ftp.login(username, password))
				{
					throw new Exception("Login failed");
				}
			}

		}
		catch(IOException ioe) 
		{
			success = false;
			ioe.printStackTrace();

		}
		catch(Exception e)
		{
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	private boolean disconnect()
	{
		boolean success = true;
		try
		{
			ftp.logout();
		}
		catch(IOException IOE)
		{
			System.out.println("No connection present.\n"+IOE.getMessage());
			success = false;
		}
		finally 
		{
			if(ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch(IOException ioe) {
					// do nothing
				}
			}
		}

		return success;
	}


	public void copyToLocal(String username, String password, String remoteURI, String localURI) throws Exception
	{
		URL url = new URL(remoteURI);

		boolean connected = (username == null && password == null) ? connect(url): connect(url, username, password);

		if(!connected)
		{
			System.err.println("Could not connect to " + remoteURI);
			throw new Exception("Connection Error.");
		}

		File localDirectory = new File(localURI);
		if(localDirectory.exists())
		{
			System.out.println("File or Directory, " + localURI + " already exists closing...");
			disconnect();
			return;
		}

		// Be careful. If you don't list a path, it will try to download
		// the entire FTP site parent folder

		FTPFile[] files;
		if(url.getPath() != null)
		{ 
			files = ftp.listFiles(url.toString());
		}
		else
		{
			files  = ftp.listFiles();
		}

		FileOutputStream fos;

		if(files == null)
		{

		}
		else
		{
			for(FTPFile file: files)
			{
				if(file.isFile())
				{
					try 
					{
						fos = new FileOutputStream(localDirectory.getAbsolutePath()+File.pathSeparatorChar+file.getName());
						ftp.retrieveFile(file.getName(), fos);
						fos.close();
					}
					catch(IOException e)
					{
						System.out.println(e);
					}

				}
			}
		}
		disconnect();
	}




	public static void main( String[] args ) throws Exception
	{
		GetFilesFromFTP myFtpClient = new GetFilesFromFTP();

		myFtpClient.copyToLocal("anonymous", 
				"jairam.chandar@yahoo.com", 
				"ftp://ftp.ncbi.nlm.nih.gov/pub/pmc", 
				"/home/jairam/workspace/Documents1");
	}

	private static void printUsage()
	{
		System.err.println("java -cp <jar> me.jairam.utils.GetFilesFromFTP [<username> <password>]");
	}
}
