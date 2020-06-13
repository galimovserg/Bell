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
import java.util.Arrays;
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



public class Generator {
	
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
	
	
	static class ReportTableColumn{
		private int width;
		private String title;
		ReportTableColumn(String title, int width){
			this.width=width;
			this.title=title;
		}
		void setWidth(int width){
			this.width=width;
		}
		int getWidth() {
			return width;
		}
		String getTitle() {
			return title;
		}
	}
	static class ReportTableRow {
		private Object[] rowContent;
		private ReportTable t;
		private String rowSpliter;
		ReportTableRow(ReportTable t, Object[] row){
			this.t=t;
			this.rowContent=row;
			this.rowSpliter=genReportTableRowSpliter();
		}
		
		
		
		static boolean isletter(char c) {
			switch(c) {
			case '+':case '-':case '_':case ' ':case '*':case '/': {return false;}
			default: return true;
			}
		}
		/**
		 * Делит строку s таким образом, чтобы каждая подстрока вмещалась 
		 * в ячейку по ширине колонки size
		 * при этом слова переносятся
		 * @param s
		 * @param size
		 * @return
		 */
		static ArrayList<String> SplitCell(String s, int size){
			
			ArrayList<String> res=new ArrayList<String>();
			if(size<=0)
				return res;
			int pos=0;
			boolean prvIsNotSpace=true;
			while(pos<s.length()) {
				
				if(s.charAt(pos)==' '&&prvIsNotSpace) {
					pos++;
					prvIsNotSpace=false;
					continue;
				}else
				if(s.charAt(pos)!=' ') {
					prvIsNotSpace=true;
				}
				
				if(pos+size>=s.length()) {
					//add space
					res.add(s.substring(pos, s.length()));
					break;
				}
				
				for(int i=size-1;i>=0;i--) {
					//System.out.print(i+" ");
					if(i==0) {
						res.add(s.substring(pos,pos+size));
						pos=pos+size;
						break;
					}
					if(!isletter(s.charAt(pos+i))) {
						
						res.add(s.substring(pos, pos+i+1));
						pos=pos+i+1;
						break;
					}
					
				}
				
				
			}
			
			return res;
		}
		
		/**
		 * Функция добавляет дополнительные пробелы до заданной ширины ячейки
		 * @param Content
		 * @param width
		 * @param height
		 * @param space
		 * @return
		 */
		static String[] addSpace(ArrayList<String> Content, int width, char space){
			String res[] = new String[Content.size()];
			for(int i=0; i<Content.size();i++) {
				res[i]=" ";
				for(int j=0;j<width+1;j++) {
					if(j<Content.get(i).length())
						res[i]+=Content.get(i).charAt(j);
					else
						res[i]+=space;
				}
			}
			return res;
		}
		/**
		 * Преобразует строку в строки документа
		 * @return
		 */
		ArrayList<String> toDocumentLines(){
			ArrayList<String> EmptyLines=generateEmptyLines(t.getReportColumns(),' ');
			//результат
			ArrayList<String> rowlines = new ArrayList<String>();
			for(int j=0;j<rowContent.length&&j<t.getReportColumns().length;j++) {
				ArrayList<String> res= SplitCell((String)rowContent[j], t.getReportColumns()[j].width);
				String[] cellContent = addSpace(res,t.getReportColumns()[j].width,' ');
				
				for(int k=0;k<cellContent.length;k++) {
					if(k<rowlines.size()) {
						if(rowlines.get(k)!=null)
							rowlines.set(k,rowlines.get(k)+"|"+cellContent[k]);
						else {
							rowlines.set(k,"|"+cellContent[k]);
						}
						if(j==rowContent.length-1) {
							rowlines.set(k,rowlines.get(k)+"|");
						}
					}else {
						if(j>0)
							rowlines.add(EmptyLines.get(j-1)+cellContent[k]);
						else
							rowlines.add("|"+cellContent[k]);
						if(j==rowContent.length-1) {
							rowlines.set(k,rowlines.get(k)+"|");
						}
					}
				}
				for(int k=cellContent.length;k<rowlines.size();k++) {
					String s="|";
					for(int l=0;l<t.getReportColumns()[j].width+2;l++) {
						s+=' ';
					}
					if(j==rowContent.length-1) {
						s+='|';
					}
					rowlines.set(k,rowlines.get(k)+s);
				}
			}
			rowlines.add(rowSpliter);
			return rowlines;
		}
		private ArrayList<String> generateEmptyLines(ReportTableColumn[] reportColumns, char space) {
			// TODO Auto-generated method stub
			ArrayList<String> EmptyLines=new ArrayList<String>();
			String s="|";
			for(int i=0;i<reportColumns.length;i++) {
				for(int j=0;j<reportColumns[i].getWidth()+2;j++) {
					s+=space;
				}
				s+="|";
				EmptyLines.add(s);
				
			}
			return EmptyLines;
		}
		private String genReportTableRowSpliter() {
			// TODO Auto-generated method stub
			String res="";
			for(int i=0;i<t.getWidth();i++) res+='-';
			return res;
		}
	}
	static class ReportTable{
		private ReportTableColumn[] columns;
		private ArrayList<ReportTableRow> rowList;
		private int width;
		ReportTable(ReportTableColumn[] columns){
			this.columns=columns;
			this.width=computeWidth();
			this.rowList=new ArrayList<ReportTableRow>();
		}
		public int getWidth() {
			return this.width;
		}
		private int computeWidth() {
			int reswidth=0;
			for(int i=0;i<columns.length;i++) {
				reswidth+=columns[i].getWidth()+3;
			}
			return reswidth+1;
		}
		public void setData(Object[][] data) {
			for(int i = 0;i<data.length;i++) {
				rowList.add(new ReportTableRow(this, data[i]));
			}
		}
		public void setData(List<String[]> data) {
			for(String[] row:data) {
				rowList.add(new ReportTableRow(this, row));
				//System.out.println(row.length);
			}
		}
		public ReportTableColumn[] getReportColumns() {
			return columns;
		}
		public ArrayList<ReportTableRow> getReportTableRows(){
			return rowList;
		}
		
	}
	static class ReportDocumentSetting {
		private String pageLineSpliter;
		ReportDocumentSetting(String pageLineSpliter){
			this.pageLineSpliter=pageLineSpliter;
		}
		public String getPageLineSpliter() {
			return pageLineSpliter;
		}
		public void setPageLineSpliter(String pageLineSpliter) {
			this.pageLineSpliter = pageLineSpliter;
		}
	}
	static class ReportPageSetting {
		private int width;
		private int height;
		ReportPageSetting(int width, int height){
			this.width=width;
			this.height=height;	
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		
	}
	
