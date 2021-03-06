package org.apache.axis2.tools.idea;

import org.apache.axis2.tools.bean.CodegenBean;
import org.apache.axis2.util.URLProcessor;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.border.BevelBorder;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

import com.intellij.openapi.module.Module;
/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
*/

public class SecondFrame extends JPanel implements ActionListener {

    JLabel lblLangauge;
    JComboBox cmbLan;

    JLabel lblServiceName;
    JComboBox cmbServiceName;

    JLabel lblportName;
    JComboBox cmbPortName;

    JLabel lblpackgeName;
    JTextField txtPacakgeName;

    JLabel lbldbtype;
    JComboBox cmbdbtype;

    JCheckBox chkTestCase;

    JRadioButton clientSide;


    JRadioButton rdBoth;
    JRadioButton rdsyn;
    JRadioButton rdasync;

    JRadioButton serverSide;
    JCheckBox serviceXML;

    JCheckBox serverSideInterface;

    JRadioButton all;

    ButtonGroup buttonGroup;

    ButtonGroup generationType;

    PackageNameTableModel model;

    JLabel lblPackageMapping;

    JTable table;

    CodegenBean codegenBean;
    java.util.List serviceNameList;

    public SecondFrame()  {
        SecondFrameLayout customLayout = new SecondFrameLayout();

        setFont(new Font("Helvetica", Font.PLAIN, 12));
        setLayout(customLayout);

       

        lblLangauge = new JLabel("Select the output language");
        add(lblLangauge);

        cmbLan = new JComboBox();
        cmbLan.addItem("java");
        cmbLan.addItem("C#");
        cmbLan.setToolTipText("Select the language of the generated code");
        add(cmbLan);

        lblServiceName = new JLabel("Select Service Name");
        add(lblServiceName);

        cmbServiceName = new JComboBox();
        add(cmbServiceName);
        cmbServiceName.setToolTipText("Select the name of the service that the code should be generated for");
        cmbServiceName.addActionListener(this);

        lblportName = new JLabel("Select Port Name");
        add(lblportName);

        cmbPortName = new JComboBox();
        cmbPortName.setToolTipText("Select the port name that the code should be generated for");
        add(cmbPortName);

        lblpackgeName = new JLabel("Select the package name");
        add(lblpackgeName);

        txtPacakgeName = new JTextField("org.axis2");
        txtPacakgeName.setToolTipText("Set the package name of the generated code");
        add(txtPacakgeName);

        lbldbtype = new JLabel("Select Databinding type");
        add(lbldbtype);

        cmbdbtype = new JComboBox();
        cmbdbtype.addItem("adb");
        cmbdbtype.addItem("xmlbeans");
        cmbdbtype.addItem("none");
        cmbdbtype.setToolTipText("Select the databinding framework to be used in the generation process");
        add(cmbdbtype);

        chkTestCase = new JCheckBox("Generate Test Case", true);
        chkTestCase.setVisible(false);
        chkTestCase.setToolTipText("A test case will be generated if this is checked");
        add(chkTestCase);

        generationType = new ButtonGroup();

        JSeparator clintSep = new JSeparator(JSeparator.HORIZONTAL);
        add(clintSep);

        clientSide = new JRadioButton("Generate Client Side",true);
        generationType.add(clientSide);
        clientSide.addActionListener(this);
        add(clientSide);

        buttonGroup = new ButtonGroup();

        rdBoth = new JRadioButton("Generate both sync and async", true);
        buttonGroup.add(rdBoth);
        add(rdBoth);

        rdsyn = new JRadioButton("Generate sync only", false);
        buttonGroup.add(rdsyn);
        add(rdsyn);

        rdasync = new JRadioButton("Generate async only", false);
        buttonGroup.add(rdasync);
        add(rdasync);

        JSeparator serverSep = new JSeparator(JSeparator.HORIZONTAL);
        add(serverSep);

        serverSide = new JRadioButton("Generate Server Side");
        generationType.add(serverSide);
        serverSide.addActionListener(this);
        add(serverSide);

        serviceXML = new JCheckBox("Generate default service.xml", true);
        serviceXML.setEnabled(false);
        add(serviceXML);

        serverSideInterface = new JCheckBox("Generate an interface for skeleton", true);
        serverSideInterface.setEnabled(false);
        add(serverSideInterface);

        JSeparator allSep = new JSeparator(JSeparator.HORIZONTAL);
        add(allSep);

        all = new JRadioButton("Generate All");
        generationType.add(all);
        all.addActionListener(this);
        add(all);

        JSeparator packageSep = new JSeparator(JSeparator.HORIZONTAL);
        add(packageSep);

        lblPackageMapping = new JLabel("Namespace to Package Mapping");
        add(lblPackageMapping);

        model = new PackageNameTableModel(new Object [1][2]);
        table = new JTable(model);
        add(new JScrollPane(table));

        Dimension dim = new Dimension(450, 600);
        setSize(dim);
    }

