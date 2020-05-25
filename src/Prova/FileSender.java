/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prova;

import java.io.Serializable;

/**
 *
 * @author PC
 */
public class FileSender implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String fileName;
    private long fileSize;

    public FileSender(String fileName,long fileSize)
    {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
    
    public FileSender() {}

    public String getFilename()
    {
        return fileName;
    }
    public void setFilename(String filename)
    {
        this.fileName = filename;
    }
    public long getFileSize()
    {
        return fileSize;
    }
    public void setFileSize(long fileSize)
    {
        this.fileSize = fileSize;
    }
}
