package basilspizza;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollectionOrder {
	
	private int orderId;
	private String customerName;
	private int menuItemId;
	private int quantity;
	private String dateTime;
	private String menuItemName;
	private double menuItemPrice;
	
	// Add new collection order constructor
	public CollectionOrder(String customerName) {
		this.customerName = customerName;
	}
	

	// Add new collection order item constructor.
	public CollectionOrder(String customerName, int menuItemId, int quantity) {
		this.customerName = customerName;
		this.menuItemId = menuItemId;
		this.quantity = quantity;
		
	}
	
	// Select collection order from database constructor.
	public CollectionOrder(int orderId, String customerName, String dateTime) {
		this.orderId = orderId;
		this.customerName = customerName;
		this.dateTime = dateTime;
	}
	
	// Select collection order item from database constructor.
	public CollectionOrder(int orderId, int menuItemId, String menuItemName, double menuItemPrice) {
		this.orderId = orderId;
		this.menuItemId = menuItemId;
		this.menuItemName = menuItemName;
		this.menuItemPrice = menuItemPrice;
	}
	
	
	
	public String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date dateTime = new Date();
		String currentDateTime = dateFormat.format(dateTime);
		
		return currentDateTime;
	}
	
	public int getOrderId() {
		return orderId;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	
	
	public int getMenuItemId() {
		return menuItemId;
	}
	
	public String getMenuItemName() {
		return menuItemName;
	}
	
	public double getMenuItemPrice() {
		return menuItemPrice;
	}
	
	
	public void databaseInsertCollectionOrderItem() {
		Database.insertTableOrderItem(customerName, menuItemId, quantity);
	}

}