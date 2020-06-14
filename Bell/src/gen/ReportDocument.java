package gen;
import java.util.ArrayList;
import java.util.Arrays;



class ReportDocument{
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