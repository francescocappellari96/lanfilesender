/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template fileToSend, choose Tools | Templates
 * and open the template in the editor.
 */
package Prova;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * @author PC
 */
public class Sender
{
    private Socket socket = null;
    private OutputStream OUT_SOCKET = null;
    private BufferedOutputStream OUT_SOCKET_BUFFERED = null;
    private FileSender[] fileSender;
    private InputInvia i;
    private FileInputStream IN_FILE;
    private BufferedInputStream IN_FILE_BUFFERED;
    private File[] files = null;
    
    //crittografia simmetrica
    private CipherOutputStream OUT_SOCKET_BUFFERED_CRYPTED = null;
    private Cipher cipher = null;
    private static final String password = "zukowski";

    public Sender(InputInvia i)
    {
        this.i = i;
    }
    
    public void connect(String ip)
    {
        try
        {
            //creo lo stream normale
            socket = new Socket(ip, 4445);
            OUT_SOCKET = socket.getOutputStream();
            OUT_SOCKET_BUFFERED = new BufferedOutputStream(OUT_SOCKET);
            
            //creo la chiave di crittografia
            SecretKey secretKey = null;
            try
            {
                byte key[] = password.getBytes();
                DESKeySpec desKeySpec = new DESKeySpec(key);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                secretKey = keyFactory.generateSecret(desKeySpec);
            }
            catch(InvalidKeySpecException ex)
            {
                ex.printStackTrace();
            }
            catch(InvalidKeyException ex)
            {
                ex.printStackTrace();
            }
            catch(NoSuchAlgorithmException ex)
            {
                ex.printStackTrace();
            }
            
            //criptazione dello stream bufferizzato
            try
            {
                cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                OUT_SOCKET_BUFFERED_CRYPTED = new CipherOutputStream(OUT_SOCKET_BUFFERED, cipher);
            }
            catch (NoSuchAlgorithmException ex)
            {
                ex.printStackTrace();
            }
            catch(InvalidKeyException ex)
            {
                ex.printStackTrace();
            }
            catch (NoSuchPaddingException ex)
            {
                ex.printStackTrace();
            }
            
            
            i.getTextArea().setText("Connessione Stabilita.\n");

            //avvio il sender
            SenderT senderT = new SenderT();
            senderT.start();
        } catch (IOException e) {
            i.getTextArea().append("Connessione non riuscita o ascoltatore non in funzione.\n");
            //e.printStackTrace();
        }
    }

