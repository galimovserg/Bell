package gen;
import java.util.ArrayList;


class ReportPage {
	
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