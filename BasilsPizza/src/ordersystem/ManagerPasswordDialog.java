package ordersystem;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class ManagerPasswordDialog {

	private JFrame frame;
	private final static String managerPassword = "pizza";
	private String employeeNumber, lastName, jobTitle;
	private JTextField textFieldPassword;
	private boolean addFlag = false;
	//private boolean editFlag = false;
	//private boolean deleteFlag = false;

	public ManagerPasswordDialog(JFrame frame) {
		this.frame = frame;
		//initDialog();
	}


	public ManagerPasswordDialog(JFrame frame, ArrayList<String> selectedCellValues) {
		this.frame = frame;
		employeeNumber = selectedCellValues.get(0);
		selectedCellValues.get(1);
		lastName = selectedCellValues.get(2);
		jobTitle = selectedCellValues.get(3);

		initDialog();
	}

	private void initDialog() {
		JDialog dialogManagerPassword = new JDialog();
		JPanel panelMain = new JPanel(new BorderLayout());
		JPanel panelForm = new JPanel(new GridBagLayout());
		JPanel panelButtons = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		// Action Listener for OK button and ENTER keyboard input.
		@SuppressWarnings("serial")
		Action enterPasswordAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getTextFieldPasswordValue().equals(managerPassword)) {
					System.out.println("PASSWORD CORRECT");
					if (addFlag) {
						new AddStaffDialogGUI(frame);
						dialogManagerPassword.dispose();
					} else {
						Database.deleteStaff(employeeNumber);
						dialogManagerPassword.dispose();
					}

				} else {
					JOptionPane.showMessageDialog(frame, "Incorrect password.",
							"Access Denied", JOptionPane.ERROR_MESSAGE);
				}

			}

		};

		JLabel labelPassword = new JLabel("Password: ");
		textFieldPassword = new JTextField(15);
		textFieldPassword.addActionListener(enterPasswordAction);

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogManagerPassword.dispose();
			}
		});




		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(enterPasswordAction);





		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		panelForm.add(labelPassword, gbc);

		// SPACING
		gbc.gridy = 20;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panelForm.add(new JLabel(), gbc);



		// TEXTFIELD
		gbc.gridx++;
		gbc.gridy = 0;

		gbc.anchor = GridBagConstraints.LINE_START;
		panelForm.add(textFieldPassword, gbc);

		// BUTTONS

		GridBagConstraints gbcBtn = new GridBagConstraints();

		gbcBtn.gridx = 20;
		gbcBtn.gridy = 0;
		gbcBtn.weightx = 1.0;
		gbcBtn.weighty = 1.0;
		panelButtons.add(new JLabel(), gbcBtn);

		gbcBtn.gridx++;
		gbcBtn.weightx = 0;
		gbcBtn.weighty = 0;
		panelButtons.add(cancelBtn, gbcBtn);

		gbcBtn.gridx++;
		panelButtons.add(okBtn, gbcBtn);

		panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		TitledBorder border = new TitledBorder("Manager Access Only:");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		panelMain.setBorder(border);

		panelMain.add(panelForm, BorderLayout.CENTER);
		panelMain.add(panelButtons, BorderLayout.SOUTH);

		dialogManagerPassword.add(panelMain);
		dialogManagerPassword.setModal(true); 
		dialogManagerPassword.pack();
		dialogManagerPassword.setLocationRelativeTo(frame);
		dialogManagerPassword.setVisible(true);
	}

	private String getTextFieldPasswordValue() {
		return textFieldPassword.getText();
	}

	public void addStaff() {
		addFlag = true;
		initDialog();

	}

}
