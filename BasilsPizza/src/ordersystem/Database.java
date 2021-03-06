package ordersystem;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class Database {

	private static Connection conn = null;

	private static ArrayList<Stock> stockArray;
	private static ArrayList<Customer> customersArray;
	private static ArrayList<Staff> staffArray;
	private static ArrayList<Staff> staffClockedInArray;
	private static ArrayList<Table> tablesArray;
	private static ArrayList<MenuItem> menuItemArray;
	private static ArrayList<TableOrder> tableOrdersArray;
	private static ArrayList<TableOrder> tableOrderItemsArray;
	private static ArrayList<TableOrder> tableActiveOrderItemsArray;
	private static ArrayList<CollectionOrder> collectionOrdersArray;
	private static ArrayList<CollectionOrder> collectionOrderItemsArray;
	private static ArrayList<DeliveryOrder> deliveryOrdersArray;
	private static ArrayList<DeliveryOrder> deliveryOrderItemsArray;
	

	// STOCK SQL STRINGS
	private final static String createStockTableSql = "CREATE TABLE IF NOT EXISTS stock (stock_id INTEGER PRIMARY KEY NOT NULL, item TEXT NOT NULL, price DOUBLE NOT NULL, quantity INT NOT NULL);";
	private final static String selectStockSql = "SELECT * FROM stock ORDER BY item ASC;";
	private final static String insertStockSql = "INSERT INTO stock (item, price, quantity) VALUES (?, ?, ?);";
	private final static String updateStockSql = "UPDATE stock SET item = ?, price = ?, quantity = ? WHERE item = ?;";
	private final static String deleteStockSql = "DELETE FROM stock WHERE item = ?;";
	private final static String dropStockTableSql = "DROP TABLE stock;";

	// CUSTOMERS SQL STRINGS
	private final static String createCustomersTableSql = "CREATE TABLE IF NOT EXISTS customer (customer_id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT NOT NULL, last_name TEXT NOT NULL, house_number TEXT NOT NULL, address TEXT NOT NULL, city TEXT NOT NULL, postcode TEXT NOT NULL, phone_number TEXT NOT NULL, distance DOUBLE);";
	private final static String selectCustomersSql = "SELECT * FROM customer ORDER BY last_name ASC;";
	private final static String insertCustomersSql = "INSERT INTO customer (first_name, last_name, house_number, address, city, postcode, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?);";
	private final static String deleteCustomersSql = "DELETE FROM customer WHERE (first_name = ? AND last_name = ? AND house_number = ? AND address = ? AND city = ?);";
	private final static String dropCustomersTableSql = "DROP TABLE customer;";
	private final static String selectCustomerFromIdSql = "SELECT * FROM customer WHERE customer_id = ?;";
	private final static String updateCustomerDistanceSql = "UPDATE customer SET distance = ? WHERE customer_id = ?;";
	private final static String selectLastCustomerIdSql = "SELECT seq FROM sqlite_sequence WHERE name = \"customer\";";
	private final static String selectCustomersWithinDeliveryDistanceSql = "SELECT * FROM customer WHERE distance < 10;";
	
	
	// STAFF SQL STRINGS
	private final static String createStaffTableSql = "CREATE TABLE IF NOT EXISTS staff (staff_id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT NOT NULL, last_name TEXT NOT NULL, job_title TEXT NOT NULL, last_clock_in DATETIME, last_clock_out DATETIME);";
	private final static String selectStaffSql = "SELECT staff_id, first_name, last_name, job_title, last_clock_in, last_clock_out FROM staff ORDER BY last_name ASC;";
	private final static String insertStaffSql = "INSERT INTO staff (first_name, last_name, job_title) VALUES (?, ?, ?);";
	private final static String deleteStaffSql = "DELETE FROM staff WHERE (staff_id = ?);";
	private final static String dropStaffTableSql = "DROP TABLE staff;";
	private final static String selectStaffFromIdSql = "SELECT * FROM staff WHERE staff_id = ?;";

	// STAFF CLOCK IN SQL STRINGS
	private final static String createStaffClockInSql = "CREATE TABLE IF NOT EXISTS staff_clocked_in (staff_clocked_in_id INTEGER PRIMARY KEY AUTOINCREMENT, staff_id INTEGER UNIQUE NOT NULL REFERENCES staff(staff_id), clock_in_time DATETIME);";
	private final static String selectStaffClockInSql = "SELECT staff.staff_id, first_name, last_name, job_title, staff_clocked_in.clock_in_time FROM staff INNER JOIN staff_clocked_in ON staff.staff_id = staff_clocked_in.staff_id;";
	private final static String clockInStaffSql = "INSERT INTO staff_clocked_in (staff_id, clock_in_time) VALUES (?, ?);";
	private final static String updateStaffLastClockInSql = "UPDATE staff SET last_clock_in = ? WHERE staff_id = ?;";

	// STAFF CLOCK OUT SQL STRINGS
	private final static String clockOutStaffSql = "DELETE FROM staff_clocked_in WHERE (staff_id = ?);";
	private final static String updateStaffLastClockOutSql = "UPDATE staff SET last_clock_out = ? WHERE staff_id = ?;";
	private final static String deleteAssignedStaffOnClockOutSql = "UPDATE tables SET assigned_staff = null WHERE assigned_staff = ?;";
	
	// ORDERS SQL STRINGS
	private final static String createOrdersTableSql = "CREATE TABLE IF NOT EXISTS order_table (order_table_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER REFERENCES customer(customer_id), table_id TEXT REFERENCES tables(table_id), collection_id TEXT, collection_phone_number INTEGER, order_time DATETIME NOT NULL, order_type TEXT NOT NULL, order_status TEXT NOT NULL);";
	private final static String insertTableOrderSql = "INSERT INTO order_table (table_id, order_time, order_type, order_status) VALUES (?, ?, ?, ?);";
	private final static String insertCollectionOrderSql = "INSERT INTO order_table (collection_id, collection_phone_number, order_time, order_type, order_status) VALUES (?, ?, ?, ?, ?);";
	private final static String insertDeliveryOrderSql = "INSERT INTO order_table (customer_id, order_time, order_type, order_status) VALUES (?, ?, ?, ?);";
	private final static String selectLastOrderIdSql = "SELECT seq FROM sqlite_sequence WHERE name = \"order_table\";"; // Gets last inserted autoincremented ID
	private final static String selectOrdersByTypeSql = "SELECT * FROM order_table WHERE order_type = ?;";
	private final static String selectTableOrdersSql = "SELECT * FROM order_table WHERE order_type = ? AND order_status = ?;";
	private final static String selectCustomerOrdersFromIdSql = "SELECT * FROM order_table WHERE customer_id = ?;";
	private final static String closeOrderSql = "UPDATE order_table SET order_status = ? WHERE order_table_id = ?;";
	//private final static String selectClosedTableOrdersSql = "SELECT * FROM order_table WHERE order_type = ? AND order"
	private final static String selectCustomerPhoneNumberFromOrderIdSql = "SELECT collection_phone_number FROM order_table WHERE order_table_id = ?;";
	//private final static String selectCustomerOrdersFromCustomerId = "SELECT * FROM order_table"
	private final static String selectCollectionOrdersSql = "SELECT * FROM order_table WHERE order_type = ? AND order_status = ?;";
	private final static String selectDeliveryOrdersSql = "SELECT * FROM order_table WHERE order_type = ? AND order_status = ?;"; //Duplicate SQL but easier for me to understand what the method that uses it is doing.
	
	// ORDER ITEM SQL STRINGS
	private final static String createOrderItemsTableSql = "CREATE TABLE IF NOT EXISTS order_table_item (order_table_item_id INTEGER PRIMARY KEY AUTOINCREMENT, order_table_id INTEGER NOT NULL REFERENCES order_table(order_table_id), menu_item_id INTEGER NOT NULL REFERENCES menu_item(menu_item_id), quantity INTEGER);";
	private final static String insertOrderItemSql = "INSERT INTO order_table_item (order_table_id, menu_item_id, quantity) VALUES (?, ?, ?);";
	private final static String selectOrderItemsFromIdSql = "SELECT order_table_item.order_table_item_id, order_table_item.order_table_id, order_table_item.menu_item_id, menu_item.item_name, order_table_item.quantity, menu_item.item_price FROM order_table_item INNER JOIN menu_item on order_table_item.menu_item_id = menu_item.menu_item_id WHERE order_table_item.order_table_id = ?;";
	private final static String selectActiveOrderItemsFromIdSql = "SELECT order_table_item.order_table_item_id, order_table_item.order_table_id, order_table.table_id, order_table_item.menu_item_id, menu_item.item_name, order_table_item.quantity, menu_item.item_price FROM order_table_item INNER JOIN menu_item ON order_table_item.menu_item_id = menu_item.menu_item_id INNER JOIN order_table ON order_table_item.order_table_id = order_table.order_table_id WHERE order_table.table_id = ?;";
	
	
	
	// TABLES SQL STRINGS (Had to name the table "tables" as it did not like "table").
	private final static String createTablesTableSql = "CREATE TABLE IF NOT EXISTS tables (table_id TEXT PRIMARY KEY, assigned_staff TEXT, special_requirements TEXT, order_id INTEGER REFERENCES orders(order_id));"; 
	private final static String selectTablesSql = "SELECT * FROM tables;";
	private final static String insertTableSql = "INSERT INTO tables (table_id) VALUES (?);";
	private final static String deleteTableSql = "DELETE FROM tables WHERE (table_id = ?);";
	private final static String updateTableInfoSql = "UPDATE tables SET assigned_staff = ?, special_requirements = ? WHERE table_id = ?;";
	private final static String selectTableSpecialRequirementsSql = "SELECT special_requirements FROM tables WHERE table_id = ?;";
	private final static String selectTableAssignedStaffSql = "SELECT assigned_staff FROM tables WHERE table_id = ?;";

	
	
	// TABLE ORDERS SQL STRINGS
	//private final static String createTableOrdersTableSql = "CREATE TABLE IF NOT EXISTS table_order (table_order_id INTEGER PRIMARY KEY AUTOINCREMENT, table_id INTEGER NOT NULL REFERENCES tables(table_id), order_time DATETIME NOT NULL);";
	//private final static String insertTableOrderSql = "INSERT INTO table_order(table_id, order_time) VALUES (?, ?);";
	//private final static String selectLastTableOrderIdSql = "SELECT seq FROM sqlite_sequence WHERE name = \"table_order\";"; // Gets last inserted autoincremented ID
	//private final static String selectTableOrdersSql = "SELECT * FROM table_order;";

	// TABLE ORDER ITEM SQL STRINGS
	//private final static String createTableOrderItemTableSql = "CREATE TABLE IF NOT EXISTS table_order_item (table_order_item_id INTEGER PRIMARY KEY AUTOINCREMENT, table_order_id INTEGER NOT NULL REFERENCES table_order(table_order_id), menu_item_id INTEGER NOT NULL REFERENCES menu_item(menu_item_id), quantity INTEGER NOT NULL);";
	//private final static String insertTableOrderItemSql = "INSERT INTO table_order_item(table_order_id, menu_item_id, quantity) VALUES (?, ?, ?);";
	//private final static String selectTableOrderItemsSql = "SELECT table_order_item.table_order_item_id, table_order_item.table_order_id, table_order_item.menu_item_id, menu_item.item_name, table_order_item.quantity, menu_item.item_price FROM table_order_item INNER JOIN menu_item ON table_order_item.menu_item_id = menu_item.menu_item_id WHERE table_order_item.table_order_id = ?;";
	//private final static String selectActiveTableOrderItemsSql = "SELECT table_order_item.table_order_item_id, table_order_item.table_order_id, table_order.table_id, table_order_item.menu_item_id, menu_item.item_name, table_order_item.quantity, menu_item.item_price FROM table_order_item INNER JOIN menu_item ON table_order_item.menu_item_id = menu_item.menu_item_id INNER JOIN table_order ON table_order_item.table_order_id = table_order.table_order_id WHERE table_order.table_id = ?;";
	
	
	// COLLECTION ORDERS SQL STRINGS
	//private final static String createCollectionOrdersTableSql = "CREATE TABLE IF NOT EXISTS collection_order (collection_order_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_name TEXT NOT NULL, order_time DATETIME NOT NULL);";
	//private final static String insertCollectionOrderSql = "INSERT INTO collection_order(customer_name, order_time) VALUES (?, ?);";
	//private final static String selectLastCollectionOrderIdSql = "SELECT seq FROM sqlite_sequence WHERE name = \"collection_order\";"; // Gets last inserted autoincremented ID
	//private final static String selectCollectionOrdersSql = "SELECT * FROM collection_order;";

	// COLLECTION ORDER ITEM SQL STRINGS
	//private final static String createCollectionOrderItemTableSql = "CREATE TABLE IF NOT EXISTS collection_order_item (collection_order_item_id INTEGER PRIMARY KEY AUTOINCREMENT, collection_order_id INTEGER NOT NULL REFERENCES collection_order(collection_order_id), menu_item_id INTEGER NOT NULL REFERENCES menu_item(menu_item_id), quantity INTEGER NOT NULL);";
	//private final static String insertCollectionOrderItemSql = "INSERT INTO collection_order_item(collection_order_id, menu_item_id, quantity) VALUES (?, ?, ?);";
	//private final static String selectCollectionOrderItemsSql = "SELECT collection_order_item.collection_order_item_id, collection_order_item.collection_order_id, collection_order_item.menu_item_id, menu_item.item_name, collection_order_item.quantity, menu_item.item_price FROM collection_order_item INNER JOIN menu_item ON collection_order_item.menu_item_id = menu_item.menu_item_id WHERE collection_order_item.collection_order_id = ?;";

	// DELIVERY ORDERS SQL STRINGS
	//private final static String createDeliveryOrdersTableSql = "CREATE TABLE IF NOT EXISTS delivery_order (delivery_order_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL REFERENCES customer(customer_id), order_time DATETIME NOT NULL);";
	//private final static String insertDeliveryOrderSql = "INSERT INTO delivery_order(customer_id, order_time) VALUES (?, ?);";
	//private final static String selectLastDeliveryOrderIdSql = "SELECT seq FROM sqlite_sequence WHERE name = \"delivery_order\";"; // Gets last inserted autoincremented ID
	//private final static String selectDeliveryOrdersSql = "SELECT * FROM delivery_order;";

	// DELIVERY ORDER ITEM SQL STRINGS
	//private final static String createDeliveryOrderItemTableSql = "CREATE TABLE IF NOT EXISTS delivery_order_item (delivery_order_item_id INTEGER PRIMARY KEY AUTOINCREMENT, delivery_order_id INTEGER NOT NULL REFERENCES delivery_order(delivery_order_id), menu_item_id INTEGER NOT NULL REFERENCES menu_item(menu_item_id), quantity INTEGER NOT NULL);";
	//private final static String insertDeliveryOrderItemSql = "INSERT INTO delivery_order_item(delivery_order_id, menu_item_id, quantity) VALUES (?, ?, ?);";
	//private final static String selectDeliveryOrderItemsSql = "SELECT delivery_order_item.delivery_order_item_id, delivery_order_item.delivery_order_id, delivery_order_item.menu_item_id, menu_item.item_name, delivery_order_item.quantity, menu_item.item_price FROM delivery_order_item INNER JOIN menu_item ON delivery_order_item.menu_item_id = menu_item.menu_item_id WHERE delivery_order_item.delivery_order_id = ?;";

	// DELIVERY ORDER CUSTOMER LOCATION STRINGS
	//private final static String selectDeliveryCustomerLocation = "SELECT delivery_order_item.delivery_order_item_id, delivery_order_item.delivery_order_id"

	// MENU ITEM SQL STRINGS

	private final static String createMenuItemTableSql = "CREATE TABLE IF NOT EXISTS menu_item (menu_item_id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT NOT NULL, item_type TEXT NOT NULL, item_price DOUBLE NOT NULL);";
	private final static String selectMenuItemsSql = "SELECT * FROM menu_item WHERE item_type = ?";
	private final static String insertMenuItemSql = "INSERT INTO menu_item (item_name, item_type, item_price) VALUES (?, ?, ?);";



	//private final static String foreignKeysEnabledSql = "PRAGMA foreign_keys;";

	private static void openDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:stock.db");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Opened database successfully.");
	}

	public static void closeDB() {
		try {
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Closed database successfully.");
	}

	public static void initialise() {
		try {
			openDB();
			PreparedStatement stmt = conn.prepareStatement(createStockTableSql);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(createCustomersTableSql);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(createStaffTableSql);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(createStaffClockInSql);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(createTablesTableSql);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(createOrdersTableSql);
			stmt.executeUpdate();
			
			stmt = conn.prepareStatement(createOrderItemsTableSql);
			stmt.executeUpdate();


			stmt = conn.prepareStatement(createMenuItemTableSql);
			stmt.executeUpdate();


			stmt.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		//selectStock();
		//selectCustomers();
		System.out.println("SQLite database initialisation complete.");
	}

	/////////// STOCK ///////////

	public static void selectStock() {
		try {
			stockArray = new ArrayList<Stock>();
			openDB();

			PreparedStatement selectStock = conn.prepareStatement(selectStockSql);
			ResultSet rs = selectStock.executeQuery();

			while (rs.next()) {
				String item = rs.getString("item");
				String price = rs.getString("price");
				String quantity = rs.getString("quantity");

				Stock s = new Stock(item, price, quantity);
				stockArray.add(s);
			}

			rs.close();
			selectStock.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT stock successful.");
	}

	public static void insertStock(String item, Double price, int quantity ) throws Exception{

		openDB();
		System.out.println("Inserting stock: " + item + " , " + price + " , " + quantity);

		PreparedStatement insert = conn.prepareStatement(insertStockSql);

		insert.setString(1, item);
		insert.setDouble(2, price);
		insert.setInt(3, quantity);

		insert.executeUpdate();
		insert.close();

		closeDB();

		System.out.println("INSERT stock successful.");
	}

	public static void deleteStock(String stockItem) {
		try {
			openDB();

			PreparedStatement delete = conn.prepareStatement(deleteStockSql);
			delete.setString(1, stockItem);
			delete.executeUpdate();
			delete.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Delete successful.");
	}

	public static void updateStock(String currentItem, String newItem, Double newPrice, int newQuantity) {
		try {
			openDB();

			PreparedStatement update = conn.prepareStatement(updateStockSql);

			update.setString(1, newItem);
			update.setDouble(2, newPrice);
			update.setInt(3, newQuantity);
			update.setString(4, currentItem);
			update.executeUpdate();

			update.close();
			conn.commit();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("UPDATED " + currentItem + " successfully.");
	}

	public static ArrayList<Stock> getStockArray() {
		return stockArray;
	}

	///////////////////////////CUSTOMERS

	public static void selectCustomers() {
		try {
			customersArray = new ArrayList<Customer>();
			openDB();

			PreparedStatement selectCustomers = conn.prepareStatement(selectCustomersSql);
			ResultSet rs = selectCustomers.executeQuery();

			while (rs.next()) {
				int customerId = rs.getInt("customer_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String houseNumber = rs.getString("house_number");
				String address = rs.getString("address");
				String city = rs.getString("city");
				String postcode = rs.getString("postcode");
				String phoneNumber = rs.getString("phone_number");

				Customer cust = new Customer(customerId, firstName, lastName, houseNumber, address, city, postcode, phoneNumber);
				customersArray.add(cust);
			}

			rs.close();
			selectCustomers.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT customers successful.");
	}
	
	public static void selectCustomersWithinDeliveryDistance() {
		try {
			customersArray = new ArrayList<Customer>();
			openDB();

			PreparedStatement selectCustomers = conn.prepareStatement(selectCustomersWithinDeliveryDistanceSql);
			ResultSet rs = selectCustomers.executeQuery();

			while (rs.next()) {
				int customerId = rs.getInt("customer_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String houseNumber = rs.getString("house_number");
				String address = rs.getString("address");
				String city = rs.getString("city");
				String postcode = rs.getString("postcode");
				String phoneNumber = rs.getString("phone_number");

				Customer cust = new Customer(customerId, firstName, lastName, houseNumber, address, city, postcode, phoneNumber);
				customersArray.add(cust);
			}

			rs.close();
			selectCustomers.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT customers within 10 miles successful.");
	}
	
	public static void selectCustomerFromId(int id) {
		try {
			customersArray = new ArrayList<Customer>();
			openDB();

			PreparedStatement selectCustomers = conn.prepareStatement(selectCustomerFromIdSql);
			selectCustomers.setInt(1, id);
			ResultSet rs = selectCustomers.executeQuery();
			
			

			while (rs.next()) {
				int customerId = rs.getInt("customer_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String houseNumber = rs.getString("house_number");
				String address = rs.getString("address");
				String city = rs.getString("city");
				String postcode = rs.getString("postcode");
				String phoneNumber = rs.getString("phone_number");

				Customer cust = new Customer(customerId, firstName, lastName, houseNumber, address, city, postcode, phoneNumber);
				customersArray.add(cust);
			}

			rs.close();
			selectCustomers.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT customer from ID successful.");
	}

	public static void insertCustomer(String firstName, String lastName, String houseNumber, String address, String city, String postcode, String phoneNumber) throws Exception {
		openDB();
		//System.out.println("Inserting customer: " + item + " , " + price + " , " + quantity);

		PreparedStatement insert = conn.prepareStatement(insertCustomersSql);

		insert.setString(1, firstName);
		insert.setString(2, lastName);
		insert.setString(3, houseNumber);
		insert.setString(4, address);
		insert.setString(5, city);
		insert.setString(6, postcode);
		insert.setString(7, phoneNumber);

		insert.executeUpdate();
		insert.close();

		closeDB();

		System.out.println("INSERT customer successful.");
	}

	public static void deleteCustomer(ArrayList<String> cellValues) throws SQLException {
		openDB();
		PreparedStatement delete = conn.prepareStatement(deleteCustomersSql);
		delete.setString(1, cellValues.get(0));
		System.out.println(cellValues.get(0));

		delete.setString(2, cellValues.get(1));
		System.out.println(cellValues.get(1));

		delete.setString(3, cellValues.get(2));
		System.out.println(cellValues.get(2));

		delete.setString(4, cellValues.get(3));
		System.out.println(cellValues.get(3));

		delete.setString(5, cellValues.get(4));
		System.out.println(cellValues.get(4));


		delete.executeUpdate();
		delete.close();

		closeDB();

		System.out.println("DELETE customer successful");
	}

	public static void updateCustomerDistance(double distance) {
		try {
		openDB();
		
		PreparedStatement selectLastId = conn.prepareStatement(selectLastCustomerIdSql);
		ResultSet rs = selectLastId.executeQuery();
		int lastCustomerId = 0;
		
		while(rs.next()) {
			lastCustomerId = rs.getInt("seq");
			System.out.println("LAST CUSTOMER ID" + lastCustomerId);
		}
		
		selectLastId.close();
		rs.close();

		PreparedStatement update = conn.prepareStatement(updateCustomerDistanceSql);

		update.setDouble(1, distance);
		update.setInt(2, lastCustomerId);
		
		System.out.println("THE ID IS " + lastCustomerId + " distnce " + distance);

		update.executeUpdate();
		update.close();

		closeDB();

		System.out.println("UPDATE customer distance successful.");
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Customer> getCustomersArray() {
		return customersArray;
	}

	//////////// STAFF /////////////

	public static void selectStaff() {
		try {
			staffArray = new ArrayList<Staff>();
			openDB();

			PreparedStatement selectStaff = conn.prepareStatement(selectStaffSql);
			ResultSet rs = selectStaff.executeQuery();

			while (rs.next()) {
				String staffId = rs.getString("staff_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String jobTitle = rs.getString("job_title");
				String lastClockIn = rs.getString("last_clock_in");
				String lastClockOut = rs.getString("last_clock_out");


				Staff s = new Staff(staffId, firstName, lastName, jobTitle, lastClockIn, lastClockOut);
				staffArray.add(s);
			}

			rs.close();
			selectStaff.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT successful.");
	}
	
	public static void selectStaffFromId(int staffId) {
		try {
			staffArray = new ArrayList<Staff>();
			openDB();

			PreparedStatement selectStaff = conn.prepareStatement(selectStaffFromIdSql);
			selectStaff.setInt(1, staffId);
			ResultSet rs = selectStaff.executeQuery();

			while (rs.next()) {
				String id = rs.getString("staff_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String jobTitle = rs.getString("job_title");
				String lastClockIn = rs.getString("last_clock_in");
				String lastClockOut = rs.getString("last_clock_out");


				Staff s = new Staff(id, firstName, lastName, jobTitle, lastClockIn, lastClockOut);
				staffArray.add(s);
			}

			rs.close();
			selectStaff.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT successful.");
	}



	public static void insertStaff(String firstName, String lastName, String jobTitle) throws SQLException {
		openDB();
		//System.out.println("Inserting staff: " + item + " , " + price + " , " + quantity);

		PreparedStatement insert = conn.prepareStatement(insertStaffSql);

		//insert.setString(1, staffId);
		insert.setString(1, firstName);
		insert.setString(2, lastName);
		insert.setString(3, jobTitle);

		insert.executeUpdate();
		insert.close();

		closeDB();

		System.out.println("INSERT staff successful.");
	}

	public static void updateStaff() {
		try {
			openDB();

			//PreparedStatement update = conn.prepareStatement(updateStaffSql);

			//update.setString(1, staffNumber);
			//update.setDouble(2, newPrice);
			//update.setInt(3, newQuantity);
			//update.setString(4, currentItem);
			//update.executeUpdate();

			//update.close();
			conn.commit();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("UPDATED staff successfully");
	}


	public static void deleteStaff(String staffId) {
		try {
			openDB();

			PreparedStatement delete = conn.prepareStatement(deleteStaffSql);
			delete.setString(1, staffId);
			delete.executeUpdate();
			delete.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("DELETE staff successful.");
	}

	/////// STAFF CLOCK IN ///////

	public static void selectClockedInStaff() {
		try {
			staffClockedInArray = new ArrayList<Staff>();
			openDB();

			PreparedStatement selectStaffClockedIn = conn.prepareStatement(selectStaffClockInSql);
			ResultSet rs = selectStaffClockedIn.executeQuery();

			while (rs.next()) {
				String staffId = rs.getString("staff_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String jobTitle = rs.getString("job_title");

				String clockInTime = rs.getString("clock_in_time");

				Staff s = new Staff(staffId, firstName, lastName, jobTitle, clockInTime);
				staffClockedInArray.add(s);
			}

			rs.close();
			selectStaffClockedIn.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT STAFF CLOCKED IN successful.");
	}


	public static void clockInStaff(String staffId, String dateTime) {
		try {
			openDB();

			PreparedStatement clockIn = conn.prepareStatement(clockInStaffSql);
			clockIn.setString(1, staffId);
			clockIn.setString(2, dateTime);
			clockIn.executeUpdate();
			clockIn.close();

			closeDB();
		
		} catch (SQLException e) {
			if (e.getErrorCode() == 19) {
				closeDB();
				JOptionPane.showMessageDialog(null,  "Staff is already clocked in.", 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		System.out.println("Staff clock in successful.");
	}

	public static void updateLastClockIn(String staffId, String dateTime) {
		try {
			openDB();

			PreparedStatement update = conn.prepareStatement(updateStaffLastClockInSql);
			update.setString(1, dateTime);
			update.setString(2, staffId);
			update.executeUpdate();
			update.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}



	public static void clockOutStaff(String staffId) {
		try {
			openDB();

			PreparedStatement delete = conn.prepareStatement(clockOutStaffSql);
			delete.setString(1, staffId);
			delete.executeUpdate();
			delete.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Clock out staff successful.");
	}

	public static void updateStaffClockOutTime(String staffId, String dateTime) {
		try {
			openDB();

			PreparedStatement update = conn.prepareStatement(updateStaffLastClockOutSql);
			update.setString(1, dateTime);
			update.setString(2, staffId);
			update.executeUpdate();
			update.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Update staff clock out time successful.");
	}
	
	public static void unassignTablesOnClockOut(String staff) {
		try {
			openDB();

			PreparedStatement update = conn.prepareStatement(deleteAssignedStaffOnClockOutSql);
			update.setString(1, staff);
			
			update.executeUpdate();
			update.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Unassign staff from tables successful.");
	}
	
	public static ArrayList<Staff> getStaffArray() {
		return staffArray;
	}

	public static ArrayList<Staff> getStaffClockedInArray() {
		return staffClockedInArray;
	}

	////// TABLES


	//////////// TABLES

	public static void selectTables() {
		try {
			tablesArray = new ArrayList<Table>();
			openDB();

			PreparedStatement selectTables = conn.prepareStatement(selectTablesSql);
			ResultSet rs = selectTables.executeQuery();

			while (rs.next()) {
				String tableId = rs.getString("table_id");
				String assignedStaff = rs.getString("assigned_staff");
				String specialRequirements = rs.getString("special_requirements");
				String orderId = rs.getString("order_id");

				Table t = new Table(tableId, assignedStaff, specialRequirements, orderId);
				tablesArray.add(t);
			}

			rs.close();
			selectTables.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT tables successful.");
	}

	public static void insertTable(String tableId) {
		try {
			openDB();
			//System.out.println("Inserting staff: " + item + " , " + price + " , " + quantity);

			PreparedStatement insert = conn.prepareStatement(insertTableSql);

			insert.setString(1, tableId);


			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT table successful.");
	}

	public static void deleteTable(String tableId) {
		try {
			openDB();
			//System.out.println("Inserting staff: " + item + " , " + price + " , " + quantity);

			PreparedStatement delete = conn.prepareStatement(deleteTableSql);

			delete.setString(1, tableId);


			delete.executeUpdate();
			delete.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT staff successful.");
	}

	public static void updateTableInfo(String assignedStaff, String specialRequirements, String tableId) {
		try {
			openDB();

			PreparedStatement update = conn.prepareStatement(updateTableInfoSql);


			update.setString(1, assignedStaff);
			update.setString(2, specialRequirements);
			update.setString(3, tableId);

			update.executeUpdate();
			update.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("UPDATE table information successful.");
	}

	public static String selectTableSpecialRequirements(String tableId) {
		String specialRequirements = null;
		try {
			openDB();

			PreparedStatement select = conn.prepareStatement(selectTableSpecialRequirementsSql);


			select.setString(1, tableId);
			ResultSet rs = select.executeQuery();

			while(rs.next()) {
				specialRequirements = rs.getString("special_requirements");
			}


			select.close();
			rs.close();
			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("UPDATE table information successful.");

		return specialRequirements;

	}

	public static String selectTableAssignedStaff(String tableId) {
		String assignedStaff = "";
		try {
			openDB();

			PreparedStatement select = conn.prepareStatement(selectTableAssignedStaffSql);


			select.setString(1, tableId);
			ResultSet rs = select.executeQuery();
			while(rs.next()) {
				assignedStaff = rs.getString("assigned_staff");
			}


			select.close();
			rs.close();
			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("UPDATE table information successful.");

		return assignedStaff;
	}


	public static ArrayList<Table> getTablesArray() {
		return tablesArray;
	}

	///////// TABLE ORDER /////////

	public static void insertTableOrder(String tableId, String dateTime) {
		try {
			openDB();

			PreparedStatement insert = conn.prepareStatement(insertTableOrderSql);

			insert.setString(1, tableId);
			insert.setString(2,  dateTime);
			insert.setString(3, "TABLE");
			insert.setString(4, "OPEN");

			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT table order successful.");
	}
	
	public static void closeOrder(int orderId) {
		try {
			openDB();

			PreparedStatement close = conn.prepareStatement(closeOrderSql);

			close.setString(1, "CLOSED");
			close.setInt(2, orderId);

			close.executeUpdate();
			close.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Close order successful.");
	}


	public static void selectTableOrders() {
		try {
			tableOrdersArray = new ArrayList<TableOrder>();
			openDB();

			PreparedStatement selectTableOrders = conn.prepareStatement(selectOrdersByTypeSql);
			selectTableOrders.setString(1, "TABLE");
			ResultSet rs = selectTableOrders.executeQuery();

			while (rs.next()) {
				int tableOrderId = rs.getInt("order_table_id");
				String tableId = rs.getString("table_id");
				String dateTime = rs.getString("order_time");

				TableOrder o = new TableOrder(tableOrderId, tableId, dateTime);
				tableOrdersArray.add(o);
			}

			rs.close();
			selectTableOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT table orders successful.");
	}
	
	public static void selectOpenTableOrders() {
		try {
			tableOrdersArray = new ArrayList<TableOrder>();
			openDB();

			PreparedStatement selectOpenTableOrders = conn.prepareStatement(selectTableOrdersSql);
			selectOpenTableOrders.setString(1, "TABLE");
			selectOpenTableOrders.setString(2,  "OPEN");
			ResultSet rs = selectOpenTableOrders.executeQuery();

			while (rs.next()) {
				int tableOrderId = rs.getInt("order_table_id");
				String tableId = rs.getString("table_id");
				String dateTime = rs.getString("order_time");

				TableOrder o = new TableOrder(tableOrderId, tableId, dateTime);
				tableOrdersArray.add(o);
			}

			rs.close();
			selectOpenTableOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT OPEN table orders successful.");
	}
	
	public static void selectClosedTableOrders(int orderId) {
		try {
			tableOrdersArray = new ArrayList<TableOrder>();
			openDB();

			PreparedStatement selectClosedTableOrders = conn.prepareStatement(selectTableOrdersSql);
			selectClosedTableOrders.setString(1, "TABLE");
			selectClosedTableOrders.setString(2,  "CLOSED");
			ResultSet rs = selectClosedTableOrders.executeQuery();

			while (rs.next()) {
				int tableOrderId = rs.getInt("order_table_id");
				String tableId = rs.getString("table_id");
				String dateTime = rs.getString("order_time");

				TableOrder o = new TableOrder(tableOrderId, tableId, dateTime);
				tableOrdersArray.add(o);
			}

			rs.close();
			selectClosedTableOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT CLOSED table orders successful.");
	}

	public static ArrayList<TableOrder> getTableOrdersArray() {
		return tableOrdersArray;
	}


	public static void selectOpenCollectionOrders() {
		try {
			collectionOrdersArray = new ArrayList<CollectionOrder>();
			openDB();

			PreparedStatement selectOpenCollectionOrders = conn.prepareStatement(selectCollectionOrdersSql);
			selectOpenCollectionOrders.setString(1, "COLLECTION");
			selectOpenCollectionOrders.setString(2,  "OPEN");
			ResultSet rs = selectOpenCollectionOrders.executeQuery();

			while (rs.next()) {
				int collectionOrderId = rs.getInt("order_table_id");
				String collectionId = rs.getString("collection_id");
				String dateTime = rs.getString("order_time");

				CollectionOrder o = new CollectionOrder(collectionOrderId, collectionId, dateTime);
				collectionOrdersArray.add(o);
			}

			rs.close();
			selectOpenCollectionOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT OPEN collection orders successful.");
	}
	
	public static void selectOpenDeliveryOrders() {
		try {
			deliveryOrdersArray = new ArrayList<DeliveryOrder>();
			openDB();

			PreparedStatement selectOpenDeliveryOrders = conn.prepareStatement(selectDeliveryOrdersSql);
			selectOpenDeliveryOrders.setString(1, "DELIVERY");
			selectOpenDeliveryOrders.setString(2,  "OPEN");
			ResultSet rs = selectOpenDeliveryOrders.executeQuery();

			while (rs.next()) {
				int deliveryOrderId = rs.getInt("order_table_id");
				int customerId = rs.getInt("customer_id");
				String dateTime = rs.getString("order_time");

				DeliveryOrder o = new DeliveryOrder(deliveryOrderId, customerId, dateTime);
				deliveryOrdersArray.add(o);
			}

			rs.close();
			selectOpenDeliveryOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT OPEN delivery orders successful.");
	}




	///////// TABLE ORDER ITEM /////////

	public static void selectTableOrderItems(int orderId) {
		try {
			tableOrderItemsArray = new ArrayList<TableOrder>();
			openDB();

			PreparedStatement selectTableOrderItems = conn.prepareStatement(selectOrderItemsFromIdSql);

			selectTableOrderItems.setInt(1, orderId);

			ResultSet rs = selectTableOrderItems.executeQuery();

			while (rs.next()) {
				int tableOrderId = rs.getInt("order_table_id");
				int menuItemId = rs.getInt("menu_item_id");
				String menuItemName = rs.getString("item_name");
				int quantity = rs.getInt("quantity");
				double menuItemPrice = rs.getDouble("item_price");

				TableOrder o = new TableOrder(tableOrderId, menuItemId, menuItemName, quantity, menuItemPrice);
				tableOrderItemsArray.add(o);
			}

			rs.close();
			selectTableOrderItems.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT table order items successful.");

	}

	public static ArrayList<TableOrder> getTableOrderItemsArray() {
		return tableOrderItemsArray;
	}
	
	public static void selectActiveTableOrderItems(String tableId) {
		try {
			tableActiveOrderItemsArray = new ArrayList<TableOrder>();
			openDB();

			PreparedStatement selectActiveTableOrderItems = conn.prepareStatement(selectActiveOrderItemsFromIdSql);

			selectActiveTableOrderItems.setString(1, tableId);

			ResultSet rs = selectActiveTableOrderItems.executeQuery();

			while (rs.next()) {
				int orderId = rs.getInt("order_table_id");
				String tableName = rs.getString("table_id");
				int menuItemId = rs.getInt("menu_item_id");
				String menuItemName = rs.getString("item_name");
				int quantity = rs.getInt("quantity");
				double menuItemPrice = rs.getDouble("item_price");

				TableOrder o = new TableOrder(orderId, menuItemId, tableName, menuItemName, quantity, menuItemPrice);
				tableActiveOrderItemsArray.add(o);
			}

			rs.close();
			selectActiveTableOrderItems.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT active table order items successful.");
	}
	
	public static ArrayList<TableOrder> getActiveTableOrderItemsArray() {
		return tableActiveOrderItemsArray;
	}

	public static void insertTableOrderItem(String orderId, int menuItemId, int quantity) {
		try {
			openDB();

			PreparedStatement selectLastId = conn.prepareStatement(selectLastOrderIdSql);

			ResultSet rs = selectLastId.executeQuery();

			int lastOrderId = 0;

			while(rs.next()) {

				lastOrderId = rs.getInt("seq");
				System.out.println("LAST ORDER ID " + lastOrderId);
			}


			selectLastId.close();
			rs.close();
			//closeDB();



			PreparedStatement insert = conn.prepareStatement(insertOrderItemSql);

			insert.setInt(1, lastOrderId);
			insert.setInt(2, menuItemId);
			insert.setInt(3, quantity);


			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT table order item successful.");
	} 

	///////// COLLECTION ORDER /////////

	public static void insertCollectionOrder(String customerName, int customerPhoneNumber, String dateTime) {
		try {
			openDB();

			PreparedStatement insert = conn.prepareStatement(insertCollectionOrderSql);

			System.out.println("COLLECTION ORDER VALUES " + customerName + " " + customerPhoneNumber);
			
			insert.setString(1, customerName);
			insert.setInt(2, customerPhoneNumber);
			insert.setString(3,  dateTime);
			insert.setString(4, "COLLECTION");
			insert.setString(5, "OPEN");

			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT collection order successful.");
	}
	
	public static String selectCustomerPhoneNumberFromOrderId(int orderId) {
		String customerPhoneNumber = "";
		try {
			openDB();

			PreparedStatement select = conn.prepareStatement(selectCustomerPhoneNumberFromOrderIdSql);

			
			select.setInt(1, orderId);
			
			ResultSet rs = select.executeQuery();
			
			while (rs.next()) {
				customerPhoneNumber = rs.getString("collection_phone_number");
			}
			
			rs.close();
			select.close();

			closeDB();
			
			return customerPhoneNumber;

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("SELECT customer phone number from order ID successful.");
		return customerPhoneNumber;
	}

	public static void selectCollectionOrders() {
		try {
			collectionOrdersArray = new ArrayList<CollectionOrder>();
			openDB();

			PreparedStatement selectCollectionOrders = conn.prepareStatement(selectOrdersByTypeSql);
			selectCollectionOrders.setString(1, "COLLECTION");
			ResultSet rs = selectCollectionOrders.executeQuery();

			while (rs.next()) {
				int collectionOrderId = rs.getInt("order_table_id");
				String customerName = rs.getString("collection_id");
				String dateTime = rs.getString("order_time");

				CollectionOrder c = new CollectionOrder(collectionOrderId, customerName, dateTime);
				collectionOrdersArray.add(c);
			}

			rs.close();
			selectCollectionOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT collection orders successful.");
	}

	public static ArrayList<CollectionOrder> getCollectionOrdersArray() {
		return collectionOrdersArray;
	}



	///////// COLLECTION ORDER ITEM //////////

	public static void selectCollectionOrderItem(int orderId) {
		try {
			collectionOrderItemsArray = new ArrayList<CollectionOrder>();
			openDB();

			PreparedStatement selectCollectionOrderItems = conn.prepareStatement(selectOrderItemsFromIdSql);

			selectCollectionOrderItems.setInt(1, orderId);

			ResultSet rs = selectCollectionOrderItems.executeQuery();

			while (rs.next()) {
				int collectionOrderId = rs.getInt("order_table_id");
				int menuItemId = rs.getInt("menu_item_id");
				String menuItemName = rs.getString("item_name");
				int quantity = rs.getInt("quantity");
				double menuItemPrice = rs.getDouble("item_price");

				System.out.println("DATABASE!!! " + orderId + " " + menuItemId );

				CollectionOrder c = new CollectionOrder(collectionOrderId, menuItemId, menuItemName, quantity, menuItemPrice);
				collectionOrderItemsArray.add(c);
			}

			rs.close();
			selectCollectionOrderItems.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT collection order items successful.");
	}

	public static ArrayList<CollectionOrder> getCollectionOrderItemsArray() {
		return collectionOrderItemsArray;
	}



	public static void insertCollectionOrderItem(int menuItemId, int quantity) {
		try {
			openDB();

			PreparedStatement selectLastId = conn.prepareStatement(selectLastOrderIdSql);

			ResultSet rs = selectLastId.executeQuery();

			int lastOrderId = 0;

			while(rs.next()) {

				lastOrderId = rs.getInt("seq");
				System.out.println("LAST ORDER ID " + lastOrderId);
			}


			selectLastId.close();
			rs.close();



			PreparedStatement insert = conn.prepareStatement(insertOrderItemSql);

			insert.setInt(1, lastOrderId);
			insert.setInt(2, menuItemId);
			insert.setInt(3, quantity);


			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT collection order item successful.");
	}

	///////// DELIVERY ORDER /////////

	public static void insertDeliveryOrder(int customerId, String dateTime) {
		try {
			openDB();

			PreparedStatement insert = conn.prepareStatement(insertDeliveryOrderSql);

			System.out.println("QWERTY " + customerId + " " + dateTime);
			insert.setInt(1, customerId);
			insert.setString(2,  dateTime);
			insert.setString(3, "DELIVERY");
			insert.setString(4, "OPEN");

			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT delivery order successful.");
	}

	public static void selectDeliveryOrders() {
		try {
			deliveryOrdersArray = new ArrayList<DeliveryOrder>();
			openDB();

			PreparedStatement selectDeliveryOrders = conn.prepareStatement(selectOrdersByTypeSql);
			selectDeliveryOrders.setString(1, "DELIVERY");
			ResultSet rs = selectDeliveryOrders.executeQuery();

			while (rs.next()) {
				int deliveryOrderId = rs.getInt("order_table_id");
				int customerId = rs.getInt("customer_id");
				String dateTime = rs.getString("order_time");

				DeliveryOrder d = new DeliveryOrder(deliveryOrderId, customerId, dateTime);
				deliveryOrdersArray.add(d);
			}

			rs.close();
			selectDeliveryOrders.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT delivery orders successful.");
	}

	public static ArrayList<DeliveryOrder> getDeliveryOrdersArray() {
		return deliveryOrdersArray;
	}



	///////// DELIVERY ORDER ITEM //////////

	public static void selectDeliveryOrderItem(int orderId) {
		try {
			deliveryOrderItemsArray = new ArrayList<DeliveryOrder>();
			openDB();

			PreparedStatement selectDeliveryOrderItems = conn.prepareStatement(selectOrderItemsFromIdSql);
			
			
			selectDeliveryOrderItems.setInt(1, orderId);

			ResultSet rs = selectDeliveryOrderItems.executeQuery();

			while (rs.next()) {
				int deliveryOrderId = rs.getInt("order_table_id");
				int menuItemId = rs.getInt("menu_item_id");
				String menuItemName = rs.getString("item_name");
				int quantity = rs.getInt("quantity");
				double menuItemPrice = rs.getDouble("item_price");


				DeliveryOrder d = new DeliveryOrder(deliveryOrderId, menuItemId, menuItemName, quantity, menuItemPrice);
				deliveryOrderItemsArray.add(d);
			}

			rs.close();
			selectDeliveryOrderItems.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT delivery order items successful.");
	}

	public static ArrayList<DeliveryOrder> getDeliveryOrderItemsArray() {
		return deliveryOrderItemsArray;
	}



	public static void insertDeliveryOrderItem(int menuItemId, int quantity) {
		try {
			openDB();

			PreparedStatement selectLastId = conn.prepareStatement(selectLastOrderIdSql);

			ResultSet rs = selectLastId.executeQuery();

			int lastOrderId = 0;

			while(rs.next()) {

				lastOrderId = rs.getInt("seq");
				System.out.println("LAST ORDER ID " + lastOrderId);
			}


			selectLastId.close();
			rs.close();



			PreparedStatement insert = conn.prepareStatement(insertOrderItemSql);

			insert.setInt(1, lastOrderId);
			insert.setInt(2, menuItemId);
			insert.setInt(3, quantity);


			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT delivery order item successful.");
	}
	
	
	///////// CUSTOMER ORDERS /////////
	
	
	public static void selectCustomerOrderFromCustomerId(int customerId) {
		try {
			deliveryOrderItemsArray = new ArrayList<DeliveryOrder>();
			openDB();

			PreparedStatement selectCustomerOrders = conn.prepareStatement(selectCustomerOrdersFromIdSql);
			PreparedStatement selectCustomerOrderItems = conn.prepareStatement(selectOrderItemsFromIdSql);
			
			
			selectCustomerOrders.setInt(1, customerId);

			ResultSet ordersResults = selectCustomerOrders.executeQuery();
			ResultSet orderItemsResults = null;

			while (ordersResults.next()) {
				int returnedCustomerId = ordersResults.getInt("customer_id");
				int orderId = ordersResults.getInt("order_table_id");
				String orderTime = ordersResults.getString("order_time");
				
				selectCustomerOrderItems.setInt(1, orderId);
				orderItemsResults = selectCustomerOrderItems.executeQuery();
				
				double totalPrice = 0;
				
				while (orderItemsResults.next()) {
					totalPrice += orderItemsResults.getDouble("item_price");
					
				}

				DeliveryOrder d = new DeliveryOrder(returnedCustomerId, orderId, orderTime, totalPrice);
				deliveryOrderItemsArray.add(d);
			}

			ordersResults.close();
			//orderItemsResults.close();
			selectCustomerOrders.close();
			selectCustomerOrderItems.close();
			
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT delivery order items successful.");
	}
	


	///////// MENU ITEMS //////////



	public static void selectMenuItems(String menuItemType) {
		try {
			menuItemArray = new ArrayList<MenuItem>();
			openDB();

			PreparedStatement selectMenuItem = conn.prepareStatement(selectMenuItemsSql);

			selectMenuItem.setString(1, menuItemType);

			ResultSet rs = selectMenuItem.executeQuery();

			while (rs.next()) {
				int itemId = rs.getInt("menu_item_id");
				String itemName = rs.getString("item_name");
				String itemType = rs.getString("item_type");
				Double itemPrice = rs.getDouble("item_price");

				MenuItem m = new MenuItem(itemId, itemName, itemType, itemPrice);
				menuItemArray.add(m);
			}

			rs.close();
			selectMenuItem.close();
			closeDB();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("SELECT menu successful.");
	}


	public static void insertMenuItem(String menuItemName, String menuItemType, double menuItemPrice) {
		try {
			openDB();
			//System.out.println("Inserting staff: " + item + " , " + price + " , " + quantity);

			PreparedStatement insert = conn.prepareStatement(insertMenuItemSql);

			insert.setString(1, menuItemName);
			insert.setString(2, menuItemType);
			insert.setDouble(3, menuItemPrice);


			insert.executeUpdate();
			insert.close();

			closeDB();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("INSERT menu item successful.");
	}

	public static ArrayList<MenuItem> getMenuItemArray() {
		return menuItemArray;
	}


	///////// DEBUG


	////////////// ORDERS

	/////////////////////// DEBUG
	public static void dropStockTable() { 
		try {
			openDB();

			PreparedStatement dropStockTable = conn.prepareStatement(dropStockTableSql);
			dropStockTable.executeUpdate();
			dropStockTable.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("STOCK table dropped successfully");
	}

	public static void dropCustomersTable() {
		try {
			openDB();

			PreparedStatement dropCustomersTable = conn.prepareStatement(dropCustomersTableSql);
			dropCustomersTable.executeUpdate();
			dropCustomersTable.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("CUSTOMERS table dropped successfully");
	}

	public static void dropStaffTable() {
		try {
			openDB();

			PreparedStatement dropCustomersTable = conn.prepareStatement(dropStaffTableSql);
			dropCustomersTable.executeUpdate();
			dropCustomersTable.close();

			closeDB();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("STAFF table dropped successfully");
	}

}