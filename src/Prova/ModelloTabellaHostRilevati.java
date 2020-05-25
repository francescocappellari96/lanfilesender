/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prova;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author PC
 */
public class ModelloTabellaHostRilevati extends AbstractTableModel
{
    private ArrayList<HostInfo> hostRilevati = new ArrayList<HostInfo>();
    
    public ModelloTabellaHostRilevati(ArrayList<HostInfo> hostRilevati)
    {
        this.hostRilevati = hostRilevati;
    }
    
    @Override
    public String getColumnName(int column)
    {
        switch(column)
        {
            case 0:
            {
                return "Nome Utente:";
            }
            case 1:
            {
                return "Indirizzo IP:";
            }
            case 2:
            {
                return "MAC Address:";
            }
            default:
            {
                return "";
            }
        }
    }
    
    @Override
    public int getRowCount()
    {
        return hostRilevati.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        switch(columnIndex)
        {
            case 0:
            {
                return hostRilevati.get(rowIndex).getHostName();
            }
            case 1:
            {
                return hostRilevati.get(rowIndex).getHostIp();
            }
            case 2:
            {
                return hostRilevati.get(rowIndex).getHostMACAddress();
            }
            default:
            {
                return null;
            }
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int column)
    {  
        return false;  
    }
}
