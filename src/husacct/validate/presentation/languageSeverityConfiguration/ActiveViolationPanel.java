package husacct.validate.presentation.languageSeverityConfiguration;

import husacct.validate.abstraction.language.ValidateTranslator;
import husacct.validate.domain.configuration.ActiveRuleType;
import husacct.validate.domain.configuration.ActiveViolationType;
import husacct.validate.domain.validation.ruletype.RuleType;
import husacct.validate.task.TaskServiceImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;

public class ActiveViolationPanel extends javax.swing.JPanel {
	
	private static Logger logger = Logger.getLogger(ActiveViolationPanel.class);
	
	private final DefaultListModel categoryModel;
	private final DefaultListModel ruletypeModel;
	private final String language;
	private final TaskServiceImpl taskServiceImpl;
	private final HashMap<String, List<RuleType>> ruletypes;
	private final List<ActiveRuleType> activeRuletypes;
	private List<ActiveViolationType> activeViolationtypes;
	
	private DefaultTableModel violationtypeModel;
	
	private JButton apply, deselectAll, selectAll;
	private JList categoryJList, ruletypeJList;
	private JScrollPane categoryScrollpane, ruletypeScrollpane,
			violationtypeScrollpane;
	private JTable violationtypeTable;

	public ActiveViolationPanel(TaskServiceImpl taskServiceImpl, HashMap<String, List<RuleType>> ruletypes, String language) {
		
		categoryModel = new DefaultListModel();
		ruletypeModel = new DefaultListModel();
		
		this.taskServiceImpl = taskServiceImpl;
		this.ruletypes = ruletypes;
		this.language = language;
		activeRuletypes = taskServiceImpl.getActiveViolationTypes().get(language);
		
		initComponents();
		loadAfterChange();
	}

