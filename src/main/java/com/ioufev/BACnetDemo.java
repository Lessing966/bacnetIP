package com.ioufev;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.Arrays;


public class BACnetDemo {

    LocalDevice device=null;

    public void GetRemotedevice(String ip,Integer port){
        //创建虚拟网络对象
        try {
            IpNetwork ipNetwork =new IpNetworkBuilder()
                    //本地ip地址
                    .withLocalBindAddress(ip)
                    .withSubnet("255.255.255.0",24)
                    .withPort(port)
                    .withReuseAddress(true)
                    .build();

            //创建虚拟设备   名字：666
            device=new LocalDevice(666,new DefaultTransport(ipNetwork));
            device.initialize();
            device.startRemoteDeviceDiscovery();

            //链接设备
            RemoteDevice remoteDevice = device.getRemoteDeviceBlocking(3965067); //设备device id

            System.out.println(remoteDevice.getDeviceProperty(PropertyIdentifier.modelName)+"：设备名称");

            ObjectIdentifier oid =new ObjectIdentifier(ObjectType.analogInput,0); //获取设备的 analogInput:0
            ObjectIdentifier oid1 = new ObjectIdentifier(ObjectType.analogInput, 1);//获取设备的 analogInput:1
            ObjectIdentifier oid2 = new ObjectIdentifier(ObjectType.analogInput, 2);//获取设备的 analogInput:2
            System.out.println(oid.getObjectType());


            //获取指定的presentValue
            PropertyValues propertyValues = RequestUtils.readOidPresentValues(device,remoteDevice, Arrays.asList(oid,oid1,oid2),null);
            Encodable encodable = propertyValues.get(oid, PropertyIdentifier.presentValue);
            String s = encodable.toString();
            System.out.println("analogInput:0："+propertyValues.get(oid,PropertyIdentifier.presentValue));
            System.out.println("analogInput:1："+propertyValues.get(oid1,PropertyIdentifier.presentValue));
            System.out.println("analogInput:2："+propertyValues.get(oid2,PropertyIdentifier.presentValue));

            device.terminate();
        }catch (Exception e){
            e.printStackTrace();
            device.terminate();
        }
    }


    public static void main(String[] args) {
        BACnetDemo baCnetDemo =new BACnetDemo();
        baCnetDemo.GetRemotedevice("192.168.2.26", 47808);
    }


}
