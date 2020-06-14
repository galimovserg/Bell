package gen;
import java.util.ArrayList;
import java.util.List;


class ReportTable{
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