    private void initComponents() {

        categoryScrollpane = new JScrollPane();
		categoryJList = new JList();
		ruletypeScrollpane = new JScrollPane();
		violationtypeTable = new JTable();
		violationtypeScrollpane = new JScrollPane();
		ruletypeJList = new JList();
		selectAll = new JButton();
		deselectAll = new JButton();
		apply = new JButton();

		categoryJList.setModel(categoryModel);
		categoryJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoryJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(categoryJList.getSelectedIndex() > -1){
					CategoryValueChanged();
				}
			}
		});
		categoryScrollpane.setViewportView(categoryJList);
		
		violationtypeTable.getTableHeader().setReorderingAllowed(false);
		violationtypeTable.setFillsViewportHeight(true);
		violationtypeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ruletypeScrollpane.setViewportView(violationtypeTable);
		
		ruletypeJList.setModel(ruletypeModel);
		
		ruletypeJList.setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		ruletypeJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(ruletypeJList.getSelectedIndex() > -1){
					RuletypeValueChanged();
				}
			}
		});
		violationtypeScrollpane.setViewportView(ruletypeJList);
		
		selectAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				SelectAllActionPerformed();
			}
		});
		
		deselectAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				DeselectAllActionPerformed();
			}
		});
		
		apply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				ApplyActionPerformed();
			}
		});

		GroupLayout activeViolationtypeLayout = new GroupLayout(
				this);
		activeViolationtypeLayout.setHorizontalGroup(
			activeViolationtypeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(activeViolationtypeLayout.createSequentialGroup()
					.addComponent(categoryScrollpane)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(violationtypeScrollpane)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(ruletypeScrollpane)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(activeViolationtypeLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
						.addComponent(selectAll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(deselectAll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(apply, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					)
				)
		);
		activeViolationtypeLayout.setVerticalGroup(
			activeViolationtypeLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(ruletypeScrollpane)
				.addGroup(GroupLayout.Alignment.LEADING, activeViolationtypeLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(selectAll)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(deselectAll)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(apply)
					.addContainerGap())
				.addComponent(categoryScrollpane)
				.addComponent(violationtypeScrollpane)
		);
		
		this.setLayout(activeViolationtypeLayout);
    }
	
	public final void loadAfterChange(){
		setText();
		loadModels();
		loadRuleTypeCategories();
	}
	
	private void setText(){
		categoryJList.setBorder(BorderFactory.createTitledBorder(ValidateTranslator.getValue("Category")));
		ruletypeJList.setBorder(BorderFactory.createTitledBorder(ValidateTranslator.getValue("Ruletypes")));
		selectAll.setText(ValidateTranslator.getValue("SelectAll"));
		deselectAll.setText(ValidateTranslator.getValue("DeselectAll"));
		apply.setText(ValidateTranslator.getValue("Apply"));
	}
	
	private void loadModels(){
		
		String[] ViolationtypeModelHeaders = {ValidateTranslator.getValue("Violationtype"), ValidateTranslator.getValue("Active")};
		violationtypeModel = new DefaultTableModel(ViolationtypeModelHeaders, 0){

			Class<?>[] types = new Class[]{String.class, Boolean.class};
			boolean[] canEdit = new boolean[]{false, true};

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};
		
		
		violationtypeTable.setModel(violationtypeModel);
	}
	
	private void loadRuleTypeCategories() {
		categoryModel.clear();
		for (String categoryString : ruletypes.keySet()) {
			categoryModel.addElement(ValidateTranslator.getValue(categoryString));
		}
	}
	
	private void SelectAllActionPerformed() {
		for(int i = 0; i < violationtypeModel.getRowCount(); i++){
			violationtypeModel.setValueAt(true, i, 1);
		}
	}
	
	private void DeselectAllActionPerformed() {
		for(int i = 0; i < violationtypeModel.getRowCount(); i++){
			violationtypeModel.setValueAt(false, i, 1);
		}
	}

	private void ApplyActionPerformed() {
		ActiveRuleType activeRuletype = activeRuletypes.get(ruletypeJList.getSelectedIndex());
		for(int i = 0; i < violationtypeModel.getRowCount(); i++){
			boolean test = (Boolean) violationtypeModel.getValueAt(i, 1);
			try{
				activeViolationtypes.get(i).setEnabled(test);
			} catch(IndexOutOfBoundsException outOfBoundsException) {
				logger.error("Something went wrong. Active violationtypes can not be set.");
			}
		}
		activeRuletype.setViolationTypes(activeViolationtypes);
		
		activeRuletypes.remove(ruletypeJList.getSelectedIndex());
		activeRuletypes.add(ruletypeJList.getSelectedIndex(), activeRuletype);

		taskServiceImpl.setActiveViolationTypes(language, activeRuletypes);
	}

	private void CategoryValueChanged() {
		LoadRuletypes((String) categoryJList.getSelectedValue());
	}

	private void LoadRuletypes(String category) {
		ruletypeModel.clear();

		List<RuleType> rules = ruletypes.get(ValidateTranslator.getKey(category));
		for(RuleType ruletype: rules){
			ruletypeModel.addElement(ValidateTranslator.getValue(ruletype.getKey()));
		}
	}
	
	private void RuletypeValueChanged() {
		LoadViolationtypes((String) ruletypeJList.getSelectedValue());
	}

	private void LoadViolationtypes(String ruletypekey) {
		while(violationtypeModel.getRowCount() > 0){
			violationtypeModel.removeRow(0);
		}

		for (ActiveRuleType ruletypeKey : activeRuletypes) {
			if(ruletypeKey.getRuleType().equals(ValidateTranslator.getKey(ruletypekey))){
				for(ActiveViolationType violationtype : ruletypeKey.getViolationTypes()){
					violationtypeModel.addRow(new Object[]{ValidateTranslator.getValue(violationtype.getType()), violationtype.isEnabled()});
				}
				activeViolationtypes = ruletypeKey.getViolationTypes();
			}
		}
	}
}
