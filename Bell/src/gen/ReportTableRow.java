package gen;
import java.util.ArrayList;



public class ReportTableRow {
	private Object[] rowContent;
	private ReportTable t;
	private String reportTableRowSeparator;
	ReportTableRow(ReportTable t, Object[] row){
		this.t=t;
		this.rowContent=row;
		this.reportTableRowSeparator=genReportTableRowSeparator();
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
	public static ArrayList<String> SplitCell(String s, int size){
		
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
			//Если следующий символ - знак, то не переносим
			if(!isletter(s.charAt(pos+size))) {
				res.add(s.substring(pos, pos+size));
				pos+=size;
				continue;
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
	 * Преобразует строку таблицы в строчки документа
	 * @return
	 */
	ArrayList<String> toDocumentLines(){
		ArrayList<String> EmptyLines=generateEmptyLines(t.getReportColumns(),' ');
		//результат
		ArrayList<String> rowlines = new ArrayList<String>();
		for(int j=0;j<rowContent.length&&j<t.getReportColumns().length;j++) {
			ArrayList<String> res= SplitCell((String)rowContent[j], t.getReportColumns()[j].getWidth());
			String[] cellContent = addSpace(res,t.getReportColumns()[j].getWidth(),' ');
			
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
				for(int l=0;l<t.getReportColumns()[j].getWidth()+2;l++) {
					s+=' ';
				}
				if(j==rowContent.length-1) {
					s+='|';
				}
				rowlines.set(k,rowlines.get(k)+s);
			}
		}
		rowlines.add(reportTableRowSeparator);
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
	private String genReportTableRowSeparator() {
		// TODO Auto-generated method stub
		String res="";
		for(int i=0;i<t.getWidth();i++) res+='-';
		return res;
	}
}