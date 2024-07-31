package QueryManagment;

import constant.Constant;
import java.util.*;

public class Query {
    // Global variable for managing current database name
    public static String currentDatabase = Constant.ROOT_FOLDER;

    /**
     * After login, handle user queries and divide them based on what user has written
     * user can write different queries which can be database related, transaction related, or table related
     */
    public static void handleUserQuery() {
        DatabaseQueries databaseQueries = new DatabaseQueries();
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Please hit SQL command : - SELECT | INSERT | CREATE ");
            String userQuery = sc.nextLine().trim().toLowerCase();

            // Check if its database operations
            if(userQuery.startsWith("create database") || userQuery.startsWith("use") || userQuery.startsWith("show")) {
                databaseQueries.handleDatabaseQuery(userQuery);
            }
            //if its not DB operations and if database is not selected
            else if(currentDatabase.equals(Constant.ROOT_FOLDER)) {
                System.out.println("Please select Database or create new before proceeding");
            }

            //If its in transaction or starting transaction
            else if(userQuery.equals(Constant.BEGIN_TRANSACTION) || userQuery.equals(Constant.COMMIT) || userQuery.equals(Constant.ROLLBACK) || Transaction.isInTransaction) {
                Transaction.handleTransactionQuery(userQuery);
            }
            // if it is normal User queries
            else if(userQuery.startsWith("create table") || userQuery.startsWith("insert") || userQuery.startsWith("select")) {
                ProcessQuery.handleQueries(userQuery);
            }
            // Exit from system
            else if(userQuery.startsWith("exit")) {
                System.out.println("Thank you, Visit again!");
                System.exit(0);
                break;
            } else {
                System.out.println("Your query seems Invalid please try again!");
            }
        }
    }
}
