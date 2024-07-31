package QueryManagment;

import constant.Constant;
import constant.Utils;
import fileOperations.FileIO;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessQuery {

    /**
     *handle normal queries and divide flow to different methods such as CREATE TABLE | INSERT | SELECT
     * @param query
     */
    public static void handleQueries(String query) {
        if(query.startsWith("create table")) {
            createTable(query);
        }else if(query.startsWith("insert")) {
            insertRecords(query);
        } else if(query.startsWith("select")) {
            selectRecords(query);
        }
    }

    /**
     * create table based on user given table name and columns of that
     * @param query
     */
    public static void createTable(String query) {
        try {
            // create table pattern
            Pattern pattern = Pattern.compile("^create table (\\w+) \\((.+)\\);$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(query);
            if(matcher.find()){
                Utils.addLogs("Create Table");
                String tableName = matcher.group(1);
                String columns = matcher.group(2);

                File file = new File(Query.currentDatabase+ "/" + tableName+".txt");
                // do not create table if same table name already exists
                if(file.exists()) {
                    System.out.println("Table already exists");
                } else {
                    // create new table = create new file
                    file.createNewFile();
                    String[] columnNames = columns.split(",");
                    StringBuilder formattedColumns = new StringBuilder();
                    for (String columnName : columnNames) {
                        formattedColumns.append(columnName.trim()).append("##");
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(formattedColumns.toString());
                    writer.newLine();
                    // separate with header separator
                    writer.write(Constant.HEADER_SEPARATOR);
                    writer.newLine();
                    writer.close();
                    System.out.println(tableName + " table created successfully!!");
                }

            } else {
                System.out.println("Invalid create table query");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * insert records inside file based on what user has specified table name and columns as well as values
     * @param query
     */
    public static void insertRecords(String query) {
        try {

            // insert to DB pattern
            Pattern pattern = Pattern.compile("^insert into (\\w+) \\((.+)\\) values \\((.+)\\)$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(query);

            if (matcher.find()) {
                String tableName = matcher.group(1);
                String columnsPart = matcher.group(2);
                String valuesPart = matcher.group(3);

                // separating column and values from query
                String[] columns = columnsPart.split(",");
                String[] values = valuesPart.split(",");

                // if length is not the same of column and values - invalid query
                if (columns.length != values.length) {
                    System.out.println("column and values mismatch.");
                    return;
                }

                // putting column and corresponding values in key value pair
                LinkedHashMap<String, String> columnValuesMap = new LinkedHashMap<>();
                for (int i = 0; i < columns.length; i++){
                    columnValuesMap.put(columns[i].trim(), values[i].trim());
                }

                //fetching column names that are exist in table
                BufferedReader reader = new BufferedReader(new FileReader(Query.currentDatabase + "/" +  tableName + ".txt"));
                String[] columnsFromTable = reader.readLine().split(Constant.DELIMITER);
                reader.close();

                // values to inserted in table
                String valuesToBeinserted[] = new String[columnsFromTable.length];
                for (int i = 0; i < columnsFromTable.length; i++) {
                    String currentValue = columnValuesMap.get(columnsFromTable[i]);
                    valuesToBeinserted[i] = currentValue;
                }

                // appending delimiter to values
                StringBuilder valuesToWrittenToTable = new StringBuilder();
                for (String s : valuesToBeinserted) {
                    valuesToWrittenToTable.append(s).append(Constant.DELIMITER);
                }
                // Insert to file
                FileIO.writeToFile(Query.currentDatabase + "/" + tableName + ".txt" , valuesToWrittenToTable.toString());
                Utils.addLogs("Does Insertion");
                System.out.println("1 ROW Affected!");
            } else {
                System.out.println("Your query seems Invalid please try again!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * select records from table
     * @param query
     */
    public static void selectRecords(String query) {
        try {
            // Select regex

//            String regex = "^SELECT\\s+((?:\\*|\\w+\\s*,?\\s*)+)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?;$";
            String regex = "^SELECT\\s+((?:\\*|\\w+\\s*,?\\s*)+)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(\\w+)\\s*=\\s*(\\w+))?;$";

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(query);

            if (matcher.matches()) {
                Utils.addLogs("Does Selection");
                String requestedColumns = matcher.group(1);
                String tableName = matcher.group(2);
                String whereClause = matcher.group(3);
                BufferedReader reader = new BufferedReader(new FileReader(Query.currentDatabase + "/" +  tableName + ".txt"));

                // if user is trying select *
                if(requestedColumns.trim().equals("*")) {
                    selectAllFromtable(reader, whereClause, matcher);
                }
                // if user select particular columns from table
                else {
                    selectColumnsFromTable(requestedColumns,reader, whereClause, matcher);

                }
            } else {
                System.out.println("Input does not match the pattern.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }


    /**
     *called if user wants all data from table
     * @param reader - read from table
     * @param whereClause - if user has specified where conditions
     * @param matcher - to get wherekey and wherevalue from userinput
     * @throws IOException
     */
    public static void selectAllFromtable(BufferedReader reader, String whereClause,Matcher matcher) throws IOException{
        StringBuilder sb = new StringBuilder();
        String[] cols = reader.readLine().split(Constant.DELIMITER);
        for (String s : cols) {
            sb.append(s).append("\t");
        }

        System.out.println();
        System.out.println(sb);
        System.out.println();
        if(whereClause != null) {
            String whereKey = matcher.group(3);
            String whereValue = matcher.group(4);
            int index = -1;
            for (int i = 0; i < cols.length; i++) {
                if (cols[i].trim().equals(whereKey.trim())) {
                    index = i;
                    break;
                }
            }

            String line = reader.readLine();
            while (line != null) {
                String[] vals = line.split(Constant.DELIMITER);
                if (index < 0 || (vals[index] != null && vals[index].equals(whereValue)))
                    System.out.println(line.replaceAll(Constant.DELIMITER, "\t"));
                line = reader.readLine();
            }
        } else {
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line.replaceAll(Constant.DELIMITER, "\t\t"));
                line = reader.readLine();
            }
        }
        reader.close();

    }

    /**
     * if user has selected particular columns from table
     * @param requestedColumns
     * @param reader
     * @param whereClause
     * @param matcher
     * @throws IOException
     */
    public static void selectColumnsFromTable(String requestedColumns,BufferedReader reader, String whereClause,Matcher matcher) throws IOException {
        List<Integer> columnToBeIncluded = new ArrayList<>();
        String[] columnSplit = requestedColumns.split(",\\s*");
        List<String> requestedCols = Arrays.asList(columnSplit);
        String[] tableHeader = reader.readLine().split(Constant.DELIMITER);
        StringBuilder printTableHeader = new StringBuilder();
        for (int index = 0; index < tableHeader.length; index++) {
            if (requestedCols.contains(tableHeader[index])) {
                columnToBeIncluded.add(index);
                printTableHeader.append(tableHeader[index]).append("\t");
            }
        }
        System.out.println(printTableHeader);

        if(whereClause != null) {
            System.out.println(Constant.HEADER_SEPARATOR);
            String whereKey = matcher.group(3);
            String whereValue = matcher.group(4);

            int index = -1;
            for (int i = 0; i < tableHeader.length; i++) {
                if (tableHeader[i].trim().equals(whereKey.trim())) {
                    index = i;
                    break;
                }
            }
            reader.readLine();
            String line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                String[] vals = line.split(Constant.DELIMITER);
                if (index < 0 || (vals[index] != null && vals[index].equals(whereValue))) {
                    for (int i : columnToBeIncluded) {
                        System.out.print(vals[i] + "\t");
                    }
                    System.out.println("\n");
                }
                line = reader.readLine();
            }
        } else {
            reader.readLine();
            String line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                String[] tableData = line.split(Constant.DELIMITER);
                for (int index : columnToBeIncluded) {
                    System.out.print(tableData[index] + "\t");
                }
                System.out.println("\n");
                line = reader.readLine();
            }
            reader.close();
        }
    }
}
