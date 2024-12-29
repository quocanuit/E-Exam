package com.example.e_exam;

import android.content.Context;
import android.net.Uri;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelHandler {
    public static Map<Integer, String> readAnswerSheet(Context context, Uri fileUri) throws Exception {
        Map<Integer, String> answers = new HashMap<>();

        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                Cell questionCell = row.getCell(0);
                Cell answerCell = row.getCell(1);

                if (questionCell != null && answerCell != null) {
                    int questionNumber = (int) questionCell.getNumericCellValue();
                    String answer = answerCell.getStringCellValue().trim().toUpperCase();
                    answers.put(questionNumber - 1, answer);
                }
            }
        }
        return answers;
    }
}
