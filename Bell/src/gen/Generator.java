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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class Generator{
	
	

	static class DocumentWriter{
		ReportPageSetting pageSet;
		ReportDocumentSetting docSet;
		Writer w;
		private int pagePos;
		DocumentWriter(Writer w,ReportPageSetting pageSet, ReportDocumentSetting docSet) {
			this.w=w;
			this.pageSet=pageSet;
			this.docSet=docSet;
			pagePos=0;
		}
		
		void addLines(ArrayList<String> docLines) throws IOException {
			for(String line:docLines) {
				if(pageSet.getHeight()-pagePos>=1) {
					w.write(line+System.lineSeparator());
					pagePos++;
				}else {
					pagePos=0;
					w.write(docSet.getPageLineSpliter());
					
				}
			}
		}

		public int getFreeLinesCount() {
			// TODO Auto-generated method stub
			return pageSet.getHeight()-pagePos;
		}

		public void nextPage() throws IOException {
			// TODO Auto-generated method stub
			w.write(docSet.getPageLineSpliter());
			pagePos=0;
		}
		
	}
	
	
	public static class TSVFile extends File{
		public TSVFile(String pathname) {
			super(pathname);
		}

		public void makeTextReport(ReportTableColumn[] columns,ReportPageSetting pageSet, ReportDocumentSetting docSet, Writer w){
			try (Scanner scanner = new Scanner(this)) {
				ReportTable t=new ReportTable(columns);
				Object[] title=Arrays.stream(t.getReportColumns()).map(s->s.getTitle()).toArray();
				
				DocumentWriter dw=new DocumentWriter(w, pageSet, docSet);
				dw.addLines(new ReportTableRow(t,title).toDocumentLines());
				
				while (scanner.hasNext()){
					Scanner cellsc=new Scanner(scanner.nextLine());
					cellsc.useDelimiter("\t");
					ArrayList<Object> cells=new ArrayList<Object>();
					while(cellsc.hasNext()) {
						//w.write("| "++" | ");
						//System.out.print("| "+cellsc.next()+" | ");
						cells.add(cellsc.next());
					}
					//w.write(System.lineSeparator());	
					ReportTableRow row=new ReportTableRow(t, cells);
					ArrayList<String> rowLines = row.toDocumentLines();
					
					if(dw.getFreeLinesCount()>=rowLines.size()) {
						dw.addLines(rowLines);
					}else {
						//если строка не помещается на данной странице
						//то добавляем новую страницу
						dw.nextPage();
						//и выводим заголовки столбцов
						dw.addLines(new ReportTableRow(t,title).toDocumentLines());
						dw.addLines(rowLines);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
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
			
			
			//записываем отчет в файл
			File reportFile=new File(reportPath);
			try(FileWriter writer = new FileWriter(reportFile, true)){
				new TSVFile(sourcePath).makeTextReport(reportcolumns,pageSet, new ReportDocumentSetting("~"+System.lineSeparator()), writer);
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
