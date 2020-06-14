package gen;

class ReportDocumentSetting {
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