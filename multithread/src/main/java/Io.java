import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Io {//читает задания из экселя
    public static Map<String, List<Task>> readTasks(String filename) throws IOException{
        Map<String, List<Task>> employeeTasks = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filename);
        Workbook workbook = WorkbookFactory.create(fis)){
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Пропускаем заголовок

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String empId = row.getCell(0).getStringCellValue();
                String taskId = row.getCell(1).getStringCellValue();
                int duration = (int) row.getCell(2).getNumericCellValue();

                Task task = new Task(taskId, duration);
                employeeTasks.computeIfAbsent(empId, k -> new ArrayList<>()).add(task);
            }
        }
        return employeeTasks;
    }

    //записывает дневную статистику
    public static void saveDailyStatistics(String fileName, int day, List<Employee> employees) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fileName)) {
            Sheet sheet = workbook.createSheet("Day_" + day);
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Айди работника");
            header.createCell(1).setCellValue("Продуктивные часы");
            header.createCell(2).setCellValue("Часы простоя");
            header.createCell(3).setCellValue("Эффективность");

            int rowNum = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getEmployeeId());
                row.createCell(1).setCellValue(emp.getHoursWorked());
                row.createCell(2).setCellValue(emp.getHoursIdle());
                row.createCell(3).setCellValue(emp.getEfficiency());
            }
            workbook.write(fos);
        }
    }

    //записывает оставшиеся задания
    public static void saveTasks(String fileName, Map<String, List<Task>> employeeTasks) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fileName)) {
            Sheet sheet = workbook.createSheet("Задания");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Айди работника");
            header.createCell(1).setCellValue("Айди задания");
            header.createCell(2).setCellValue("Оставшееся время");

            int rowNum = 1;
            for (Map.Entry<String, List<Task>> entry : employeeTasks.entrySet()) {
                String empId = entry.getKey();
                for (Task task : entry.getValue()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(empId);
                    row.createCell(1).setCellValue(task.getTaskId());
                    row.createCell(2).setCellValue(task.getRemainingTime());
                }
            }
            workbook.write(fos);
        }
    }


}