    public void setCodeGenBean(CodegenBean codegenBean) {
        this.codegenBean = codegenBean;
        setStatus();

    }

    public void setStatus(){
        loadNamespaces(codegenBean.getDefinitionNamespaceMap());
        table.updateUI();
        txtPacakgeName.setText(codegenBean.packageFromTargetNamespace());
        cmbServiceName.removeAllItems();
        serviceNameList = codegenBean.getServiceList();
        for (int i = 0; i < serviceNameList.size(); i++) {
            QName name = (QName) serviceNameList.get(i);
            cmbServiceName.addItem(name.getLocalPart());
        }
        cmbServiceName.setSelectedIndex(0);
    }

    /**
	 * Loads the namespaces
	 * @param namespaceMap
	 */
	private void loadNamespaces(Collection namespaceMap){
		Iterator namespaces = namespaceMap.iterator();
        Object tableData [][] = new Object[namespaceMap.size()][2];
        int i = 0;
        while(namespaces.hasNext()){

           String namespace = (String)namespaces.next();
           tableData[i][0] = namespace;
           tableData[i][1] = getPackageFromNamespace(namespace);
           i++;
        }

        model.setTableData(tableData);


    }

    /**
	 * get the package derived by  Namespace
	 */
	public String getPackageFromNamespace(String namespace){
		return  URLProcessor.makePackageName(namespace);
	}

    public void fillBean() {
        int index = cmbLan.getSelectedIndex();
        switch (index) {
            case 0: {
                codegenBean.setLanguage("java");
                break;
            }
            case 1: {
                codegenBean.setLanguage("c-sharp");
                break;
            }
        }

        index = cmbdbtype.getSelectedIndex();
        switch (index) {
            case 0: {
                codegenBean.setDatabindingName("adb");
                break;
            }
            case 1: {
                codegenBean.setDatabindingName("xmlbeans");
                break;
            }
            case 2: {
                codegenBean.setDatabindingName("none");
                break;
            }
        }

        if (clientSide.isSelected()){

        if (rdasync.isSelected()) {
            codegenBean.setAsyncOnly(true);
        }
        else if (rdsyn.isSelected()) {
            codegenBean.setSyncOnly(true);
        }
        }
        else if (serverSide.isSelected()) {

            if (serviceXML.isSelected())
                codegenBean.setServerSide(true);
            else
                codegenBean.setServerSide(false);
            if (serverSideInterface.isSelected())
                codegenBean.setServerSideInterface(true);
            else
               codegenBean.setServerSideInterface(false);
        }
        else {
        codegenBean.setGenerateAll(true);


}
        if (chkTestCase.isSelected()) {
            codegenBean.setTestCase(true);
        }
        else
        codegenBean.setTestCase(false);
        codegenBean.setPackageName(txtPacakgeName.getText());
        codegenBean.setServiceName(cmbServiceName.getSelectedItem().toString());
        codegenBean.setServiceName(cmbPortName.getSelectedItem().toString());
        codegenBean.setNamespace2packageList(getNs2PkgMapping());
    }

