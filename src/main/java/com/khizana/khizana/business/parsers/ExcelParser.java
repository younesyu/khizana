package com.khizana.khizana.business.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khizana.khizana.dao.models.Tool;
import com.khizana.khizana.dao.repositories.ToolRepository;


@Service
public class ExcelParser {

	@Autowired
	ToolRepository toolRepository;
	
	public void parseFile() {
		try {
			FileInputStream fis = new FileInputStream(new File("src/main/resources/static/data.xlsx"));
			XSSFWorkbook wb = new XSSFWorkbook(fis); 
			List<Tool> tools = new ArrayList<>();

			for (int i = 0; i < 3; i++) {
				XSSFSheet sheet = wb.getSheetAt(i);
				
				for(Row row: sheet) {  
					int firstCellNum = row.getFirstCellNum();
					if (firstCellNum >= 0
							&& row.getCell(firstCellNum) != null
							&& row.getCell(firstCellNum + 1) != null
							&& row.getCell(firstCellNum + 2) != null
							&& row.getCell(firstCellNum).getCellTypeEnum() == CellType.NUMERIC
							&& row.getCell(firstCellNum + 1).getCellTypeEnum() == CellType.STRING
							&& row.getCell(firstCellNum + 2).getCellTypeEnum() == CellType.NUMERIC) {
						Tool tool = new Tool();
						
						tool.setId(((Double) row.getCell(firstCellNum).getNumericCellValue()).longValue());
						tool.setDescription(row.getCell(firstCellNum + 1).getStringCellValue());
						tool.setQuantity(((Double) row.getCell(firstCellNum + 2).getNumericCellValue()).intValue());
						
						String categorie;
						switch (i) {
							case 0:
								categorie = "Outil de coupe";
								break;
							case 1:
								categorie = "Outillage à main";
								break;
							case 2:
								categorie = "Consommable";
								 break;
							default:
								categorie = null;
								break;
						}
						
						tool.setCategory(categorie);
						
						tools.add(tool);
					}    
				}
			}
	
			wb.close();

			toolRepository.saveAll(tools);

		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