	static class ReportDocument{
		private ArrayList<ReportPage> pages;
		private ReportPageSetting pageSet;
		private ReportDocumentSetting docSet;
		
		ReportDocument(ReportPageSetting pageSet, ReportDocumentSetting docSet){
			this.pageSet=pageSet;
			this.docSet=docSet;
			pages=new ArrayList<ReportPage>();
			pages.add(new ReportPage(pageSet));
		}
		void addLines(ArrayList<String> docLines) {
			if(pages.get(pages.size()-1).getFreeLinesCount()>=docLines.size()) {
				pages.get(pages.size()-1).addDocumentLines(docLines);
				return;
			}
			for(String line:docLines) {
				if(pages.get(pages.size()-1).getFreeLinesCount()>=1) {
					pages.get(pages.size()-1).addDocumentLine(line);
				}else {
					pages.add(new ReportPage(pageSet));
					pages.get(pages.size()-1).addDocumentLine(line);
				}
			}
		}
		void addTable(ReportTable rt){
			ArrayList<ReportTableRow> tablerows = rt.getReportTableRows();
			//добавляет титульник таблицы (только имена)
			Object[] title=Arrays.stream(rt.getReportColumns()).map(s->s.getTitle()).toArray();
			pages.get(pages.size()-1).addDocumentLines(new ReportTableRow(rt,title).toDocumentLines());
			
			for(ReportTableRow row:tablerows) {
				ArrayList<String> docLines=row.toDocumentLines();
				if(pages.get(pages.size()-1).getFreeLinesCount()>docLines.size()) {
					addLines(docLines);
				}else {
					//если строка не помещается на данной странице
					//то добавляем новую страницу
					pages.add(new ReportPage(pageSet));
					//и выводим заголовки столбцов
					addLines(new ReportTableRow(rt,title).toDocumentLines());
					addLines(docLines);
				}
			}
		}
		
		void startDocumentColumns(int count) {
			
		}
		public String toString(){
			String res="";
			int pageNum=0;
			for(ReportPage page:pages) {
				if(pageNum>0) {
					res+=docSet.getPageLineSpliter();
				}
				pageNum++;
				res+=page.toString();
				
			}
			return res;
		}
	}
	static class ReportPage {
		
		private ReportPageSetting pageSet;
		private ArrayList<String> lines;
		
		void addDocumentLines(ArrayList<String> docLines){
			lines.addAll(docLines);
		}
		public void addDocumentLine(String docLine) {
			// TODO Auto-generated method stub
			lines.add(docLine);
		}
		ReportPage(ReportPageSetting pageSet){
			lines=new ArrayList<String>();
			this.pageSet=pageSet;
		}
		int getFreeLinesCount() {
			
			return pageSet.getHeight()-lines.size();
		}
		public String toString() {
			String res="";
			int linecount=0;
			for(String line:lines) {
				linecount++;
				if (linecount<=pageSet.getHeight()) {
					if(pageSet.getWidth()<line.length())
						res+=line.substring(0, pageSet.getWidth())+System.lineSeparator();
					else
						res+=line+System.lineSeparator();
				}else {
					break;
				}
			}
			return res;
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
