package tests;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gen.*;

class GeneratorTest {

	@Test
	void testSplitCell1() {
		
		String cellContent="";
		ArrayList<String> expectedLines=new ArrayList<String>();
		
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,5);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}
		
	}
	@Test
	void testSplitCell2() {
		
		String cellContent="Hello";
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("Hello");
		
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,5);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}
		
	}
	@Test
	void testSplitCell3() {
		
		String cellContent="Hello my dear friend!";
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("Hello");
		expectedLines.add("my ");
		expectedLines.add("dear ");
		expectedLines.add("frien");
		expectedLines.add("d!");
		
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,5);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}
		
		
	}
	
	
	@Test
	void testSplitCell4() {
		//Пока неясно что делать при таких ситуациях
		String cellContent="You're - my dear friend!";
		
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("You're");
		expectedLines.add("- my ");
		expectedLines.add("dear ");
		expectedLines.add("friend");
		expectedLines.add("!");
		
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,6);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}
		
	}
	
	@Test
	void testSplitCell5() {
		
		String cellContent="Ким Чен-Ир";
		
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("Ким Чен");
		expectedLines.add("-Ир");
	
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,7);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}	
	}
	@Test
	void testSplitCell6() {
		
		String cellContent="ОченьДлиннаяСтрока";
		
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("Оче");
		expectedLines.add("ньД");
		expectedLines.add("лин");
		expectedLines.add("ная");
		expectedLines.add("Стр");
		expectedLines.add("ока");
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,3);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}	
	}
	@Test
	void testSplitCell7() {
		
		String cellContent="Очень-Длинная-Строка";
		
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("Очень-");
		expectedLines.add("Длинна");
		expectedLines.add("я-");
		expectedLines.add("Строка");
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,6);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}	
	}
	@Test
	void testSplitCell8() {
		
		String cellContent="-..--.-";
		
		ArrayList<String> expectedLines=new ArrayList<String>();
		expectedLines.add("-");
		expectedLines.add(".");
		expectedLines.add(".");
		expectedLines.add("-");
		expectedLines.add("-");
		expectedLines.add(".");
		expectedLines.add("-");
		ArrayList<String> actualLines = ReportTableRow.SplitCell(cellContent,1);
		
		assertEquals(expectedLines.size(), actualLines.size());
		
		int i=0;
		for(String line:actualLines) {
			assertEquals(expectedLines.get(i),line);
			i++;
		}	
	}

}
