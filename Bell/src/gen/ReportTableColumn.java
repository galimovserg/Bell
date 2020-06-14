package gen;

class ReportTableColumn{
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