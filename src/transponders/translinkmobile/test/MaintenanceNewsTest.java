package transponders.translinkmobile.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class MaintenanceNewsTest extends TestCase
{	
	StringBuilder allTestURLs;
	StringBuilder allTestTitles;
	
	String allTitlesFromApp;
	String allURLsFromApp;
	
	public MaintenanceNewsTest(String allTitles, String allURLs)
	{
		super();
		
		allTitlesFromApp = allTitles;
		allURLsFromApp = allURLs;
	}
	
	@MediumTest
	public void testAllNews()
	{
		Log.d("TESTMAINTENANCENEWS", "just got in testAllNews()");
		assertNotNull(allTitlesFromApp);
		assertNotNull(allURLsFromApp);
		
		loadMaintenanceNews();	
		
		assertNotNull(allTestURLs);
		assertNotNull(allTestTitles);
		
		String allTestURLsString = allTestURLs.toString();
		String allTestTitlesString = allTestTitles.toString();
		
		assertEquals(allURLsFromApp, allTestURLsString);
		assertEquals(allTitlesFromApp, allTestTitlesString);
		
		Log.d("TESTMAINTENANCENEWS", "finished testAllNews()");
	}
	
	public void loadMaintenanceNews()
	{
		URL url;
		
		allTestURLs = new StringBuilder();
		allTestTitles = new StringBuilder();
		
	    try {
	    	
	    	URI uri = new URI("http://jp.translink.com.au/travel-information/service-updates/rss");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(uri.toString());
         
            doc.getDocumentElement().normalize();
         
            NodeList nList = doc.getElementsByTagName("item");

            for (int temp = 0; temp < nList.getLength(); temp++) {
         
                Node nNode = nList.item(temp);
         
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
         
                    Element eElement = (Element) nNode;
         
                    String link =  eElement.getElementsByTagName("link").item(0).getTextContent();
                    String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                    
                    allTestURLs.append(link);
                    allTestTitles.append(title);
                    
                    Log.d("loadMaintenanceNews", link);
                    Log.d("loadMaintenanceNews", title);
                }

            }
		
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
}

