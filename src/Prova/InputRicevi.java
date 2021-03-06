/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prova;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;

/**
 *
 * @author PC
 */
public class InputRicevi extends javax.swing.JFrame implements WindowListener
{
    private RisponditoreLan risponditoreLan = null;
    private ArrayList<InputRiceviProgress> irp = new ArrayList<InputRiceviProgress>();
    private ServerSocket serverSocket = null;

    public InputRicevi() throws IOException
    {
        initComponents();
        //set immagine della finestra
        Image immagineIc = null;
        try
        {
            immagineIc = ImageIO.read(InputRicevi.class.getClassLoader().getResourceAsStream("img/Ricevi1.png"));
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        this.setIconImage(immagineIc);
        addWindowListener(this);
        jTextArea1.append("In attesa di connessioni...\n");
        this.jButton1.setEnabled(false);
        
        //serverSocket
        this.serverSocket = new ServerSocket(4445);
        irp.add(new InputRiceviProgress(serverSocket,this));
        
        //ascoltatore
        risponditoreLan = new RisponditoreLan();
        risponditoreLan.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lan File Sender - Ricevi");
        setResizable(false);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setFocusable(false);
        jTextArea1.setRequestFocusEnabled(false);
        jTextArea1.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText("Reporter:");

        jButton1.setText("Interrompi tutti i download");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Directory Programma", "Richiesta" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //dopo aver chiuso tutti gli stream, libero l'array
        //System.out.println(irp.size());
        for(int cont = 0;cont < irp.size();cont++)
        {
            try
            {
                irp.get(cont).getReceiver().getAccepterT().interrupt();
                irp.get(cont).getReceiver().getSocket().close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            irp.get(cont).dispose();
            if(!irp.get(cont).isVisible())
            {
                System.out.println("sattoh");
                irp.remove(irp.get(cont));
            }
        }
        //irp.clear();
        //System.out.println(irp.size());
        tryDisableJButton1();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InputRicevi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InputRicevi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InputRicevi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InputRicevi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new InputRicevi().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(InputRicevi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void tryEnableJButton1()
    {
        if(!this.jButton1.isEnabled())
        {
            this.jButton1.setEnabled(true);
        }
    }
    
    public void tryDisableJButton1()
    {
        //metodo chiamato dalle interfacce di download quando vengono chiuse.
        //questo metodo disattiva il bottone "interrompi tutti download" se non vi è nessun donwload in corso(una interfaccia in più è sempre pronta a ricevere file)
        if(irp.size() == 1)
        {
            this.jButton1.setEnabled(false);
        }
    }
    
    public JComboBox getjComboBox1() {
        return jComboBox1;
    }
    
    public ArrayList<InputRiceviProgress> getArrayInputRiceviProgress()
    {
        return this.irp;
    }
    
    public javax.swing.JTextArea getTextArea() {
        return jTextArea1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e){
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        System.out.println("Inizio chiusura Classe InputRicevi");
        //chiusura di tutte le interfacce alla chiusura
        //udp
        risponditoreLan.interrupt();
        risponditoreLan.socketUDP.close();
        //download di tutte le intefaccie
        for(int cont = 0;cont < irp.size();cont++)
        {
            try
            {
                irp.get(cont).getReceiver().getAccepterT().interrupt();
                irp.get(cont).getReceiver().getServerSocket().close();
            }
            catch(IOException ex)
            {
                System.out.println("Thread non chiuso");
            }
            irp.get(cont).dispose();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    //thread che risponde alle richieste di localizzazione nella rete del programma in esecuzione
    //porta 4444
    public class RisponditoreLan extends Thread
    {
        public DatagramSocket socketUDP;
        
        public void run()
        {
            setName("Receiver - DatagramSocket");
            try
            {
                //MacAddress pc
                NetworkInterface localNetwork = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                byte[] byteMacAddress = localNetwork.getHardwareAddress();
                StringBuilder StringMacAddress = new StringBuilder();

                for (int cont = 0; cont < byteMacAddress.length; cont++)
                {
                    StringMacAddress.append(String.format("%02X%s", byteMacAddress[cont], (cont < byteMacAddress.length - 1) ? "-" : ""));
                }

                //nome dell'utente loggato nella macchina
                com.sun.security.auth.module.NTSystem NTSystem = new com.sun.security.auth.module.NTSystem();
                //System.out.println(NTSystem.getName());
                
                //inseriti dai caratteri separatori aggiuntivi per delimitare correttamente la chiave nella conversione non del tutto corretta (es: "StringaInviata" con numeri spazi concatenati dietro)
                StringTokenizer packetData = null;
                String passKey = "Wrachuz=bac?2-ePufrESpabeyA2ad";
                //String passKey = "sattoh";
                
                //stringa contenente la passKey, il nome, l'ip e il mac address del pc
                String stringInformation = passKey + "@" + NTSystem.getName() + "@" + StringMacAddress.toString() + "@";
                byte[] byteInformation = stringInformation.getBytes();

                byte[] buffer = new byte[1024];
                socketUDP = new DatagramSocket(4444);
                DatagramPacket packetSend = null;
                DatagramPacket packetReceived = new DatagramPacket(buffer,buffer.length);
                
                System.out.println("Ascoltatore avviato");
                
                //sistema sempre attivo di risposta
                while(true)
                {
                    System.out.println("Attesa richieste...");
                    socketUDP.receive(packetReceived);
                    //se il pacchetto contiene il la passKey, il thread risponde con i dati di indentificazione
                    
                    packetData = new StringTokenizer( new String(packetReceived.getData()),"@");
                    
                    System.out.println("Key: '" + packetData + "'");
                    
                    if(passKey.equals(packetData.nextToken()))
                    {
                        System.out.println("pacchetto ricevuto da: " + packetReceived.getAddress());
                        packetSend = new DatagramPacket(byteInformation, byteInformation.length, packetReceived.getAddress(), 4443);
                        socketUDP.send(packetSend);
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("Receiver: DatagramSocket Chiuso:");
            }
        }
    };
}