    public void sendFile()
    {
        //istanzio gli oggetti fileSender
        int cont;
        System.out.println("grandezza array files sender:" + files.length);
        fileSender = new FileSender[files.length];
        for (cont = 0; cont < files.length; cont++)
        {
            //System.out.println("dati del file N°: " + cont);
            //System.out.println("Nome: " + files[cont].getName());
            //System.out.println("Grandezza: " + files[cont].length());
            fileSender[cont] = new FileSender();
            fileSender[cont].setFilename(files[cont].getName());
            fileSender[cont].setFileSize(files[cont].length());
        }

        //String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("\\"), sourceFilePath.length());
        //fileSender.setFilename(fileName);
        //creazione stream di dati riguardanti le informazioni dei file da inviare
        String data = "";
        
        for(cont = 0;cont < fileSender.length;cont++)
        {
            data = data + "@" + fileSender[cont].getFilename() + "@" + fileSender[cont].getFileSize();
        }
        data = data + "@";
        
        try
        {
            OUT_SOCKET_BUFFERED_CRYPTED.write(intToByteArray(data.length()));
            OUT_SOCKET_BUFFERED_CRYPTED.flush();
            OUT_SOCKET_BUFFERED_CRYPTED.write(data.getBytes(),0,data.getBytes().length);
            OUT_SOCKET_BUFFERED_CRYPTED.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //-----------------------------------
        
        i.getTextArea().append("Invio files...\n");
        String textArea = i.getTextArea().getText();
        
        for (cont = 0; cont < files.length; cont++)
        {
            try
            {
                IN_FILE = new FileInputStream(files[cont]);
                IN_FILE_BUFFERED = new BufferedInputStream(IN_FILE);
                //----------------

                //long len = fileToSend.length();
                //fileSender.setFileSize(len);
                //fileSender.setStatus("Success");

                //testing jprogress bar
                i.getJProgressBar1().setMaximum((int) (fileSender[cont].getFileSize() / 1048576));
                i.getJProgressBar1().setMinimum(0);
                i.getJProgressBar1().setValue(0);
                //---------------------------------

                try
                {
                    byte[] buffer = new byte[8192];
                    int byteFile = 0;
                    long byteSend = 0;
                    long megaByteSend = 0;

                    //String textArea = i.getTextArea().getText();
                    i.getjButton3().setEnabled(true);

                    //-------Tempo rimanente trasferimento
                    long kiloByteRemaning = fileSender[cont].getFileSize() / 1024;
                    long timeRemaining = 0;
                    String downloadRemaining = null;
                    //------------------------------------

                    //--------velocità trasferimento
                    long byteSecond = 0;
                    //long kiloByteSecond = 0;
                    long kiloByteDisplayed = 0;
                    long currentTime = System.currentTimeMillis();
                    long second;
                    //----------------------------------------

                    while ((byteFile = IN_FILE_BUFFERED.read(buffer)) > -1)
                    {
                        OUT_SOCKET_BUFFERED_CRYPTED.write(buffer, 0, byteFile);
                        byteSend = byteSend + byteFile;
                        megaByteSend = byteSend / 1048576;
                        //-------Tempo rimanente trasferimento
                        kiloByteRemaning -= byteFile / 1024;
                        //------------------------------------

                        //----------jprogress bar
                        i.getJProgressBar1().setValue((int) megaByteSend);
                        //-----------------------

                        //--------velocità trasferimento
                        byteSecond = byteSecond + byteFile;
                        second = System.currentTimeMillis();
                        if ((second - currentTime) >= 1000) //1 secondo. Codice eseguito ogni secondo
                        {
                            kiloByteDisplayed = byteSecond / 1024;

                            //-----tempo rimanente download-----
                            timeRemaining = (long) ((double) kiloByteRemaning / kiloByteDisplayed) * 1000; //secondi to millisecondi
                            downloadRemaining = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(timeRemaining),
                                    TimeUnit.MILLISECONDS.toMinutes(timeRemaining)
                                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeRemaining)),
                                    TimeUnit.MILLISECONDS.toSeconds(timeRemaining)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemaining)));
                            //----------------------------------
                            byteSecond = 0;
                            currentTime = System.currentTimeMillis();
                        }
                        //-----------------------
                        i.getTextArea().setText(textArea + "FILE(" + (cont + 1) + "): " + fileSender[cont].getFilename() + "\n" + "Byte Inviati: " + byteSend + "\n" + "MegaByte Inviati: " + megaByteSend + "\n" + "Velocità trasmissione: " + kiloByteDisplayed + " KByte/s\n" + "Tempo Rimanente: " + downloadRemaining + "\n" + "KiloByte Rimanenti: " + kiloByteRemaning + "\n");
                    }
                    
                    i.getjButton3().setEnabled(false);
                    OUT_SOCKET_BUFFERED_CRYPTED.flush();
                    //i.getTextArea().append((cont + 1) + "° File inviato con successo.\n");
                } catch (IOException e)
                {
                    e.printStackTrace();
                    i.getTextArea().append("Errore di connessione o upload fermato volontariamente.\n");
                    i.getjButton3().setEnabled(false);
                    OUT_SOCKET_BUFFERED_CRYPTED.close();
                    OUT_SOCKET_BUFFERED.close();
                    OUT_SOCKET.close();
                    IN_FILE_BUFFERED.close();
                    IN_FILE.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            //chiusura degli stream
            OUT_SOCKET_BUFFERED_CRYPTED.flush();
            OUT_SOCKET_BUFFERED_CRYPTED.close();
            OUT_SOCKET_BUFFERED.close();
            OUT_SOCKET.close();
            IN_FILE_BUFFERED.close();
            IN_FILE.close();
            i.getTextArea().append("Per inviare un altro file, chiudere questa finestra e premere su \"invia\".\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public OutputStream getOUT_SOCKET() {
        return OUT_SOCKET;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
    
    public byte[] intToByteArray(int value)
    {
        return new byte[]
        {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value
        };
    }
    
    public class SenderT extends Thread
    {
        public void run()
        {
            setName("Sender - TCP");
            sendFile();
        }
    }
}
