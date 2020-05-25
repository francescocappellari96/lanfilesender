/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prova;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.Action;
import javax.swing.JFileChooser;

/**
 *
 * @author PC
 */
public class Receiver {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private InputStream IN_SOCKET = null;
    private BufferedInputStream IN_SOCKET_BUFFERED = null;
    private ArrayList<FileSender> fileSender;
    private File dstFile = null;
    private FileOutputStream OUT_FILE = null;
    private InputRiceviProgress i;
    private JFileChooser jChooser;
    private BufferedOutputStream OUT_FILE_BUFFERED;
    private static final String defaultDirectoryDownload = "file_ricevuti/"; //directory default del download
    private AccepterT accepterT;
    
    //crittografia simmetrica
    private CipherInputStream IN_SOCKET_BUFFERED_CRYPTED = null;
    private Cipher cipher = null;
    private static final String password = "zukowski";

    public Receiver(InputRiceviProgress i,ServerSocket serverSocket) throws IOException {
        this.i = i;
        this.serverSocket = serverSocket;
        //accepter.start();
        //thread avviato ad apertura schermata, a ricevimento completato e a ricevimento fallito
        accepterT = new AccepterT();
        accepterT.start();
    }

    public void download(String percorso) throws IOException
    {
        try
        {
            int cont;
            
            //lettura informazioni sui file
            fileSender = new ArrayList<FileSender>();
            boolean reading = true;
            int dataLength;                            //lunghezza della stringa da ricevere
            byte[] data;                               //array contentente i dati sui file
            byte[] dataLengthBytes = new byte[4];      //int contente la lunghezza della stringa
            
            //leggo la lunghezza dei dati in arrivo
            IN_SOCKET_BUFFERED_CRYPTED.read(dataLengthBytes);
            dataLength = fromByteArray(dataLengthBytes);
            
            //preparo l'array di byte che conterrà i dati veri e propri
            data = new byte[dataLength];
            IN_SOCKET_BUFFERED_CRYPTED.read(data);
            
            StringTokenizer fileInformation = new StringTokenizer(new String(data),"@");
            while(reading)
            {
                try
                {
                    fileSender.add(new FileSender(fileInformation.nextToken(),Long.valueOf(fileInformation.nextToken()).longValue()));
                }
                catch(Exception e)
                {
                    //e.printStackTrace();
                    reading = false;
                }
            }
            for(cont = 0;cont < fileSender.size();cont++)
            {
                System.out.println("-----");
                System.out.println(fileSender.get(cont).getFilename());
                System.out.println(fileSender.get(cont).getFileSize());
            }
            
            //fileSender = (FileSender[]) IN_SOCKET.readObject();
            //-------------------
            
            i.getTextArea().append("File in arrivo: " + "\n");
            for(cont = 0;cont < fileSender.size();cont++)
            {
                i.getTextArea().append((cont + 1) + "):--->" + fileSender.get(cont).getFilename() + " Grandezza in byte: " + fileSender.get(cont).getFileSize() + "\n");
            }
            
            i.getTextArea().append("Avvio download file\n");
            
            String outputFile;

            String textArea = i.getTextArea().getText();
            
            for (cont = 0; cont < fileSender.size(); cont++)
            {
                outputFile = percorso + fileSender.get(cont).getFilename();

                if (!new File(percorso).exists())
                {
                    new File(percorso).mkdirs();
                }
                
                //testing jprogress bar
                i.getJProgressBar1().setMaximum((int) (fileSender.get(cont).getFileSize() / 1048576));
                i.getJProgressBar1().setMinimum(0);
                i.getJProgressBar1().setValue(0);
                //---------------------------------

                //crezione dell'oggetto File in base al persorso del file
                dstFile = new File(outputFile);
                OUT_FILE = new FileOutputStream(dstFile);
                OUT_FILE_BUFFERED = new BufferedOutputStream(OUT_FILE);

                byte[] buffer = new byte[8192];
                int byteFile = 0;
                long byteReceived = 0;
                long megaByteReceived = 0;
                //String textArea = i.getTextArea().getText();

                //--------velocità trasferimento
                long byteSecond = 0;
                //long kiloByteSecond = 0;
                long kiloByteDisplayed = 0;
                long currentTime = System.currentTimeMillis();
                long second;
                //----------------------------------------

                //-------Tempo rimanente trasferimento
                long kiloByteRemaning = fileSender.get(cont).getFileSize() / 1024;
                long timeRemaining = 0;
                String downloadRemaining = null;
                //------------------------------------

                i.getjButton1().setEnabled(true);
                
                
                long byteCounter = 0;
                
                //meccanismo che permette di riceve byte di molti file assieme, senza separatori tra un file e l'altro
                while(byteCounter < fileSender.get(cont).getFileSize())
                {
                    if(fileSender.get(cont).getFileSize() - byteCounter < buffer.length)
                    {
                        byteFile = IN_SOCKET_BUFFERED_CRYPTED.read(buffer, 0, (int)(fileSender.get(cont).getFileSize()-byteCounter));
                        
                        //System.out.println("numero file: " + cont);
                        //System.out.println("Byte salvati per ciclo: " + byteFile);
                        
                        byteCounter += byteFile;
                        
                        //System.out.println("byteCounter: " + byteCounter);
                        
                        OUT_FILE_BUFFERED.write(buffer, 0, byteFile);
                        OUT_FILE_BUFFERED.flush();
                    }
                    else
                    {
                        //System.out.println("numero file: " + cont);
                        //System.out.println("Byte salvati per ciclo: " + byteFile);
                        //System.out.println("byteCounter: " + byteCounter);
                        
                        byteFile = IN_SOCKET_BUFFERED_CRYPTED.read(buffer);
                        byteCounter+=byteFile;
                        OUT_FILE_BUFFERED.write(buffer, 0, byteFile);
                    }
                    
                    //OUT_FILE_BUFFERED.write(buffer, 0, byteFile); minto
                    byteReceived = byteReceived + byteFile;
                    megaByteReceived = byteReceived / 1048576;

                    //-------Tempo rimanente trasferimento
                    kiloByteRemaning -= byteFile / 1024;
                    //------------------------------------

                    //--------------aggiornamento jprogressbar
                    i.getJProgressBar1().setValue((int) megaByteReceived);
                    //--------------

                    //--------------velocità trasferimento
                    byteSecond = byteSecond + byteFile;
                    second = System.currentTimeMillis();
                    
                    if ((second - currentTime) >= 1000) //1 secondo
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

                        //System.out.println("kilobyte secondo: " + kiloByteSecond + " tempo rimanente: " + timeRemaining);
                        
                        byteSecond = 0;
                        currentTime = System.currentTimeMillis();
                    }
                    //-----------------------
                    i.getTextArea().setText(textArea + "FILE(" + (cont + 1) + "): " + fileSender.get(cont).getFilename() + "\n" + "Byte Ricevuti: " + byteReceived + "\n" + "MegaByte Ricevuti: " + megaByteReceived + "\n" + "Velocità trasmissione: " + kiloByteDisplayed + " KByte/s\n" + "Tempo Rimanente: " + downloadRemaining + "\n" + "KiloByte Rimanenti: " + kiloByteRemaning + "\n");
                }
                i.getjButton1().setEnabled(false);
            }
            
            OUT_FILE_BUFFERED.flush();
            OUT_FILE_BUFFERED.close();
            OUT_FILE.close();
            IN_SOCKET_BUFFERED_CRYPTED.close();
            IN_SOCKET_BUFFERED.close();
            IN_SOCKET.close();
            //i.getTextArea().append("Per ricevere un altro file, chiudere questa finestra e premere su \"ricevi\".");
            
            //chiusura finestra a fine download
            this.i.getInputRicevi().getArrayInputRiceviProgress().remove(this.i);
            this.i.getInputRicevi().tryDisableJButton1();
            this.i.dispose();
            
            //riavvio thread a ricevimento completato
            //accepterT = new AccepterT();
            //accepterT.start();
        }
        catch (IOException e)
        {
            i.getTextArea().append("Errore di connessione o upload fermato volontariamente.\n");
            i.getjButton1().setEnabled(false);
            IN_SOCKET_BUFFERED_CRYPTED.close();
            IN_SOCKET_BUFFERED.close();
            IN_SOCKET.close();
            OUT_FILE_BUFFERED.close();
            OUT_FILE.close();
            dstFile.delete();
            
            this.i.getInputRicevi().getArrayInputRiceviProgress().remove(this.i);
            this.i.getInputRicevi().tryDisableJButton1();
            
            e.printStackTrace();
            //riavvio thread a ricevimento fallito
            //accepterT = new AccepterT();
            //accepterT.start();
            return;
        }
        /*
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            IN_SOCKET_BUFFERED.close();
            IN_SOCKET.close();
            OUT_FILE_BUFFERED.close();
            OUT_FILE.close();
            dstFile.delete();
            //accepterT = new AccepterT();
            //accepterT.start();
            this.i.getInputRicevi().getArrayInputRiceviProgress().remove(this.i.getInputRicevi());
            this.i.getInputRicevi().tryDisableJButton1();
            return;
        }
        */
    }

    public ServerSocket getServerSocket()
    {
        return this.serverSocket;
    }

    public Socket getSocket()
    {
        return this.socket;
    }
    
    public AccepterT getAccepterT() {
        return this.accepterT;
    }
    
    int fromByteArray(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getInt();
    }
    
    /*
    
    
    Thread accepter = new Thread()
    {
        public void run()
        {
            try
            {
                //accetto la connessione
                String percorso = "nessuna directory";
                socket = serverSocket.accept();
                i.getTextArea().append("Connessione accettata da: " + socket.getInetAddress().getHostAddress() + "\n");
                
                //salvo l'IN_SOCKET
                IN_SOCKET = new ObjectInputStream(socket.getInputStream());
                IN_SOCKET_BUFFERED = new BufferedInputStream(IN_SOCKET);

                //in base alla jComboBox, salvo o nella directory del programma, accettando e salvando automaticamente i file in arrivo, o appare il jFileChooser
                
                if(i.getjComboBox1().getSelectedIndex() == 0)
                {
                    System.out.println("Default Directory");
                    
                     percorso = defaultDirectoryDownload;
                    
                }
                else
                {
                    jChooser = new JFileChooser();
                    //titolo
                    jChooser.setDialogTitle("Seleziona il percorso di destinazione del file");
                    //aperto in modalità "salvataggio"
                    jChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    //attivata la modalità dettagli dell'oggetto
                    Action details = jChooser.getActionMap().get("viewTypeDetails");
                    details.actionPerformed(null);
                    //filtro impostato a tutti i file
                    jChooser.setAcceptAllFileFilterUsed(false);
                    jChooser.showSaveDialog(null);
                    //ottengo il percorso del file
                    
                    try
                    {
                        percorso = jChooser.getSelectedFile().getAbsolutePath();
                    }catch(NullPointerException e)
                    {
                        IN_SOCKET.close();
                    }
                    System.out.println(percorso);
                }
                
                i.getTextArea().append("directory selezionata: " + percorso + "\n");
                i.getTextArea().append("Avvio download file\n");
                
                //avvio download
                try
                {
                    download(percorso);
                }catch(Exception e)
                {
                    IN_SOCKET.close();
                }
            }
            catch (IOException ex)
            {
                
                ex.printStackTrace();
            }
        }
    };
    
    */
    
    
    public class AccepterT extends Thread
    {
        public void run()
        {
            setName("Receiver - TCP");
            try
            {
                //accetto la connessione
                String percorso = null;
                
                socket = serverSocket.accept();
                System.out.println(serverSocket.getLocalSocketAddress());
                
                //la schermata non appare finchè non arriva qualche richiesta
                i.setTitle("Lan File Sender - " + socket.getInetAddress().getHostAddress());
                i.setVisible(true);
                
                //creazione nuovo ascoltatore
                i.getInputRicevi().getArrayInputRiceviProgress().add(new InputRiceviProgress(serverSocket,i.getInputRicevi()));
                
                i.getTextArea().setText("Connessione accettata da: " + socket.getInetAddress().getHostAddress() + "\n");
                i.getjLabel3().setText(socket.getInetAddress().getHostAddress());
                
                //salvo l'IN_SOCKET
                IN_SOCKET = socket.getInputStream();
                IN_SOCKET_BUFFERED = new BufferedInputStream(IN_SOCKET);

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
                
                //creo lo stream di decifratura
                try
                {
                    cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    IN_SOCKET_BUFFERED_CRYPTED = new CipherInputStream(IN_SOCKET_BUFFERED, cipher);
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
                
                //in base alla jComboBox, salvo o nella directory del programma, accettando e salvando automaticamente i file in arrivo, o appare il jFileChooser
                if(i.getInputRicevi().getjComboBox1().getSelectedIndex() == 0)
                {
                    System.out.println("Default Directory");
                    
                    percorso = defaultDirectoryDownload;
                }
                else
                {
                    jChooser = new JFileChooser();
                    //titolo
                    jChooser.setDialogTitle("Seleziona il percorso di destinazione del file");
                    //aperto in modalità "salvataggio"
                    jChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    //attivata la modalità dettagli dell'oggetto
                    Action details = jChooser.getActionMap().get("viewTypeDetails");
                    details.actionPerformed(null);
                    //filtro impostato a tutti i file
                    jChooser.setAcceptAllFileFilterUsed(false);
                    jChooser.showSaveDialog(null);
                    //ottengo il percorso del file
                    try
                    {
                        percorso = jChooser.getSelectedFile().getAbsolutePath() + "\\";
                    }
                    catch(NullPointerException e)
                    {
                        IN_SOCKET_BUFFERED_CRYPTED.close();
                        IN_SOCKET_BUFFERED.close();
                        IN_SOCKET.close();
                        //accepterT = new AccepterT();
                        //accepterT.start();
                        i.getInputRicevi().getArrayInputRiceviProgress().remove(i);
                        i.getInputRicevi().tryDisableJButton1();
                        return;
                    }
                    
                    System.out.println(percorso);
                }
                
                i.getTextArea().append("directory selezionata: " + percorso + "\n");
                
                //avvio download
                try
                {
                    //fix bug nullPointerException in caso non si accettasse la richiesta di ricezione
                    i.getInputRicevi().tryEnableJButton1();
                    download(percorso);
                }
                catch(NullPointerException e)
                {
                    IN_SOCKET_BUFFERED_CRYPTED.close();
                    IN_SOCKET_BUFFERED.close();
                    IN_SOCKET.close();
                    //accepterT = new AccepterT();
                    //accepterT.start();
                    i.getInputRicevi().getArrayInputRiceviProgress().remove(i);
                    i.getInputRicevi().tryDisableJButton1();
                    return;
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
