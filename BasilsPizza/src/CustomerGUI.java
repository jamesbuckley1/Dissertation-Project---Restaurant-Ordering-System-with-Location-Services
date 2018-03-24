import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class CustomerGUI {

	private static DefaultTableModel customersTableModel;
	private static JTable customersTable;
	private static GridBagConstraints gbc;

	private JFrame frame;

	private JTextField textFieldCustomerFirstName;
	private JTextField textFieldCustomerLastName;
	private JTextField textFieldCustomerHouseNumber;
	private JTextField textFieldCustomerAddress;
	private JTextField textFieldCustomerCity;
	private JTextField textFieldCustomerPostcode;
	private JTextField textFieldCustomerPhoneNumber;

	private JPanel panelCustomersMain;
	private JPanel panelCustomersMap;
	private JPanel panelCustomersMapButtons;
	private JPanel panelCustomersMapZoomButtons;

	private JLabel labelMapStatus;

	public CustomerGUI() {
		initGUI();
	}

	private void initGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
		frame = (JFrame)SwingUtilities.getRoot(panelCustomersMain);
		panelCustomersMain = new JPanel(new BorderLayout());
		JPanel panelCustomersMainGrid = new JPanel(new GridLayout(1, 2));
		JPanel panelCustomersFormMapGrid = new JPanel(new GridLayout(2, 1));
		JPanel panelCustomersTable = new JPanel(new BorderLayout());
		JPanel panelCustomersTableButtons = new JPanel(new GridBagLayout()); //
		JPanel panelCustomersFormBorder = new JPanel(new BorderLayout());
		JPanel panelCustomersForm = new JPanel(new GridBagLayout());
		JPanel panelCustomersFormButtons = new JPanel(new FlowLayout());
		panelCustomersMap = new JPanel(new BorderLayout());
		panelCustomersMapButtons = new JPanel(new GridBagLayout());
		panelCustomersMapZoomButtons = new JPanel(new GridBagLayout());

		customersTableModel = new DefaultTableModel(new String[] {
				"First Name", "Last Name", "House Number", "Address",
				"City", "Postcode", "Phone Number"
		}, 0);

		customersTable = new JTable(customersTableModel ) {

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Component prepareRenderer(TableCellRenderer r, int row, int col) {
				Component c = super.prepareRenderer(r, row, col);

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

		customersTable.setFont(new Font("", 0, 14));
		customersTable.setRowHeight(customersTable.getRowHeight() + 10);
		customersTable.setAutoCreateRowSorter(true);

		customersTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					//editCustomer(frame?)
				}

				try {
					int row = customersTable.getSelectedRow();
					String houseNumber = customersTable.getModel().getValueAt(row, 2).toString();
					String address = customersTable.getModel().getValueAt(row, 3).toString();
					String city = customersTable.getModel().getValueAt(row, 4).toString();
					populateMap(houseNumber, address, city);
				} catch (Exception e) {
					/*
        				JOptionPane.showMessageDialog(frame, "Please select an item to delete.",
        						"Error", JOptionPane.ERROR_MESSAGE);
					 */
				}
			}
		});

		populateCustomersTable();

		customersTable.setFillsViewportHeight(true);

		JScrollPane jsp = new JScrollPane(customersTable);

		JButton addCustomerBtn = new JButton();
		addCustomerBtn.setText("Add"); //Is this line needed?
		addCustomerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Customer c = new Customer(textFieldCustomerFirstName.getText(),
						textFieldCustomerLastName.getText(),
						textFieldCustomerHouseNumber.getText(),
						textFieldCustomerAddress.getText(),
						textFieldCustomerCity.getText(),
						textFieldCustomerPostcode.getText(),
						textFieldCustomerPhoneNumber.getText());
				try {
						if (!c.validateFirstName()) {
							JOptionPane.showMessageDialog(frame, "Invalid first name.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else if (!c.validateLastName()) {
							JOptionPane.showMessageDialog(frame, "Invalid last name.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else if (!c.validateHouseNumber()) {
							JOptionPane.showMessageDialog(frame, "Invalid house number.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else if (!c.validateAddress()) {
							JOptionPane.showMessageDialog(frame, "Invalid address.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else if (!c.validateCity()) {
							JOptionPane.showMessageDialog(frame, "Invalid city.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else if (!c.validatePostcode()) {
							JOptionPane.showMessageDialog(frame, "Invalid postcode.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else if (!c.validatePhoneNumber()) {
							JOptionPane.showMessageDialog(frame, "Invalid phone number.",
									"Invalid Entry", JOptionPane.ERROR_MESSAGE);
						} else {
							c.addCustomerToDatabase();
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, "Error",
								"Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					populateCustomersTable();
				}
			});

		JButton editCustomerBtn = new JButton();
		editCustomerBtn.setText("Edit");
		editCustomerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//editCustomer(frame?);
			}
		});

		JButton deleteCustomerBtn = new JButton();
		deleteCustomerBtn.setText("Delete");
		deleteCustomerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//do stuff
			}
		});

		JButton mapExpandBtn = new JButton();
		mapExpandBtn.setText("Expand");
		mapExpandBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//do stuff
			}
		});

		JButton mapZoomInBtn = new JButton();
		mapZoomInBtn.setText("+");
		mapZoomInBtn.setPreferredSize(new Dimension(40,40));
		mapZoomInBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
			}
		});

		JButton mapZoomOutBtn = new JButton("-");
		mapZoomOutBtn.setPreferredSize(new Dimension(40,40));
		mapZoomOutBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//do shit
			}
		});

		// TABLE BUTTONS
		gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelCustomersTableButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCustomersTableButtons.add(editCustomerBtn, gbc);

		gbc.gridx++;
		gbc.gridy = 0;
		panelCustomersTableButtons.add(deleteCustomerBtn, gbc);

		// CUSTOMER FORM
		JLabel lblCustomerFormTitle = new JLabel("Add new customer: ");
		JLabel lblCustomerFirstName = new JLabel("First name: ");
		JLabel lblCustomerLastName = new JLabel("Last name: ");
		JLabel lblCustomerHouseNumber = new JLabel("House number: ");
		JLabel lblCustomerAddress = new JLabel("Address: ");
		JLabel lblCustomerCity = new JLabel("City: ");
		JLabel lblCustomerPostcode = new JLabel("Postcode: ");
		JLabel lblCustomerPhoneNumber = new JLabel("Phone number: ");

		textFieldCustomerFirstName = new JTextField(20);
		textFieldCustomerLastName = new JTextField(20);
		textFieldCustomerHouseNumber = new JTextField(5);
		textFieldCustomerAddress = new JTextField(20);
		textFieldCustomerCity = new JTextField(20);
		textFieldCustomerPostcode = new JTextField(10);
		textFieldCustomerPhoneNumber = new JTextField(15);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5,10,0,0); //TOP, LEFT 
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCustomersForm.add(lblCustomerFormTitle, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(20,10,0,0); //TOP, LEFT 
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCustomersForm.add(lblCustomerFirstName, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(5,10,0,0); //TOP, LEFT 
		panelCustomersForm.add(lblCustomerLastName, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panelCustomersForm.add(lblCustomerHouseNumber, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panelCustomersForm.add(lblCustomerAddress, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panelCustomersForm.add(lblCustomerCity, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panelCustomersForm.add(lblCustomerPostcode, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panelCustomersForm.add(lblCustomerPhoneNumber, gbc);

		// TEXT FIELDS
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(20,10,0,0); //TOP, LEFT 
		panelCustomersForm.add(textFieldCustomerFirstName, gbc);

		gbc.gridx = 1;
		gbc.gridy++;
		gbc.insets = new Insets(5,10,0,0); //TOP, LEFT 
		panelCustomersForm.add(textFieldCustomerLastName, gbc);

		gbc.gridx = 1;
		gbc.gridy++;
		panelCustomersForm.add(textFieldCustomerHouseNumber, gbc);

		gbc.gridx = 1;
		gbc.gridy++;
		panelCustomersForm.add(textFieldCustomerAddress, gbc);

		gbc.gridx = 1;
		gbc.gridy++;
		panelCustomersForm.add(textFieldCustomerCity, gbc);

		gbc.gridx = 1;
		gbc.gridy++;
		panelCustomersForm.add(textFieldCustomerPostcode, gbc);

		gbc.gridx = 1;
		gbc.gridy++;
		panelCustomersForm.add(textFieldCustomerPhoneNumber, gbc);

		// FORM BUTTON - ADD
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCustomersForm.add(addCustomerBtn, gbc);

		//TRICK
		gbc.gridx = 0;
		gbc.gridy = 20;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelCustomersForm.add(new JLabel(), gbc);

		// MAP BUTTONS
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCustomersMapButtons.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.gridy = 0;
		panelCustomersMapButtons.add(mapExpandBtn, gbc);

		// MAP ZOOM BUTTONS
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		panelCustomersMapZoomButtons.add(new JLabel(), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		panelCustomersMapZoomButtons.add(mapZoomInBtn, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panelCustomersMapZoomButtons.add(mapZoomOutBtn, gbc);

		panelCustomersTable.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panelCustomersTable.add(jsp, BorderLayout.CENTER);
		panelCustomersTable.add(panelCustomersTableButtons, BorderLayout.SOUTH);

		panelCustomersMainGrid.add(panelCustomersTable);
		panelCustomersMainGrid.add(panelCustomersFormMapGrid);

		panelCustomersFormBorder.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelCustomersFormBorder.add(panelCustomersForm, BorderLayout.CENTER);
		panelCustomersFormBorder.add(panelCustomersFormButtons, BorderLayout.SOUTH);

		panelCustomersFormMapGrid.add(panelCustomersFormBorder);

		labelMapStatus = new JLabel("Select a customer to see map."); // Get this to change while downloading image?
		panelCustomersMap.add(labelMapStatus, BorderLayout.NORTH);
		panelCustomersMap.add(panelCustomersMapZoomButtons, BorderLayout.EAST);
		panelCustomersMap.add(panelCustomersMapButtons, BorderLayout.SOUTH);

		panelCustomersFormMapGrid.add(panelCustomersMap);

		panelCustomersMain.add(panelCustomersMainGrid);

		}

		private static void populateCustomersTable() {
			int rows = customersTableModel.getRowCount();
			for (int i = rows - 1; i >= 0; i --) {
				customersTableModel.removeRow(i);
			}

			Database.selectCustomers();

			for (int i = 0; i < Database.getCustomersArray().size(); i++) {


				String firstName = Database.getCustomersArray().get(i).getFirstName();
				String lastName = Database.getCustomersArray().get(i).getLastName(); 
				String houseNumber = Database.getCustomersArray().get(i).getHouseNumber();
				String address = Database.getCustomersArray().get(i).getAddress();
				String city = Database.getCustomersArray().get(i).getCity();
				String postcode = Database.getCustomersArray().get(i).getPostcode();
				String phoneNumber = Database.getCustomersArray().get(i).getPhoneNumber();

				System.out.println("POPULATECUSTMERTABLE");
				System.out.println(firstName);
				System.out.println(lastName);
				System.out.println(houseNumber);
				System.out.println(address);
				System.out.println(city);
				System.out.println(postcode);
				System.out.println(phoneNumber);


				Object[] data = {firstName, lastName, houseNumber, address, city,
						postcode, phoneNumber
				};



				customersTableModel.addRow(data);


			}
		}

		private void populateMap(String houseNumber, String address, String city) {

			CustomerMap cm = new CustomerMap(houseNumber, address, city);
			panelCustomersMap.removeAll();


			panelCustomersMap.add(cm.getImage(), BorderLayout.CENTER);
			panelCustomersMap.add(panelCustomersMapZoomButtons, BorderLayout.EAST);
			panelCustomersMap.add(panelCustomersMapButtons, BorderLayout.SOUTH);

			panelCustomersMap.validate();
			panelCustomersMap.repaint();


		}

		private ArrayList<String> getCustomerTextFieldValues() {
			ArrayList<String> customerTextFieldsArray = new ArrayList<String>();

			customerTextFieldsArray.add(textFieldCustomerFirstName.getText());
			customerTextFieldsArray.add(textFieldCustomerLastName.getText());
			customerTextFieldsArray.add(textFieldCustomerHouseNumber.getText());
			customerTextFieldsArray.add(textFieldCustomerAddress.getText());
			customerTextFieldsArray.add(textFieldCustomerCity.getText());
			customerTextFieldsArray.add(textFieldCustomerPostcode.getText());
			customerTextFieldsArray.add(textFieldCustomerPhoneNumber.getText());

			return customerTextFieldsArray;
		}

		public JPanel getCustomerPanel() {
			return panelCustomersMain;
		}

	}
