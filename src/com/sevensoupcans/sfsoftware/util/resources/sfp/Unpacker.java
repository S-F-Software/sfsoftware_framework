package com.sevensoupcans.sfsoftware.util.resources.sfp;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 *  Handles unpacking of legacy "SFP" files packed by the Yar Interactive Resource Packer
 *  
 * @author S. Thompson
 *
 */
public class Unpacker 
{
	// Maximum length of filenames specified by the packing tool
	private static final int MAXFILELEN = 64;
	
	public static ArrayList<String> getFileList(String BinFile) throws IOException
	{
		ArrayList<String> al = new ArrayList<String>();
										
		RandomAccessFile file = new RandomAccessFile(BinFile, "r");							
		
		// First two bytes contain the number of total files 
		byte [] countBytes = new byte[2];
		for(int i = 0; i < 2; i++)
		{
			countBytes[i] = file.readByte();
		}
		
		int fileCount = 0;
		fileCount += (countBytes[1] & 0x000000FF) << 8;
		fileCount += (countBytes[0] & 0x000000FF);										
		
		// Skip the remainder of the file header
		file.seek(10);		
		
		for(int j = 0; j < fileCount; j++)
		{
			// First four bytes represent file size
			byte[] chunk = new byte[4];
			for(int i = 0; i < 4; i++)
			{
				chunk[i] = file.readByte();
			}
			
			// Next four bytes represent file's start location (byte-wise)
			byte[] start = new byte[4];
			for(int i = 0; i < 4; i++)
			{
				start[i] = file.readByte();
			}			
			
			// Next 64 bytes contain the filename
			byte[] bytes = new byte[MAXFILELEN];
			for(int i = 0; i < MAXFILELEN; i++)
			{
				bytes[i] = file.readByte();
			}											
			
			al.add((new String(bytes, "UTF-8")).trim());
		}
		
		file.close();
		
		return al;
	}
	
	public static void extractFile(String BinFile, String DestDir, long ResKey)
	{		
		try 
		{								
			RandomAccessFile file = new RandomAccessFile(BinFile, "r");							
			
			// First two bytes contain the number of total files 
			byte [] countBytes = new byte[2];
			for(int i = 0; i < 2; i++)
			{
				countBytes[i] = file.readByte();
			}
			
			int fileCount = 0;
			fileCount += (countBytes[1] & 0x000000FF) << 8;
			fileCount += (countBytes[0] & 0x000000FF);
			
			System.out.println("Files: " + fileCount);							
			
			// Skip the remainder of the file header
			file.seek(10);

			ArrayList<PackedFile> packedFiles = new ArrayList<PackedFile>();
			
			for(int j = 0; j < fileCount; j++)
			{
				// First four bytes represent file size
				byte[] chunk = new byte[4];
				for(int i = 0; i < 4; i++)
				{
					chunk[i] = file.readByte();
				}
				
				// Next four bytes represent file's start location (byte-wise)
				byte[] start = new byte[4];
				for(int i = 0; i < 4; i++)
				{
					start[i] = file.readByte();
				}			
				
				// Next 64 bytes contain the filename
				byte[] bytes = new byte[MAXFILELEN];
				for(int i = 0; i < MAXFILELEN; i++)
				{
					bytes[i] = file.readByte();
				}						
				
				int fileSizeValue = 0;
				fileSizeValue += (chunk[3] & 0x000000FF) << 24;
				fileSizeValue += (chunk[2] & 0x000000FF) << 16;
				fileSizeValue += (chunk[1] & 0x000000FF) << 8;
				fileSizeValue += (chunk[0] & 0x000000FF);			
				
				int fileStartValue = 0;
				fileStartValue += (start[3] & 0x000000FF) << 24;
				fileStartValue += (start[2] & 0x000000FF) << 16;
				fileStartValue += (start[1] & 0x000000FF) << 8;
				fileStartValue += (start[0] & 0x000000FF);			
				
				String fileName = (new String(bytes, "UTF-8")).trim();
				PackedFile theFile = new PackedFile(fileName.toLowerCase(), fileStartValue, fileSizeValue);
				
				// Add the object to the ArrayList
				packedFiles.add(theFile);
											
			}
			
			for(PackedFile packedFile : packedFiles)
			{
				System.out.println(packedFile.toString());				
				
				DataOutputStream newFile = new DataOutputStream(new FileOutputStream(DestDir + packedFile.getFileName()));
				file.seek(packedFile.getFileStart() - 1);
				
				for(int i = 1; i <= packedFile.getFileSize(); i++)
				{					
					newFile.writeByte(file.readByte());
				}												
				newFile.close();				
										
			}
			
			file.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}		
	}		
}
