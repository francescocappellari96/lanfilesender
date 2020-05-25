/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prova;

/**
 *
 * @author PC
 */
public class HostInfo
{
    private String hostName = null;
    private String hostIp = null;
    private String hostMACAddress = null;
    
    public HostInfo(String hostName ,String hostIp ,String hostMACAddress)
    {
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.hostMACAddress = hostMACAddress;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getHostIp()
    {
        return hostIp;
    }

    public String getHostMACAddress()
    {
        return hostMACAddress;
    }
}
