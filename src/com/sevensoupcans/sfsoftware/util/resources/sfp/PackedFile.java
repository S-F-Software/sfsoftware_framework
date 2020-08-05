package com.sevensoupcans.sfsoftware.util.resources.sfp;

public class PackedFile
{
	private String fileName;
	private int fileSize;
	private int fileStart;
	
	public PackedFile(String fileName, int fileStart, int fileSize)
	{
		this.fileName = fileName;
		this.fileStart = fileStart;		
		this.fileSize = fileSize;		
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public int getFileStart()
	{
		return fileStart;
	}
	
	public int getFileSize()
	{
		return fileSize;
	}
	
	public String toString()
	{
		return fileName + ", " + fileSize + " bytes. Starts at " + fileStart;
	}
}
