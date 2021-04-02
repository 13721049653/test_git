package org.ezplatform.core.doginit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;

import org.ezplatform.workflow.listener.KingdeeDataListener;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import p1.DOGGSMH;
import sentinel.SFNTReader;
import softencrypt.BASE64;
import softencrypt.DESPlus;
import softencrypt.HardWareUtils;
import softencrypt.ReadMac;

public class DogCtr
{
	private static final Logger log = LoggerFactory.getLogger(DogCtr.class);
  public static String getFingerprintNative()
  {
    String key = "";
    String cpuid = HardWareUtils.getCPUID();
    String mac = ReadMac.getMACAddress();

    key = cpuid + mac;

    return key;
  }

  public static String getFingerprintBase64()
  {
    String key = "";
    BASE64 bs = new BASE64();
    key = bs.BASE64Encoder(getFingerprintNative());

    return key;
  }

  public void genFingerprintBase64()
  {
	  System.out.println("3333333333333333333333333333333333333333");
	  log.info("3333333333333333333333333333333333333223333333333333333333333322333");
		 log.debug("3333333333333333333333333333333333333333444444444444444444444444444444444333333333");
		 log.error("333333333333333333333333333333333335555555555555555555555555555555555555533333");
  }

  public String getDogInfoBASE64(String dogType)
  {
    String key = "";
    key = getDogInfo(dogType);
    BASE64 base = new BASE64();
    key = base.BASE64Encoder(key);
    return key;
  }

  public String getDogInfo(String dogType)
  {
    String key = "";
    if ("micro".equals(dogType)) {
      key = readMicroDogInfo();
    }
    else if ("sentinel".equals(dogType)) {
      key = readSentinelDogInfo();
    }
    else if ("soft".equals(dogType)) {
      key = readSoftLicenseInfo();
    }
    return key;
  }

  public String[] getDogInfoArr(String dogType)
    throws Exception
  {
    String[] ret = new String[9];
    String key = getDogInfo(dogType);

    if (key.indexOf("<date>") != -1) {
      ret[0] = key.substring(key.indexOf("<date>") + 6, key
        .indexOf("</date>"));
    }

    if (key.indexOf("<user>") != -1) {
      ret[1] = key.substring(key.indexOf("<user>") + 6, key
        .indexOf("</user>"));
    }

    if (key.indexOf("<mobileuser>") != -1) {
      ret[3] = key.substring(key.indexOf("<mobileuser>") + 12, key
        .indexOf("</mobileuser>"));
    }

    if (("soft".equals(dogType)) && 
      (key.indexOf("<mac>") != -1)) {
      String key64 = key.substring(key.indexOf("<mac>") + 5, key
        .indexOf("</mac>"));

      BASE64 base = new BASE64();
      key64 = base.BASE64Decoder(key64);

      ret[2] = key64;
    }

    if (key.indexOf("<imuser>") != -1) {
      ret[4] = key.substring(key.indexOf("<imuser>") + 8, key
        .indexOf("</imuser>"));
    }

    if (key.indexOf("<oamodule>") != -1) {
      ret[5] = key.substring(key.indexOf("<oamodule>") + 10, key
        .indexOf("</oamodule>"));
    }

    if (key.indexOf("<portaluser>") != -1) {
      ret[6] = key.substring(key.indexOf("<portaluser>") + 12, key
        .indexOf("</portaluser>"));
    }

    if (key.indexOf("<dinguser>") != -1) {
      ret[7] = key.substring(key.indexOf("<dinguser>") + 10, key
        .indexOf("</dinguser>"));
    }

    if (key.indexOf("<yunuser>") != -1) {
      ret[8] = key.substring(key.indexOf("<yunuser>") + 9, key
        .indexOf("</yunuser>"));
    }
    return ret;
  }