    /**
	 * get the package to namespace mappings
	 * @return
	 */
	public String getNs2PkgMapping(){
		String returnList="";
		String packageValue;
		for (int i=0;i<table.getRowCount();i++){
			packageValue = (String)table.getValueAt(i,1);
				returnList = returnList +
				             ("".equals(returnList)?"":",") +
				             (String)table.getValueAt(i,0)+ "=" + packageValue;


		}
		return "".equals(returnList)?null:returnList;
	}

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == cmbServiceName) {
            int selindex = cmbServiceName.getSelectedIndex();
            if (selindex>=0)
            {
            java.util.List ports = codegenBean.getPortNameList((QName) serviceNameList.get(selindex));
            cmbPortName.removeAllItems();
            for (int i = 0; i < ports.size(); i++) {
                String portName = (String) ports.get(i);
                cmbPortName.addItem(portName);
            }
            }
        }
        else if (obj == serverSide){
            if(clientSide.isSelected()){
                rdasync.setEnabled(true);
                rdBoth.setEnabled(true);
                rdsyn.setEnabled(true);
                chkTestCase.setEnabled(true);
                serviceXML.setEnabled(false);
                serverSideInterface.setEnabled(false);
            }
            else
            {
               rdasync.setEnabled(false);
                rdBoth.setEnabled(false);
                rdsyn.setEnabled(false);
                chkTestCase.setEnabled(false);
                serviceXML.setEnabled(true);
                serverSideInterface.setEnabled(true);
            }
        }
        else if (obj == clientSide){
            if(serverSide.isSelected()){
                rdasync.setEnabled(false);
                rdBoth.setEnabled(false);
                rdsyn.setEnabled(false);
                serviceXML.setEnabled(true);
                chkTestCase.setEnabled(false);
                serverSideInterface.setEnabled(true);
            }
            else
            {
                rdasync.setEnabled(true);
                rdBoth.setEnabled(true);
                rdsyn.setEnabled(true);
                chkTestCase.setEnabled(true);
                serviceXML.setEnabled(false);
                serverSideInterface.setEnabled(false);
            }
        }
         else if (obj == all){
            if(all.isSelected()){
                rdasync.setEnabled(false);
                rdBoth.setEnabled(false);
                rdsyn.setEnabled(false);
                serviceXML.setEnabled(false);
                serverSideInterface.setEnabled(false);
                chkTestCase.setEnabled(true);
            }


        }
    }
}

class SecondFrameLayout implements LayoutManager {

    public SecondFrameLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        Insets insets = parent.getInsets();
        dim.width = 575 + insets.left + insets.right;
        dim.height = 600 + insets.top + insets.bottom;

        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        Component c;

        // Language selection
        c = parent.getComponent(0);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 10, 192, 24);
        }
        c = parent.getComponent(1);
        if (c.isVisible()) {
            c.setBounds(insets.left + 272, insets.top + 10, 250, 24);
        }

        // Service Name selection
        c = parent.getComponent(2);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 40, 192, 24);
        }
        c = parent.getComponent(3);
        if (c.isVisible()) {
            c.setBounds(insets.left + 272, insets.top + 40, 250, 24);
        }

        // Port Name Selection
        c = parent.getComponent(4);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 70, 192, 24);
        }
        c = parent.getComponent(5);
        if (c.isVisible()) {
            c.setBounds(insets.left + 272, insets.top + 70, 250, 24);
        }

        // Data Binding Selection
        c = parent.getComponent(6);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 130, 192, 24);
        }
        c = parent.getComponent(7);
        if (c.isVisible()) {
            c.setBounds(insets.left + 272, insets.top + 130, 250, 24);
        }

        //Package NAme Selection
        c = parent.getComponent(8);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 100, 192, 24);
        }
        c = parent.getComponent(9);
        if (c.isVisible()) {
            c.setBounds(insets.left + 272, insets.top + 100, 250, 24);
        }



        // Test Case Selection
        c = parent.getComponent(10);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 160, 208, 24);
        }

        // JSeperator

        c = parent.getComponent(11);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 187, 530, 2);
        }

        // Client side options
        c = parent.getComponent(12);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 190, 168, 24);
        }

        // Service invocation both,sync,async
        c = parent.getComponent(13);
        if (c.isVisible()) {
            c.setBounds(insets.left + 48, insets.top + 220, 210, 24);
        }
        c = parent.getComponent(14);
        if (c.isVisible()) {
            c.setBounds(insets.left + 260, insets.top + 220, 140, 24);
        }
        c = parent.getComponent(15);
        if (c.isVisible()) {
            c.setBounds(insets.left + 400, insets.top + 220, 145, 24);
        }

        // JSeperator

        c = parent.getComponent(16);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 247, 530, 2);
        }

        // Server side options
        c = parent.getComponent(17);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 250, 168, 24);
        }

        // Generate serverside, generate service XML
        c = parent.getComponent(18);
        if (c.isVisible()) {
            c.setBounds(insets.left + 48, insets.top + 280, 200, 24);
        }

        c = parent.getComponent(19);
        if (c.isVisible()) {
            c.setBounds(insets.left + 248, insets.top + 280, 250, 24);
        }

        // JSeperator

        c = parent.getComponent(20);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 307, 530, 2);
        }

        c = parent.getComponent(21);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 310, 200, 24);
        }

        // JSeperator

        c = parent.getComponent(22);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 337, 530, 2);
        }

        c = parent.getComponent(23);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 340, 250, 24);
        }

        c = parent.getComponent(24);
        if (c.isVisible()) {
            c.setBounds(insets.left + 8, insets.top + 370, 522, 85);
        }


    }
}

