package basilspizza;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class NewOrderGUI {

	private JPanel panelNewOrdersMain;

	private JList tablesList;
	private DefaultListModel<String> tablesListModel;

	/*
	private JList customersList;
	private DefaultListModel customersListModel;
	 */

	//private DefaultTableModel menuTableModel;
	private JTable deliveryCustomersTable, pizzaMenuTable, sidesMenuTable, drinksMenuTable, dessertsMenuTable;

	private static DefaultTableModel deliveryCustomersTableModel, pizzaMenuTableModel, sidesMenuTableModel, drinksMenuTableModel, dessertsMenuTableModel;

	private JTable orderTable;
	private DefaultTableModel orderTableModel;



	private JTextField textFieldOrderType, textFieldTableName, textFieldCustomerCollectionNameInput, 
	textFieldCustomerCollectionPhoneNumberInput,	textFieldCustomerName,
	textFieldCustomerHouseNumber, textFieldCustomerAddress,
	textFieldCustomerCity, textFieldCustomerPostcode,
	textFieldCustomerPhoneNumber, textFieldTotalPrice;


	private String orderType;
	private String tableName;
	private double totalPrice;

	private JComboBox<Integer> comboboxPizzaMenuQuantity, comboboxSidesMenuQuantity, comboboxDrinksMenuQuantity, comboboxDessertsMenuQuantity;

	private MenuGUI menuGUI;

	private String pizzaMenuQuantity;

	boolean boolTextFieldCustomerSearchPlaceholderText = false; // Used for search filter, determines if placeholder text should be placed.
	boolean boolTextFieldPizzaMenuSearchPlaceholderText = false;
	boolean boolTextFieldSidesMenuSearchPlaceholderText = false;
	boolean boolTextFieldDrinksMenuSearchPlaceholderText = false;
	boolean boolTextFieldDessertsMenuSearchPlaceholderText = false;

	HashMap orderHM;



	public NewOrderGUI() {
		menuGUI = new MenuGUI();
		initGUI();
	}

	public void initGUI() {


		panelNewOrdersMain = new JPanel(new BorderLayout());
		JPanel panelNewOrdersMainGrid = new JPanel(new GridLayout(1, 2));

		JPanel panelNewOrdersLeftSideGrid = new JPanel(new GridLayout(2, 1)); // Panel for select customer, select menu items, order summary table.
		JPanel panelNewOrdersRightSideGrid = new JPanel(new GridLayout(2, 1)); // Panel for all orders table.

		JPanel panelOrder = new JPanel(new BorderLayout());

		JPanel panelMenu = new JPanel(new BorderLayout());



		panelOrder.add(selectOrderType(), BorderLayout.CENTER);


		panelMenu.add(selectMenu(), BorderLayout.CENTER);

		panelNewOrdersLeftSideGrid.add(panelOrder);
		panelNewOrdersLeftSideGrid.add(panelMenu);




		panelNewOrdersRightSideGrid.add(orderTable());
		panelNewOrdersRightSideGrid.add(orderDetails());


		panelNewOrdersMainGrid.add(panelNewOrdersLeftSideGrid);
		panelNewOrdersMainGrid.add(panelNewOrdersRightSideGrid);

		panelNewOrdersMain.add(panelNewOrdersMainGrid, BorderLayout.CENTER);

		resetOrder();

		/*
		//Timer timer = new Timer();
		TimerTask myTask = new TimerTask() {

			@Override
			public void run() {

			}

		};
		//timer.sch

		 */


	}



	public JPanel selectOrderType() {
		JPanel panelSelectOrderTypeMain = new JPanel(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Table", tablesOrderPane());
		tabbedPane.add("Collection", collectionOrderPane());
		tabbedPane.add("Delivery", deliveryOrderPane());

		TitledBorder border = new TitledBorder("Order Type:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		tabbedPane.setBorder(border);

		panelSelectOrderTypeMain.add(tabbedPane);

		return panelSelectOrderTypeMain;

	}


	public JPanel tablesOrderPane() {


		JPanel panelSeatedOrderMain = new JPanel(new BorderLayout());
		JPanel panelSeatedOrder = new JPanel(new GridBagLayout());




		tablesListModel = new DefaultListModel();
		tablesList = new JList(tablesListModel);
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//setSpecialRequirements(getSelectedTable());
			}
		};
		tablesList.addMouseListener(mouseListener);

		populateTables();

		JScrollPane jsp = new JScrollPane(tablesList);


		panelSeatedOrderMain.add(jsp, BorderLayout.CENTER);
		//panelSeatedOrderMain.add(tablesOrderPaneNorthControls(), BorderLayout.NORTH);
		panelSeatedOrderMain.add(tablesOrderPaneSouthControls(), BorderLayout.SOUTH);

		TitledBorder border = new TitledBorder("Tables:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		panelSeatedOrderMain.setBorder(border);


		return panelSeatedOrderMain;
	}

	public JPanel tablesOrderPaneNorthControls() { // NOT IN USE
		JPanel panelSeatedOrderNorthControls = new JPanel(new GridBagLayout());

		JTextField textFieldTableSearch = new JTextField(20);

		JButton buttonTableSearchClear = new JButton("Clear Search");

		GridBagConstraints gbc = new GridBagConstraints();


		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelSeatedOrderNorthControls.add(new JLabel(), gbc);


		gbc.gridx++;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		panelSeatedOrderNorthControls.add(textFieldTableSearch, gbc);


		gbc.gridx++;
		panelSeatedOrderNorthControls.add(buttonTableSearchClear, gbc);

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelSeatedOrderNorthControls.add(new JLabel(), gbc);

		return panelSeatedOrderNorthControls;
	}

	public JPanel tablesOrderPaneSouthControls() {
		JPanel panelSeatedOrderSouthControls = new JPanel(new GridBagLayout());

		JButton buttonRefresh = new JButton("Refresh");
		buttonRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("TABLE REFRESH");
				populateTables();
			}
		});


		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				orderType = "TABLE";
				tableName = getSelectedTableName();

				setOrderDetailsOrderType(orderType);
				setOrderDetailsTableName(tableName);
				setCustomerInfoTextFieldsBackgroundGray();
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelSeatedOrderSouthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelSeatedOrderSouthControls.add(buttonRefresh, gbc);

		gbc.gridx++;
		panelSeatedOrderSouthControls.add(buttonAddToOrder, gbc);


		return panelSeatedOrderSouthControls;
	}

	public void populateTables() {


		tablesListModel.removeAllElements();

		//DefaultListModel model = new DefaultListModel();
		//tablesList.setModel(model);

		Database.selectTables();

		for (int i = 0; i < Database.getTablesArray().size(); i++) {

			String tableId = Database.getTablesArray().get(i).getTableId();
			String assignedStaff = Database.getTablesArray().get(i).getAssignedStaff();
			String specialRequirements = Database.getTablesArray().get(i).getSpecialRequirements();
			String orderId = Database.getTablesArray().get(i).getOrderId();

			//Object[] data = {tableId, assignedStaff, specialRequirements, orderId};

			tablesListModel.addElement(tableId);


		}



	}



	public JPanel collectionOrderPane() {
		JPanel panelCollectionOrdersMain = new JPanel(new BorderLayout());
		JPanel panelCollectionOrders = new JPanel(new GridBagLayout());

		textFieldCustomerCollectionNameInput = new JTextField(15);
		textFieldCustomerCollectionPhoneNumberInput = new JTextField(15);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCollectionOrders.add(new JLabel("Customer First Name: "), gbc);

		gbc.gridy++;
		panelCollectionOrders.add(new JLabel("Customer Phone Number: "), gbc);

		gbc.gridx++;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		panelCollectionOrders.add(textFieldCustomerCollectionNameInput, gbc);

		gbc.gridy++;
		panelCollectionOrders.add(textFieldCustomerCollectionPhoneNumberInput, gbc);


		panelCollectionOrdersMain.add(panelCollectionOrders, BorderLayout.CENTER);
		panelCollectionOrdersMain.add(collectionOrderPaneSouthControls(), BorderLayout.SOUTH);
		return panelCollectionOrdersMain;

	}

	public JPanel collectionOrderPaneSouthControls() {
		JPanel panelWalkInButtons = new JPanel(new GridBagLayout());

		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grayTextFieldsForCollection();
				setOrderDetailsOrderType("COLLECTION");
				setOrderDetailsCustomerName(textFieldCustomerCollectionNameInput.getText());
				setOrderDetailsCustomerPhoneNumber(textFieldCustomerCollectionPhoneNumberInput.getText());
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		// SPACING
		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelWalkInButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelWalkInButtons.add(buttonAddToOrder, gbc);

		return panelWalkInButtons;
	}

	public JPanel deliveryOrderPane() {
		JPanel panelDeliveryOrderMain = new JPanel(new BorderLayout());
		//JPanel panelDeliveryOrderPane = new JPanel(new GridBagLayout());


		deliveryCustomersTableModel = new DefaultTableModel(new String[] {
				"Customer ID", "First Name", "Last Name", "House Number", "Address",
				"City", "Postcode", "Phone Number"
		}, 0);

		deliveryCustomersTable = new JTable(deliveryCustomersTableModel) {

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);

				// Next 3 lines adapted from https://stackoverflow.com/questions/17858132/automatically-adjust-jtable-column-to-fit-content/25570812
				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(col);
				tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth())); // Sets width of columns to fill content.

				// Rows alternate in colour for readability.
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(new Color(234, 234, 234));
				}

				if (isRowSelected(row)) {
					c.setBackground(new Color(24, 134, 254));
				}

				return c;
			}
		};

		//deliveryCustomersTable.setFont(new Font("", 0, 14));
		//deliveryCustomersTable.setRowHeight(deliveryCustomersTable.getRowHeight() + 10);
		deliveryCustomersTable.setAutoCreateRowSorter(true);
		deliveryCustomersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		deliveryCustomersTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					// EDIT CUSTOMER - Do this is there's time.
				}

				try {
					int row = deliveryCustomersTable.getSelectedRow();
					String houseNumber = deliveryCustomersTable.getModel().getValueAt(row, 2).toString();
					String address = deliveryCustomersTable.getModel().getValueAt(row, 3).toString();
					String city = deliveryCustomersTable.getModel().getValueAt(row, 4).toString();
					//populateMap(houseNumber, address, city);
				} catch (Exception e) {
					/*
        				JOptionPane.showMessageDialog(frame, "Please select an item to edit.",
        						"Error", JOptionPane.ERROR_MESSAGE);
					 */
				}
			}
		});

		populateDeliveryCustomers();

		JScrollPane jsp = new JScrollPane(deliveryCustomersTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		deliveryCustomersTable.setFillsViewportHeight(true);


		panelDeliveryOrderMain.add(jsp, BorderLayout.CENTER);
		panelDeliveryOrderMain.add(deliveryOrderPaneNorthControls(), BorderLayout.NORTH);
		panelDeliveryOrderMain.add(deliveryOrderPaneSouthControls(), BorderLayout.SOUTH);

		TitledBorder border = new TitledBorder("Customers:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		panelDeliveryOrderMain.setBorder(border);

		return panelDeliveryOrderMain;
	}

	public JPanel deliveryOrderPaneNorthControls() {
		JPanel panelDeliveryOrderNorthControls = new JPanel (new GridBagLayout());

		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(deliveryCustomersTable.getModel());
		deliveryCustomersTable.setRowSorter(rowSorter);

		JTextField textFieldCustomerSearch = new JTextField("Type to search...", 20);

		// Following search code adapted from https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java
		textFieldCustomerSearch.getDocument().addDocumentListener(new DocumentListener() {
			// Set up search filter function
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = textFieldCustomerSearch.getText();
				if (!textFieldCustomerSearch.getText().equals("Type to search...")) {
					if(text.trim().length() == 0) {
						rowSorter.setRowFilter(null);
					} else {
						rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = textFieldCustomerSearch.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Exception");
			}

		});

		textFieldCustomerSearch.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (boolTextFieldCustomerSearchPlaceholderText == false) {
					boolTextFieldCustomerSearchPlaceholderText = true;
					textFieldCustomerSearch.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				boolTextFieldCustomerSearchPlaceholderText = false;
				textFieldCustomerSearch.setText("Type to search...");
			}

		});









		JButton buttonCustmerSearchClear = new JButton("Clear Search");

		JButton buttonNewCustomer = new JButton("New Customer");


		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelDeliveryOrderNorthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelDeliveryOrderNorthControls.add(textFieldCustomerSearch, gbc);

		gbc.gridx++;
		panelDeliveryOrderNorthControls.add(buttonCustmerSearchClear, gbc);

		gbc.gridx++;
		panelDeliveryOrderNorthControls.add(buttonNewCustomer, gbc);




		return panelDeliveryOrderNorthControls;


	}

	public JPanel deliveryOrderPaneSouthControls() {
		JPanel panelDeliveryOrderSouthControls = new JPanel(new GridBagLayout());

		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOrderDetailsOrderType("DELIVERY");
				setTableNameBackgroundGray();
				setOrderDetailsTableName("");

				String customerName = getDeliveryCustomerFirstName() + " " + getDeliveryCustomerLastName();

				setOrderDetailsCustomerName(customerName);
				setOrderDetailsCustomerHouseNumber(getDeliveryCustomerHouseNumber());
				setOrderDetailsCustomerAddress(getDeliveryCustomerAddress());
				setOrderDetailsCustomerCity(getDeliveryCustomerCity());
				setOrderDetailsCustomerPostcode(getDeliveryCustomerPostcode());
				setOrderDetailsCustomerPhoneNumber(getDeliveryCustomerPhoneNumber());
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		// SPACING
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelDeliveryOrderSouthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelDeliveryOrderSouthControls.add(buttonAddToOrder, gbc);

		return panelDeliveryOrderSouthControls;
	}

	public void populateDeliveryCustomers() {
		int rows = deliveryCustomersTableModel.getRowCount();
		for (int i = rows - 1; i >= 0; i --) {
			deliveryCustomersTableModel.removeRow(i);
		}

		Database.selectCustomers();

		for (int i = 0; i < Database.getCustomersArray().size(); i++) {

			int customerId = Database.getCustomersArray().get(i).getCustomerId();
			String firstName = Database.getCustomersArray().get(i).getCustomerFirstName();
			String lastName = Database.getCustomersArray().get(i).getCustomerLastName(); 
			String houseNumber = Database.getCustomersArray().get(i).getCustomerHouseNumber();
			String address = Database.getCustomersArray().get(i).getCustomerAddress();
			String city = Database.getCustomersArray().get(i).getCustomerCity();
			String postcode = Database.getCustomersArray().get(i).getCustomerPostcode();
			String phoneNumber = Database.getCustomersArray().get(i).getCustomerPhoneNumber();


			Object[] data = {customerId, firstName, lastName, houseNumber, address, city,
					postcode, phoneNumber
			};

			deliveryCustomersTableModel.addRow(data);
			deliveryCustomersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			deliveryCustomersTable.getSelectionModel().setSelectionInterval(0, 0);
			deliveryCustomersTable.setColumnSelectionInterval(0, 0);
			deliveryCustomersTable.requestFocusInWindow();



		}

	}


	public JPanel selectMenu() {
		JPanel panelSelectMenu = new JPanel(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Pizza", pizzaMenuTable());
		tabbedPane.add("Sides", sidesMenuTable());
		tabbedPane.add("Drinks", drinksMenuTable());
		tabbedPane.add("Desserts", dessertsMenuTable());





		TitledBorder border = new TitledBorder("Menu:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		tabbedPane.setBorder(border);

		panelSelectMenu.add(tabbedPane, BorderLayout.CENTER);



		return panelSelectMenu;
	}


	public JPanel pizzaMenuTable() {
		JPanel panelPizzaMenuTable = new JPanel(new BorderLayout());



		pizzaMenuTableModel = menuGUI.getPizzaMenuTableModel();

		pizzaMenuTable = new JTable(pizzaMenuTableModel) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);
				
				// Next 3 lines adapted from https://stackoverflow.com/questions/17858132/automatically-adjust-jtable-column-to-fit-content/25570812
				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(col);
				tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth())); // Sets width of columns to fill content.


				// Rows alternate in colour for readability.
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(new Color(234, 234, 234));
				}

				if (isRowSelected(row)) {
					c.setBackground(new Color(24, 134, 254));
				}

				return c;
			}
		};

		pizzaMenuTable.setFont(new Font("", 0, 14));
		pizzaMenuTable.setRowHeight(pizzaMenuTable.getRowHeight() + 10);
		pizzaMenuTable.setAutoCreateRowSorter(true);
		pizzaMenuTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		pizzaMenuTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					// EDIT CUSTOMER - Do this is there's time.
				}

				try {
					int row = pizzaMenuTable.getSelectedRow();
					String houseNumber = pizzaMenuTable.getModel().getValueAt(row, 2).toString();
					String address = pizzaMenuTable.getModel().getValueAt(row, 3).toString();
					String city = pizzaMenuTable.getModel().getValueAt(row, 4).toString();
					//populateMap(houseNumber, address, city);
				} catch (Exception e) {
					/*
        				JOptionPane.showMessageDialog(frame, "Please select an item to edit.",
        						"Error", JOptionPane.ERROR_MESSAGE);
					 */
				}
			}
		});

		//populateCustomersTable();

		JScrollPane jsp = new JScrollPane(pizzaMenuTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		pizzaMenuTable.setFillsViewportHeight(true);

		panelPizzaMenuTable.add(jsp);


		panelPizzaMenuTable.add(pizzaMenuNorthControls(), BorderLayout.NORTH);
		panelPizzaMenuTable.add(pizzaMenuSouthControls(), BorderLayout.SOUTH);

		return panelPizzaMenuTable;
	}

	public JPanel pizzaMenuNorthControls() {
		JPanel panelPizzaNorthControls = new JPanel(new GridBagLayout());

		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(pizzaMenuTable.getModel());
		pizzaMenuTable.setRowSorter(rowSorter);

		JTextField textFieldPizzaMenuSearch = new JTextField("Type to search...", 20);

		// Following search code adapted from https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java
		textFieldPizzaMenuSearch.getDocument().addDocumentListener(new DocumentListener() {
			// Set up search filter function
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = textFieldPizzaMenuSearch.getText();
				if (!textFieldPizzaMenuSearch.getText().equals("Type to search...")) {
					if(text.trim().length() == 0) {
						rowSorter.setRowFilter(null);
					} else {
						rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = textFieldPizzaMenuSearch.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Exception");
			}

		});

		textFieldPizzaMenuSearch.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (boolTextFieldPizzaMenuSearchPlaceholderText == false) {
					boolTextFieldPizzaMenuSearchPlaceholderText = true;
					textFieldPizzaMenuSearch.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				boolTextFieldPizzaMenuSearchPlaceholderText = false;
				textFieldPizzaMenuSearch.setText("Type to search...");
			}

		});





		JButton buttonClearSearch = new JButton("Clear Search");

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelPizzaNorthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelPizzaNorthControls.add(textFieldPizzaMenuSearch, gbc);

		gbc.gridx++;
		panelPizzaNorthControls.add(buttonClearSearch, gbc);

		return panelPizzaNorthControls;


	}

	public JPanel pizzaMenuSouthControls() {
		JPanel panelPizzaMenuButtons = new JPanel(new GridBagLayout());

		JButton buttonRefresh = new JButton("Refresh");
		buttonRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("REFRESH PRESS");
				populatePizzaMenuTable();
			}
		});

		comboboxPizzaMenuQuantity = new JComboBox<Integer>();
		for (int i = 1; i <= 50; i ++) {
			comboboxPizzaMenuQuantity.addItem(i);;
		}
		comboboxPizzaMenuQuantity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pizzaMenuQuantity = (comboboxPizzaMenuQuantity.getSelectedItem().toString());
			}
		});


		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] data = {getSelectedMenuItemID(pizzaMenuTable), getSelectedMenuItem(pizzaMenuTable), getSelectedMenuItemPrice(pizzaMenuTable), getQuantity(comboboxPizzaMenuQuantity)
				};
				orderTableModel.addRow(data);

				totalPrice += Double.parseDouble(getSelectedMenuItemPrice(pizzaMenuTable)) * Integer.parseInt(getQuantity(comboboxPizzaMenuQuantity));
				setTotalPrice(totalPrice);
				
				orderHM.put(getSelectedMenuItemID(pizzaMenuTable), getQuantity(comboboxPizzaMenuQuantity));
				
				
			}
			
		});




		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelPizzaMenuButtons.add(buttonRefresh, gbc);

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelPizzaMenuButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelPizzaMenuButtons.add(new JLabel("Quantity: "), gbc);

		gbc.gridx++;
		panelPizzaMenuButtons.add(comboboxPizzaMenuQuantity, gbc);

		gbc.gridx++;
		panelPizzaMenuButtons.add(buttonAddToOrder, gbc);

		return panelPizzaMenuButtons;
	}

	public void populatePizzaMenuTable() {
		MenuGUI m = new MenuGUI();
		pizzaMenuTableModel = m.getPizzaMenuTableModel();
		pizzaMenuTable.setModel(pizzaMenuTableModel);
	}

	public JPanel sidesMenuTable() {
		JPanel panelSidesMenuTable = new JPanel(new BorderLayout());



		sidesMenuTableModel = menuGUI.getSidesMenuTableModel();

		sidesMenuTable = new JTable(sidesMenuTableModel) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);

				// Next 3 lines adapted from https://stackoverflow.com/questions/17858132/automatically-adjust-jtable-column-to-fit-content/25570812
				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(col);
				tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth())); // Sets width of columns to fill content.

				
				// Rows alternate in colour for readability.
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(new Color(234, 234, 234));
				}

				if (isRowSelected(row)) {
					c.setBackground(new Color(24, 134, 254));
				}

				return c;
			}
		};

		sidesMenuTable.setFont(new Font("", 0, 14));
		sidesMenuTable.setRowHeight(sidesMenuTable.getRowHeight() + 10);
		sidesMenuTable.setAutoCreateRowSorter(true);
		sidesMenuTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		sidesMenuTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					// EDIT CUSTOMER - Do this is there's time.
				}

				try {
					int row = sidesMenuTable.getSelectedRow();
					String houseNumber = sidesMenuTable.getModel().getValueAt(row, 2).toString();
					String address = sidesMenuTable.getModel().getValueAt(row, 3).toString();
					String city = sidesMenuTable.getModel().getValueAt(row, 4).toString();
					//populateMap(houseNumber, address, city);
				} catch (Exception e) {
					/*
        				JOptionPane.showMessageDialog(frame, "Please select an item to edit.",
        						"Error", JOptionPane.ERROR_MESSAGE);
					 */
				}
			}
		});

		//populateCustomersTable();

		JScrollPane jsp = new JScrollPane(sidesMenuTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		sidesMenuTable.setFillsViewportHeight(true);

		panelSidesMenuTable.add(jsp);


		panelSidesMenuTable.add(sidesMenuNorthControls(), BorderLayout.NORTH);
		panelSidesMenuTable.add(sidesMenuSouthControls(), BorderLayout.SOUTH);

		return panelSidesMenuTable;
	}

	public JPanel sidesMenuNorthControls() {
		JPanel panelSidesNorthControls = new JPanel(new GridBagLayout());

		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(sidesMenuTable.getModel());
		sidesMenuTable.setRowSorter(rowSorter);

		JTextField textFieldSidesMenuSearch = new JTextField("Type to search...", 20);

		// Following search code adapted from https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java
		textFieldSidesMenuSearch.getDocument().addDocumentListener(new DocumentListener() {
			// Set up search filter function
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = textFieldSidesMenuSearch.getText();
				if (!textFieldSidesMenuSearch.getText().equals("Type to search...")) {
					if(text.trim().length() == 0) {
						rowSorter.setRowFilter(null);
					} else {
						rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = textFieldSidesMenuSearch.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Exception");
			}

		});

		textFieldSidesMenuSearch.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (boolTextFieldSidesMenuSearchPlaceholderText == false) {
					boolTextFieldSidesMenuSearchPlaceholderText = true;
					textFieldSidesMenuSearch.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				boolTextFieldSidesMenuSearchPlaceholderText = false;
				textFieldSidesMenuSearch.setText("Type to search...");
			}

		});





		JButton buttonClearSearch = new JButton("Clear Search");

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelSidesNorthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelSidesNorthControls.add(textFieldSidesMenuSearch, gbc);

		gbc.gridx++;
		panelSidesNorthControls.add(buttonClearSearch, gbc);

		return panelSidesNorthControls;

	}


	public JPanel sidesMenuSouthControls() {
		JPanel panelSidesMenuButtons = new JPanel(new GridBagLayout());

		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] data = {getSelectedMenuItemID(sidesMenuTable), getSelectedMenuItem(sidesMenuTable), getSelectedMenuItemPrice(sidesMenuTable), getQuantity(comboboxSidesMenuQuantity)
				};
				orderTableModel.addRow(data);

				totalPrice += Double.parseDouble(getSelectedMenuItemPrice(sidesMenuTable)) * Integer.parseInt(getQuantity(comboboxSidesMenuQuantity));
				setTotalPrice(totalPrice);
				
				orderHM.put(getSelectedMenuItemID(sidesMenuTable), getQuantity(comboboxSidesMenuQuantity));
			}
		});

		comboboxSidesMenuQuantity = new JComboBox<Integer>();
		for (int i = 1; i <= 50; i ++) {
			comboboxSidesMenuQuantity.addItem(i);;
		}

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelSidesMenuButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelSidesMenuButtons.add(new JLabel("Quantity: "), gbc);

		gbc.gridx++;
		panelSidesMenuButtons.add(comboboxSidesMenuQuantity, gbc);

		gbc.gridx++;
		panelSidesMenuButtons.add(buttonAddToOrder, gbc);

		return panelSidesMenuButtons;
	}

	public JPanel drinksMenuTable() {
		JPanel panelDrinksMenuTable = new JPanel(new BorderLayout());




		drinksMenuTableModel = menuGUI.getDrinksMenuTableModel();

		drinksMenuTable = new JTable(drinksMenuTableModel) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);

				// Next 3 lines adapted from https://stackoverflow.com/questions/17858132/automatically-adjust-jtable-column-to-fit-content/25570812
				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(col);
				tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth())); // Sets width of columns to fill content.

				// Rows alternate in colour for readability.
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(new Color(234, 234, 234));
				}

				if (isRowSelected(row)) {
					c.setBackground(new Color(24, 134, 254));
				}

				return c;
			}
		};

		drinksMenuTable.setFont(new Font("", 0, 14));
		drinksMenuTable.setRowHeight(drinksMenuTable.getRowHeight() + 10);
		drinksMenuTable.setAutoCreateRowSorter(true);
		drinksMenuTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		drinksMenuTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					// EDIT CUSTOMER - Do this is there's time.
				}


			}
		});

		//populateCustomersTable();

		JScrollPane jsp = new JScrollPane(drinksMenuTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		drinksMenuTable.setFillsViewportHeight(true);

		panelDrinksMenuTable.add(jsp);


		panelDrinksMenuTable.add(drinksMenuNorthControls(), BorderLayout.NORTH);
		panelDrinksMenuTable.add(drinksMenuSouthControls(), BorderLayout.SOUTH);

		return panelDrinksMenuTable;
	}

	public JPanel drinksMenuNorthControls() {
		JPanel panelDrinksMenuNorthControls = new JPanel(new GridBagLayout());

		JTextField textFieldDrinksMenuSearch = new JTextField("Type to search...", 20);

		JButton buttonClearSearch = new JButton("Clear Search");
		buttonClearSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textFieldDrinksMenuSearch.setText("");
			}
		});

		// Following search code adapted from https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java

		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(drinksMenuTable.getModel());
		drinksMenuTable.setRowSorter(rowSorter);

		textFieldDrinksMenuSearch.getDocument().addDocumentListener(new DocumentListener() {
			// Set up search filter function
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = textFieldDrinksMenuSearch.getText();
				if (!textFieldDrinksMenuSearch.getText().equals("Type to search...")) {
					if(text.trim().length() == 0) {
						rowSorter.setRowFilter(null);
					} else {
						rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = textFieldDrinksMenuSearch.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Exception");
			}

		});

		textFieldDrinksMenuSearch.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (boolTextFieldSidesMenuSearchPlaceholderText == false) {
					boolTextFieldSidesMenuSearchPlaceholderText = true;
					textFieldDrinksMenuSearch.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				boolTextFieldSidesMenuSearchPlaceholderText = false;
				textFieldDrinksMenuSearch.setText("Type to search...");
			}

		});

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelDrinksMenuNorthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelDrinksMenuNorthControls.add(textFieldDrinksMenuSearch, gbc);

		gbc.gridx++;
		panelDrinksMenuNorthControls.add(buttonClearSearch, gbc);


		return panelDrinksMenuNorthControls;
	}


	public JPanel drinksMenuSouthControls() {
		JPanel panelDrinksMenuButtons = new JPanel(new GridBagLayout());

		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] data = {getSelectedMenuItemID(drinksMenuTable), getSelectedMenuItem(drinksMenuTable), getSelectedMenuItemPrice(drinksMenuTable), getQuantity(comboboxDrinksMenuQuantity)
				};
				orderTableModel.addRow(data);

				totalPrice += Double.parseDouble(getSelectedMenuItemPrice(drinksMenuTable)) * Integer.parseInt(getQuantity(comboboxDrinksMenuQuantity));
				setTotalPrice(totalPrice);
				
				orderHM.put(getSelectedMenuItemID(drinksMenuTable), getQuantity(comboboxDrinksMenuQuantity));
			}
		});

		comboboxDrinksMenuQuantity = new JComboBox<Integer>();
		for (int i = 1; i <= 50; i ++) {
			comboboxDrinksMenuQuantity.addItem(i);;
		}

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelDrinksMenuButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelDrinksMenuButtons.add(new JLabel("Quantity: "), gbc);

		gbc.gridx++;
		panelDrinksMenuButtons.add(comboboxDrinksMenuQuantity, gbc);

		gbc.gridx++;
		panelDrinksMenuButtons.add(buttonAddToOrder, gbc);

		return panelDrinksMenuButtons;
	}

	public JPanel dessertsMenuTable() {
		JPanel panelDessertsMenuTable = new JPanel(new BorderLayout());




		dessertsMenuTableModel = menuGUI.getDessertsMenuTableModel();

		dessertsMenuTable = new JTable(dessertsMenuTableModel) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);

				// Next 3 lines adapted from https://stackoverflow.com/questions/17858132/automatically-adjust-jtable-column-to-fit-content/25570812
				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(col);
				tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth())); // Sets width of columns to fill content.

				// Rows alternate in colour for readability.
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(new Color(234, 234, 234));
				}

				if (isRowSelected(row)) {
					c.setBackground(new Color(24, 134, 254));
				}

				return c;
			}
		};

		dessertsMenuTable.setFont(new Font("", 0, 14));
		dessertsMenuTable.setRowHeight(dessertsMenuTable.getRowHeight() + 10);
		dessertsMenuTable.setAutoCreateRowSorter(true);
		dessertsMenuTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		dessertsMenuTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					// EDIT CUSTOMER - Do this is there's time.
				}


			}
		});

		//populateCustomersTable();

		JScrollPane jsp = new JScrollPane(dessertsMenuTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		dessertsMenuTable.setFillsViewportHeight(true);

		panelDessertsMenuTable.add(jsp);


		panelDessertsMenuTable.add(dessertsMenuNorthControls(), BorderLayout.NORTH);
		panelDessertsMenuTable.add(dessertsMenuSouthControls(), BorderLayout.SOUTH);

		return panelDessertsMenuTable;
	}

	public JPanel dessertsMenuNorthControls() {
		JPanel panelDessertsMenuNorthControls = new JPanel(new GridBagLayout());

		JButton buttonClearSearch = new JButton("Clear Search");

		JTextField textFieldDessertsMenuSearch = new JTextField("Type to search...", 20);

		// Following search code adapted from https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java

		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(dessertsMenuTable.getModel());
		dessertsMenuTable.setRowSorter(rowSorter);

		textFieldDessertsMenuSearch.getDocument().addDocumentListener(new DocumentListener() {
			// Set up search filter function
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = textFieldDessertsMenuSearch.getText();
				if (!textFieldDessertsMenuSearch.getText().equals("Type to search...")) {
					if(text.trim().length() == 0) {
						rowSorter.setRowFilter(null);
					} else {
						rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = textFieldDessertsMenuSearch.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Exception");
			}

		});

		textFieldDessertsMenuSearch.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (boolTextFieldDessertsMenuSearchPlaceholderText == false) {
					boolTextFieldDessertsMenuSearchPlaceholderText = true;
					textFieldDessertsMenuSearch.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				boolTextFieldDessertsMenuSearchPlaceholderText = false;
				textFieldDessertsMenuSearch.setText("Type to search...");
			}

		});

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelDessertsMenuNorthControls.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelDessertsMenuNorthControls.add(textFieldDessertsMenuSearch, gbc);

		gbc.gridx++;
		panelDessertsMenuNorthControls.add(buttonClearSearch, gbc);



		return panelDessertsMenuNorthControls;
	}


	public JPanel dessertsMenuSouthControls() {
		JPanel panelDessertsMenuButtons = new JPanel(new GridBagLayout());

		JButton buttonAddToOrder = new JButton("Add to Order");
		buttonAddToOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] data = {getSelectedMenuItemID(dessertsMenuTable), getSelectedMenuItem(dessertsMenuTable), getSelectedMenuItemPrice(dessertsMenuTable), getQuantity(comboboxDessertsMenuQuantity)
				};
				orderTableModel.addRow(data);

				totalPrice += Double.parseDouble(getSelectedMenuItemPrice(dessertsMenuTable)) * Integer.parseInt(getQuantity(comboboxDessertsMenuQuantity));
				setTotalPrice(totalPrice);
				
				orderHM.put(getSelectedMenuItemID(dessertsMenuTable), getQuantity(comboboxDessertsMenuQuantity));
			}
		});

		comboboxDessertsMenuQuantity = new JComboBox<Integer>();
		for (int i = 1; i <= 50; i ++) {
			comboboxDessertsMenuQuantity.addItem(i);;
		}

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelDessertsMenuButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelDessertsMenuButtons.add(new JLabel("Quantity: "), gbc);

		gbc.gridx++;
		panelDessertsMenuButtons.add(comboboxDessertsMenuQuantity, gbc);

		gbc.gridx++;
		panelDessertsMenuButtons.add(buttonAddToOrder, gbc);

		return panelDessertsMenuButtons;
	}

	/*
	public void setPizzaMenuTableModel() {




		//pizzaMenuTableModel = menuGUI.getPizzaMenuTableModel();
		//menuTable.setModel(pizzaMenuTableModel);



	}

	public void setdrinksMenuTableModel() {

		drinksMenuTableModel = menuGUI.getDrinksMenuTableModel();
		menuTable.setModel(drinksMenuTableModel);
	}


	 */


	public JPanel orderTable() {
		JPanel panelOrderTableMain = new JPanel(new BorderLayout());

		orderTableModel = new DefaultTableModel(new String[] {
				"Menu Item ID", "Menu Item", "Price", "Quantity"

		}, 0);

		orderTable = new JTable(orderTableModel ) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);

				// Next 3 lines adapted from https://stackoverflow.com/questions/17858132/automatically-adjust-jtable-column-to-fit-content/25570812
				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(col);
				tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth())); // Sets width of columns to fill content.

				// Rows alternate in colour for readability.
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(new Color(234, 234, 234));
				}

				if (isRowSelected(row)) {
					c.setBackground(new Color(24, 134, 254));
				}

				return c;
			}
		};

		orderTable.setFont(new Font("", 0, 14));
		orderTable.setRowHeight(orderTable.getRowHeight() + 10);
		orderTable.setAutoCreateRowSorter(true);
		orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		orderTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					// EDIT CUSTOMER - Do this is there's time.
				}

				try {
					int row = orderTable.getSelectedRow();
					String houseNumber = orderTable.getModel().getValueAt(row, 2).toString();
					String address = orderTable.getModel().getValueAt(row, 3).toString();
					String city = orderTable.getModel().getValueAt(row, 4).toString();
					//populateMap(houseNumber, address, city);
				} catch (Exception e) {
					/*
        				JOptionPane.showMessageDialog(frame, "Please select an item to edit.",
        						"Error", JOptionPane.ERROR_MESSAGE);
					 */
				}
			}
		});

		//populateCustomersTable();

		JScrollPane jsp = new JScrollPane(orderTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		orderTable.setFillsViewportHeight(true);

		panelOrderTableMain.add(jsp);
		panelOrderTableMain.add(orderTableSouthControls(), BorderLayout.SOUTH);

		TitledBorder border = new TitledBorder("Order:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		panelOrderTableMain.setBorder(border);

		return panelOrderTableMain;
	}

	public JPanel orderTableSouthControls() {
		JPanel panelOrderTableButtons = new JPanel(new GridBagLayout());

		JButton buttonDelete = new JButton("Delete");
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = orderTable.getSelectedRow();
				orderTableModel.removeRow(row);
			}
		});

		JButton buttonClear = new JButton("Clear Order");
		buttonClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rows = orderTableModel.getRowCount();
				for (int i = rows - 1; i >= 0; i --) {
					orderTableModel.removeRow(i);
				}

				setTotalPrice(0.00);
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelOrderTableButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelOrderTableButtons.add(buttonDelete, gbc);

		gbc.gridx++;
		panelOrderTableButtons.add(buttonClear, gbc);

		return panelOrderTableButtons;
	}

	public JPanel orderDetails() {
		JPanel panelOrderSummaryMain = new JPanel(new BorderLayout());
		JPanel panelOrderSummary = new JPanel(new GridBagLayout());

		textFieldOrderType = new JTextField(20);
		textFieldOrderType.setEditable(false);

		textFieldTableName = new JTextField(20);
		textFieldTableName.setEditable(false);

		textFieldCustomerName = new JTextField(20);
		textFieldCustomerName.setEditable(false);

		textFieldCustomerHouseNumber = new JTextField(20);
		textFieldCustomerHouseNumber.setEditable(false);

		textFieldCustomerAddress = new JTextField(20);
		textFieldCustomerAddress.setEditable(false);

		textFieldCustomerCity = new JTextField(20);
		textFieldCustomerCity.setEditable(false);

		textFieldCustomerPostcode = new JTextField(20);
		textFieldCustomerPostcode.setEditable(false);

		textFieldCustomerPhoneNumber = new JTextField(20);
		textFieldCustomerPhoneNumber.setEditable(false);

		textFieldTotalPrice = new JTextField(20);
		textFieldTotalPrice.setEditable(false);








		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(10, 20, 0, 0);
		panelOrderSummary.add(new JLabel("Order Type: "), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 20, 0, 0);
		panelOrderSummary.add(new JLabel("Table Name: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Customer Name: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Customer House Number: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Customer Address: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Customer City: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Customer Postcode: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Customer Phone Number: "), gbc);

		gbc.gridy++;
		panelOrderSummary.add(new JLabel("Total Price: "), gbc);

		// TEXT FIELDS
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_START;
		panelOrderSummary.add(textFieldOrderType, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0 , 0);
		panelOrderSummary.add(textFieldTableName, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldCustomerName, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldCustomerHouseNumber, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldCustomerAddress, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldCustomerCity, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldCustomerPostcode, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldCustomerPhoneNumber, gbc);

		gbc.gridy++;
		panelOrderSummary.add(textFieldTotalPrice, gbc);


		// SPACING
		gbc.gridx = 0;
		gbc.gridy = 20;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelOrderSummary.add(new JLabel(), gbc);



		TitledBorder border = new TitledBorder("Order Details:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		panelOrderSummaryMain.setBorder(border);

		panelOrderSummaryMain.add(panelOrderSummary, BorderLayout.CENTER);
		panelOrderSummaryMain.add(orderDetailsSouthControls(), BorderLayout.SOUTH);

		return panelOrderSummaryMain;

	}

	public JPanel orderDetailsSouthControls() {
		JPanel panelOrderSummarySouth = new JPanel(new GridBagLayout());

		JButton buttonResetOrder = new JButton("Reset Order");
		buttonResetOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetOrder();
			}
		});

		JButton buttonPlaceOrder = new JButton("Place Order");
		buttonPlaceOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (getOrderDetailsOrderType().equals("TABLE")) {
					
					Order newOrder = new Order(getOrderDetailsTableName());
					Database.insertTableOrder(newOrder.getTableName(), newOrder.getCurrentDateTime());
					
					Set hmSet = orderHM.entrySet();
					Iterator i = hmSet.iterator();
					while(i.hasNext()) {
						
						Map.Entry me = (Map.Entry)i.next();
					
						Order newOrderItem = new Order(getOrderDetailsTableName(), Integer.parseInt(me.getKey().toString()), Integer.parseInt(me.getValue().toString()));
						newOrderItem.databaseInsertTableOrderItem();
					}
					
					/*
					for (int i = 0; i < menuItemsArray.size(); i ++) {
						//Database.insertTableOrderItem(getOrderDetailsTableName(), Integer.parseInt(menuItemsArray.get(i)), getQuantity());
					}
					*/
					
				}
				//Order o = new Order(getOrderType(), getTableName(), getCustomerName(),
				//						getCustomerHouseNumber(), getCustomerAddress(),
				//						getCustomerCity(), getCustomerPostcode(),
				//						getCustomerPhoneNumber,)
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 20;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelOrderSummarySouth.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelOrderSummarySouth.add(buttonResetOrder, gbc);

		gbc.gridx++;
		panelOrderSummarySouth.add(buttonPlaceOrder, gbc);


		return panelOrderSummarySouth;

	}



	public String getSelectedTableName() {
		System.out.println("SELECTED TABLE NAME " + tablesList.getSelectedValue().toString());
		return tablesList.getSelectedValue().toString();
	}

	//////// GET DELIVERY CUSTOMER DETAILS


	public String getDeliveryCustomerFirstName() { // All of these get methods could be solved with just one by passing the column number, however I think it is simpler to remember method names this way. 


		String customerFirstName;
		int row = deliveryCustomersTable.getSelectedRow();
		customerFirstName = deliveryCustomersTable.getModel().getValueAt(row, 1).toString();

		return customerFirstName;

	}

	public String getDeliveryCustomerLastName() {
		String customerLastName;
		int row = deliveryCustomersTable.getSelectedRow();
		customerLastName = deliveryCustomersTable.getModel().getValueAt(row, 2).toString();

		return customerLastName;
	}

	public String getDeliveryCustomerHouseNumber() {
		String customerHouseNumber;
		int row = deliveryCustomersTable.getSelectedRow();
		customerHouseNumber = deliveryCustomersTable.getModel().getValueAt(row, 3).toString();

		return customerHouseNumber;
	}

	public String getDeliveryCustomerAddress() {
		String customerAddress;
		int row = deliveryCustomersTable.getSelectedRow();
		customerAddress = deliveryCustomersTable.getModel().getValueAt(row, 4).toString();

		return customerAddress;
	}

	public String getDeliveryCustomerCity() {
		String customerCity;
		int row = deliveryCustomersTable.getSelectedRow();
		customerCity = deliveryCustomersTable.getModel().getValueAt(row, 5).toString();

		return customerCity;
	}

	public String getDeliveryCustomerPostcode() {
		String customerPostcode;
		int row = deliveryCustomersTable.getSelectedRow();
		customerPostcode = deliveryCustomersTable.getModel().getValueAt(row, 6).toString();

		return customerPostcode;
	}

	public String getDeliveryCustomerPhoneNumber() {
		String customerPhoneNumber;
		int row = deliveryCustomersTable.getSelectedRow();
		customerPhoneNumber = deliveryCustomersTable.getModel().getValueAt(row, 7).toString();

		return customerPhoneNumber;
	}

	public String getSelectedMenuItemID(JTable menuTable) {
		String menuItemID;

		int row = menuTable.getSelectedRow();
		menuItemID = menuTable.getModel().getValueAt(row, 0).toString();

		return menuItemID;
	}


	public String getSelectedMenuItem(JTable menuTable) {

		String menuItemName;

		int row = menuTable.getSelectedRow();
		menuItemName = menuTable.getModel().getValueAt(row, 1).toString();

		return menuItemName;
	}





	public String getSelectedMenuItemPrice(JTable menuTable) {
		String menuItemPrice;


		int row = menuTable.getSelectedRow();
		menuItemPrice = menuTable.getModel().getValueAt(row, 2).toString();

		return menuItemPrice;
	}

	public String getQuantity(JComboBox combobox) {


		System.out.println(combobox.getSelectedItem().toString());
		return combobox.getSelectedItem().toString();
	}

	public void setOrderDetailsOrderType(String orderType) {
		textFieldOrderType.setText(orderType);
	}

	public String getOrderDetailsOrderType() {
		return textFieldOrderType.getText();
	}

	public void setOrderDetailsTableName(String tableName	) {
		textFieldTableName.setText(tableName);
	}

	public String getOrderDetailsTableName() {
		return textFieldTableName.getText();
	}

	public void setOrderDetailsCustomerName(String customerName) {
		textFieldCustomerName.setText(customerName);
	}

	public void setOrderDetailsCustomerHouseNumber(String houseNumber) {
		textFieldCustomerHouseNumber.setText(houseNumber);
	}

	public void setOrderDetailsCustomerAddress(String address) {
		textFieldCustomerAddress.setText(address);
	}

	public void setOrderDetailsCustomerCity(String city) {
		textFieldCustomerCity.setText(city);
	}

	public void setOrderDetailsCustomerPostcode(String postcode) {
		textFieldCustomerPostcode.setText(postcode);
	}

	public void setOrderDetailsCustomerPhoneNumber(String phoneNumber) {
		textFieldCustomerPhoneNumber.setText(phoneNumber);
	}

	public void setTableNameBackgroundGray() {
		textFieldTableName.setBackground(Color.LIGHT_GRAY);
	}

	public void resetTableNameBackground() {
		textFieldTableName.setBackground(Color.WHITE);
	}

	public void setCustomerInfoTextFieldsBackgroundGray() { // Used when order type is TABLE.
		textFieldCustomerName.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerHouseNumber.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerAddress.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerCity.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerPostcode.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerPhoneNumber.setBackground(Color.LIGHT_GRAY);
	}

	public void resetCustomerInfoTextFieldsBackground() {
		textFieldCustomerName.setBackground(Color.WHITE);
		textFieldCustomerHouseNumber.setBackground(Color.WHITE);
		textFieldCustomerAddress.setBackground(Color.WHITE);
		textFieldCustomerCity.setBackground(Color.WHITE);
		textFieldCustomerPostcode.setBackground(Color.WHITE);
		textFieldCustomerPhoneNumber.setBackground(Color.WHITE);
	}

	public void grayTextFieldsForCollection() { // Used when order type is COLLECTION.
		textFieldTableName.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerHouseNumber.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerAddress.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerCity.setBackground(Color.LIGHT_GRAY);
		textFieldCustomerPostcode.setBackground(Color.LIGHT_GRAY);
	}

	public void setTotalPrice(Double totalPrice) {
		textFieldTotalPrice.setText(totalPrice.toString());
	}

	public void resetOrder() {
		orderHM = new HashMap();
		resetTableNameBackground();
		resetCustomerInfoTextFieldsBackground();

		setOrderDetailsOrderType("");
		setOrderDetailsTableName("");
		setOrderDetailsCustomerName("");
		setOrderDetailsCustomerHouseNumber("");
		setOrderDetailsCustomerAddress("");
		setOrderDetailsCustomerCity("");
		setOrderDetailsCustomerPostcode("");
		setOrderDetailsCustomerPhoneNumber("");
		setTotalPrice(0.00);

	}

	public JPanel getOrdersPanel() {
		return panelNewOrdersMain;
	}



}