  public String[] getDogInfoArrFromMapBASE64(Map<String, String> map)
    throws Exception
  {
    String[] ret = new String[9];
    String key64 = "";
    if (map.get("dog") != null)
      key64 = ((String)map.get("dog")).toString();
    else {
      return ret;
    }

    BASE64 base = new BASE64();
    key64 = base.BASE64Decoder(key64);

    if (key64.indexOf("<date>") != -1) {
      ret[0] = key64.substring(key64.indexOf("<date>") + 6, 
        key64.indexOf("</date>"));
    }
    if (key64.indexOf("<user>") != -1) {
      ret[1] = key64.substring(key64.indexOf("<user>") + 6, 
        key64.indexOf("</user>"));
    }
    if (key64.indexOf("<mac>") != -1) {
      String macencode = key64.substring(key64.indexOf("<mac>") + 5, 
        key64.indexOf("</mac>"));

      ret[2] = base.BASE64Decoder(macencode);
    }
    if (key64.indexOf("<mobileuser>") != -1) {
      ret[3] = key64.substring(
        key64.indexOf("<mobileuser>") + 12, key64
        .indexOf("</mobileuser>"));
    }

    if (key64.indexOf("<imuser>") != -1) {
      ret[4] = key64.substring(
        key64.indexOf("<imuser>") + 8, key64
        .indexOf("</imuser>"));
    }

    if (key64.indexOf("<oamodule>") != -1) {
      ret[5] = key64.substring(
        key64.indexOf("<oamodule>") + 10, key64
        .indexOf("</oamodule>"));
    }

    if (key64.indexOf("<portaluser>") != -1) {
      ret[6] = key64.substring(
        key64.indexOf("<portaluser>") + 12, key64
        .indexOf("</portaluser>"));
    }

    if (key64.indexOf("<dinguser>") != -1) {
      ret[7] = key64.substring(
        key64.indexOf("<dinguser>") + 10, key64
        .indexOf("</dinguser>"));
    }

    if (key64.indexOf("<yunuser>") != -1) {
      ret[8] = key64.substring(
        key64.indexOf("<yunuser>") + 9, key64
        .indexOf("</yunuser>"));
    }

    return ret;
  }

  public String readSentinelDogInfo()
  {
    String dogInfo = "";
    SFNTReader sf = new SFNTReader();
    sf.readDogInfo();

    dogInfo = "<date>" + sf.getDate() + "</date>";

    dogInfo = dogInfo + "<user>" + sf.getUser() + "</user>";
    return dogInfo;
  }

  public String readSoftLicenseInfo()
  {
    String dogInfo = "";

    return dogInfo;
  }

  public String readMicroDogInfo()
  {
    String dogInfo = "";

    DOGGSMH doggsmh = new DOGGSMH();
    doggsmh.DogCascade = 0;

    doggsmh.DogPassword = 76072219;

    doggsmh.DogData = new byte['È'];
    for (int ichar = 0; ichar < 200; ichar++) {
      doggsmh.DogData[ichar] = ((byte)(ichar + 48));
    }
    try
    {
      if (doggsmh.CallDogCheck() == 0)
      {
        String lit2 = "0";

        lit2 = doggsmh.CallReadDog(10, 20);

        long odate = Long.valueOf(lit2).longValue();

        dogInfo = "<date>" + odate + "</date>";

        int lit = 0;

        lit = Integer.parseInt(doggsmh.CallReadDog(30, 10));

        dogInfo = dogInfo + "<user>" + lit + "</user>";

        int lit3 = 0;

        lit3 = Integer.parseInt(doggsmh.CallReadDog(0, 10));

        dogInfo = dogInfo + "<mobileuser>" + lit3 + "</mobileuser>";

        int lit4 = 0;

        lit4 = Integer.parseInt(doggsmh.CallReadDog(40, 10));

        dogInfo = dogInfo + "<imuser>" + lit4 + "</imuser>";

        String lit5 = "";
        lit5 = doggsmh.CallReadDog(50, 50);
        dogInfo = dogInfo + "<oamodule>" + lit5 + "</oamodule>";

        String lit6 = "";
        lit6 = doggsmh.CallReadDog(100, 10);
        dogInfo = dogInfo + "<portaluser>" + lit6 + "</portaluser>";

        String lit7 = "";
        lit7 = doggsmh.CallReadDog(110, 10);
        dogInfo = dogInfo + "<dinguser>" + lit7 + "</dinguser>";

        String lit8 = "";
        lit8 = doggsmh.CallReadDog(120, 10);
        dogInfo = dogInfo + "<yunuser>" + lit8 + "</yunuser>";
      }
    }
    catch (NumberFormatException e)
    {
      dogInfo = dogInfo + "<imuser>0</imuser>";
    } catch (Exception e) {
      System.out.println(
        "---------------------------------------------");
      e.printStackTrace();
      System.out.println(
        "---------------------------------------------");
    }
    return dogInfo;
  }

  public static void main(String[] args)
  {
	  
    BASE64 bs = new BASE64();
    String key = bs.BASE64Decoder("QkZFQkZCRkYwMDAyMDZENzZDLUFFLThCLTM3LUE4LTUz");
    System.out.println(key);
    String key2 = bs.BASE64Decoder("MEZBQkZCRkYwMDA0MDZGMTAwLTUwLTU2LTk4LUQ5LTEx");
    System.out.println(key2);
    String key3 = bs.BASE64Decoder("zt5DUFVfSUSxu7bByKEwMC01MC01Ni05OC1EOS0xMQ==");
    System.out.println(key3);
  }
}