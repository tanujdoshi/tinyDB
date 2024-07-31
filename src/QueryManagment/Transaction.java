package QueryManagment;

import constant.Constant;
import constant.Utils;
import fileOperations.FileIO;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transaction {
    static LinkedHashMap<String, String> transactionData = new LinkedHashMap<String, String>();


    public static boolean isInTransaction = false;

    /**
     * handle all transaction related quries here, based on input divide flow of user into different methods
     * @param query
     */
    public static void handleTransactionQuery(String query) {

        if(query.equals(Constant.BEGIN_TRANSACTION)) {
            beginTransaction();
        } else if(query.equals(Constant.COMMIT)) {
            commit();
        } else if(query.equals(Constant.ROLLBACK)) {
            rollback();
        } else if(query.startsWith("insert")) {
            transactionInsert(query);
        } else if (query.startsWith("select")) {
            transactionSelect(query);
        }
        else {
            System.out.println("Coming soooon!");
        }


    }

    /**
     * begin transaction will change global variable isInTransaction to true
     */
    public static void beginTransaction() {
        Utils.addLogs("Begin transaction");
        isInTransaction = true;
    }

    /**
     * if user do commit, write all buffer data to persistant file storage
     */
    public static void commit() {
        writeToFile();
        Utils.addLogs("Commit transaction");
        isInTransaction = false;
    }

    /**
     * if user do rollback , clear intermediate data which is stored in our hashmap
     */
    public static void rollback() {
        transactionData.clear();
        Utils.addLogs("Rollback transaction");
        isInTransaction = false;
    }

    /**
     * Insert into intermediate buffer based on user input
     * @param query - insert query, with values to be inserted
     */
    public static void transactionInsert(String query) {
        try {
            Pattern pattern = Pattern.compile("^insert into (\\w+) \\((.+)\\) values \\((.+)\\)$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                String columnsPart = matcher.group(2);
                String valuesPart = matcher.group(3);

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

                StringBuffer tableData = new StringBuffer(fetchFromFile(tableName));

                if(tableData == null)
                    return;

                String[] HeaderAndData = tableData.toString().split(Constant.HEADER_SEPARATOR);
                String[] columnsFromTable = HeaderAndData[0].trim().split(Constant.DELIMITER);

                // values to inserted in table
                String valuesToBeinserted[] = new String[columnsFromTable.length];
                for (int i = 0; i < columnsFromTable.length; i++) {
                    String currentValue = columnValuesMap.get(columnsFromTable[i]);
                    valuesToBeinserted[i] = currentValue;
                }

                StringBuilder valuesToWrittenToTable = new StringBuilder();
                for (String s : valuesToBeinserted) {
                    valuesToWrittenToTable.append(s).append(Constant.DELIMITER);
                }
                tableData.append("\n" + valuesToWrittenToTable);

                Utils.addLogs("Insert in transaction");
                System.out.println("1 ROW affected in buffer");
                // putting to buffer data store
                transactionData.put(tableName, tableData.toString());
            } else {
                System.out.println("Invalid Query , please try again!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * select data from buffer
     * @param query - select query with column name as well as it can be with where clause
     */
    public static void transactionSelect (String query) {
        String regex = "^SELECT\\s+((?:\\*|\\w+\\s*,?\\s*)+)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(\\w+)\\s*=\\s*(\\w+))?;$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        if (matcher.matches()) {
            String columns = matcher.group(1);
            String tableName = matcher.group(2);
            String whereClause = matcher.group(3);

            StringBuffer tableData = new StringBuffer(fetchFromFile(tableName));
            String[] HeaderAndData = tableData.toString().split(Constant.HEADER_SEPARATOR);


            Utils.addLogs("Select in transaction");
            if(columns.trim().equals("*")) {
                selectAllFromBuffer(whereClause, matcher, HeaderAndData);
            } else {
                selectColsFromBuffer(columns,whereClause, matcher, HeaderAndData);
            }
        } else {
            System.out.println("Query does not matchhh!");
        }
    }

    /**
     * get data from actaul file for transaction usage
     * @param tableName - from which data to get
     * @return - string content of file
     */
    public static String fetchFromFile(String tableName) {
        try {
            boolean isTableExists = FileIO.isFileExists(Query.currentDatabase + "/" + tableName + ".txt");
            if (!isTableExists) {
                System.out.println("Table Does not exists!");
                return null;
            }
            if (transactionData.containsKey(tableName)) {
                return transactionData.get(tableName);
            }
            BufferedReader reader = new BufferedReader(new FileReader(Query.currentDatabase + "/" + tableName + ".txt"));
            StringBuffer tableData = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                tableData.append(line);
                line = reader.readLine();
                if (line != null) {
                    tableData.append("\n");
                }
            }
            reader.close();
            transactionData.put(tableName, tableData.toString());
            return tableData.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * write to file on commit
     */
    public static void writeToFile() {
        try{
            for (Map.Entry<String, String> entry : transactionData.entrySet()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(Query.currentDatabase +"/"+ entry.getKey() + ".txt"));
                writer.write(entry.getValue());
                writer.newLine();
                writer.close();
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }


    }

    /**
     * select * will be here
     * @param whereClause - with condition
     * @param matcher - to get another data from whereclause
     * @param HeaderAndData - current header and data from buffer
     */
    public static void selectAllFromBuffer(String whereClause, Matcher matcher, String[] HeaderAndData) {
        String[] cols = HeaderAndData[0].trim().split(Constant.DELIMITER);
        StringBuilder sb = new StringBuilder();
        for (String s : cols) {
            sb.append(s).append("\t");
        }

        System.out.println();
        System.out.println(sb);

        if(whereClause != null) {
            String whereKey = matcher.group(3);
            String whereValue = matcher.group(4);
            int index = -1;
            for (int i = 0; i < cols.length; i++) {
                System.out.println("COLSSS?" + cols[i]);
                if (cols[i].trim().equals(whereKey.trim())) {
                    index = i;
                    break;
                }
            }
            System.out.println("INDEXX:" + index);

            String[] allDataRows = HeaderAndData[1].split("\n");
            for(String row: allDataRows){
                if(row.trim() != ""){
                    String[] colData = row.split(Constant.DELIMITER);
                    if(colData[index] != null && colData[index].equals(whereValue)){
                        for(String col: colData){
                            System.out.print(col + "\t\t");
                        }
                        System.out.println("\n");
                    }
                }
            }

        } else {
            String[] allDataRows = HeaderAndData[1].split("\n");
            for(String row: allDataRows){
                String[] colData = row.split(Constant.DELIMITER);
                for(String col: colData){
                    System.out.print(col + "\t\t");
                }
                System.out.println("\n");
            }
        }

    }

    /**
     * get particular columns from buffer
     * @param columns - columns to get
     * @param whereClause - if there is where condition
     * @param matcher - to get wherekey and wherevalue of whereclause
     * @param HeaderAndData - currect table header and data
     */
    public static void selectColsFromBuffer(String columns,  String whereClause, Matcher matcher, String[] HeaderAndData) {
        System.out.println("Select with columns" + columns);
        String[] cols = HeaderAndData[0].trim().split(Constant.DELIMITER);
        java.util.List<Integer> colIndex = new ArrayList<>();
        String[] columnSplit = columns.split(",\\s*");
        List<String> reqCols = Arrays.asList(columnSplit);
        StringBuilder tableHeader = new StringBuilder();
        for (int index = 0; index < cols.length; index++) {
            if (reqCols.contains(cols[index])) {
                colIndex.add(index);
                tableHeader.append(cols[index]).append("\t");
            }
        }
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

            String[] allDataRows = HeaderAndData[1].split("\n");
            for(String row: allDataRows){
                if(row.trim() != "") {
                    String[] colData = row.split(Constant.DELIMITER);
                    if(colData[index] != null && colData[index].equals(whereValue)){
                        for (int i : colIndex) {
                            System.out.print(colData[i] + "\t");
                        }
                        System.out.println("\n");
                    }
                }
            }
        } else {
            String[] allDataRows = HeaderAndData[1].split("\n");
            for(String row: allDataRows){
                if(row.trim() != "") {
                    String[] colData = row.split(Constant.DELIMITER);
                    for (int index : colIndex) {
                        System.out.print(colData[index] + "\t");
                    }
                    System.out.println("\n");
                }
            }
        }
    }
 }

