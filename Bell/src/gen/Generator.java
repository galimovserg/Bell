package gen;
/**
 * 
 * Created by Сергей Галимов
 * Copyright © 2020 Сергей Галимов. All rights reserved.
 * 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;



public class Generator{
	
	interface IGenerator{
		interface ITextReports{
			
			interface ITextReportTableColumn{}
			
			interface ITextReportTableRow{}
			
			interface ITextReportTable{
				void setData(String[][] data);
				String toString();
			}
			interface ITextReportPageSetting{}
			
			interface ITextReportDocumentSetting{}
			
			interface ITextReportDocument{
				void addTable(ITextReportTable t);
			}
			interface ITextReportPage{
				void addDocumentLines(ArrayList<String> lines);
				int getFreeLinesCount();
			}
		}
	}
	
	
	@SuppressWarnings("resource")
	static void pressToExit() {
		System.out.println("Press enter to exit...");
		new Scanner(System.in).nextLine();
		System.exit(0);
	}
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		//new MainWindow();
		String settingPath = null;
		String sourcePath = null;
		String reportPath = null;
		try {
			settingPath=args[0];
			sourcePath=args[1];
			reportPath=args[2];
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			pressToExit();
		}
		System.out.println("Загрузка...");
		System.out.println("Setting path is \'"+settingPath+"\'");
		System.out.println("Source path is \'"+sourcePath+"\'");
		System.out.println("Report path is \'"+reportPath+"\'");
		
		System.out.println("Загрузка файла конфигурации...");
		//System.out.println("Содержимое файл конфигурации:");
		//System.out.println(settingFileContent);
		
		int width=0;
		int height=0;
		
		try {
			//парсим xml
			DocumentBuilderFactory docFactory =DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(settingPath));
			doc.getDocumentElement().normalize();
		
			//настройка страницы
			NodeList pageNodes = doc.getElementsByTagName("page");
			Node pageSetNode=pageNodes.item(0);
			Element pageSetElement = (Element) pageSetNode;
			//ширина
			width = Integer.valueOf(pageSetElement.getElementsByTagName("width").item(0).getTextContent());
			//System.out.println("Width: "+width);
			//высота
			height = Integer.valueOf(pageSetElement.getElementsByTagName("height").item(0).getTextContent());
			//System.out.println("Height: "+height);
			
			ReportPageSetting pageSet=new ReportPageSetting(width, height);
			
			//название колонок и их размеры
			NodeList columnsNodes = doc.getElementsByTagName("columns");
			Node columnsNode=columnsNodes.item(0);
			Element columnsElement = (Element) columnsNode;
			NodeList columnsList=columnsElement.getElementsByTagName("column");
			
			ReportTableColumn[] reportcolumns=new ReportTableColumn[columnsList.getLength()];
			for(int i=0;i<columnsList.getLength();i++) {
				Node column= columnsList.item(i);
				Element columnElement = (Element)column;
				
				String cname=columnElement.getElementsByTagName("title").item(0).getTextContent();
				int cwidth=Integer.valueOf(columnElement.getElementsByTagName("width").item(0).getTextContent());
				reportcolumns[i]=new ReportTableColumn(cname,cwidth);
			}
			//данные из tsv
			TsvParserSettings settings = new TsvParserSettings();
			settings.getFormat().setLineSeparator("\n");
			TsvParser parser = new TsvParser(settings);
			List<String[]> ml =  parser.parseAll(new File(sourcePath));
			
		    //генерация отчета
			ReportDocument report = new ReportDocument(pageSet, new ReportDocumentSetting("~"+System.lineSeparator()));
			ReportTable t=new ReportTable(reportcolumns);
			
			t.setData(ml);
			report.addTable(t);
			System.out.println(report.toString());
			
			//основная функция, генерирует отчет
			String reportContent=report.toString();
			//записываем отчет в файл
			File reportFile=new File(reportPath);
			try(FileWriter writer = new FileWriter(reportFile, true)){
				writer.write(reportContent);
				writer.flush();
			}catch(IOException ex){
				System.out.println(ex.getMessage());
				System.out.println("Проблема с записью файла");
				pressToExit();
			} 
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("Файл настроек поврежден, исправьте и попробуйте снова.");
			pressToExit();
		}
				
	}

}
