/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacionfacturas;

import java.awt.Color;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author manrique
 */
public class ClientesWindow extends javax.swing.JFrame {

    protected Connection connection;
    protected ResultSet resultSet;
    protected Statement statement;
    protected JFrame loginWindow;
    
    //for clientes
    protected int codigo;
    protected String dni,nombre, fechNac;
    protected Date checkDate;
    SimpleDateFormat dateFormater;
    /**
     * Creates new form ClientesWindow
     */
    public ClientesWindow(Connection connectionE, JFrame ventanaLoginE) {
        initComponents();
        loginWindow = ventanaLoginE;        //get the ventanaLogin again
        loginWindow.setVisible(false);      //close the main window
        connection = connectionE;           //get the connection
        dateFormater = new SimpleDateFormat("dd/MM/yyyy");  //check date
        try {
            getClientes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error en la busqueda de clientes");
            Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }//end constructor
    
    public void getClientes() throws SQLException {
        statement = connection.createStatement();   //get the statement
        resultSet = statement.executeQuery("SELECT CODIGO, NOMBRE FROM CLIENTES ORDER BY CODIGO");
        tableClientes.startTable();
        tableClientes.createTheTable(resultSet, Color.black, Color.white);
    }//create the table

    public void selectCliente() throws SQLException {
        int codCliente = Integer.parseInt(JOptionPane.showInputDialog(this, "Introduzca código de cliente"));
        resultSet = statement.executeQuery("SELECT * FROM CLIENTES WHERE CODIGO="+codCliente+"");
        while(resultSet.next()){    
            //put text in textfiles
            codigoText.setText(resultSet.getString("CODIGO"));
            codigoText.setEditable(false);      //you cannot edit referencia
            dniText.setText(resultSet.getString("DNI"));
            nombreText.setText(resultSet.getString("NOMBRE"));
            fechNacText.setText( resultSet.getString("FECNAC").substring(0, 9) ) ;
        } 
    }//select specific cliente     
    
    public void cleanTextFields() {        
        codigoText.setText("");
        codigoText.setEditable(true);       //cannot edit
        dniText.setText("");
        nombreText.setText("");
        fechNacText.setText("");
    }//clean textfields
    
    public boolean getDataClienteText() {
        if (codigoText.getText().length() >0 || nombreText.getText().length() >0 || dniText.getText().length() >0 || fechNacText.getText().length() >0) {            
            try {                
                codigo = Integer.parseInt( codigoText.getText() );
                dni = dniText.getText();
                nombre = nombreText.getText();
                fechNac = fechNacText.getText();
                //check the date formate
                checkDate = dateFormater.parse(fechNac);
                System.out.println(codigo+" "+dni+" "+nombre+" // "+fechNac);
                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El codigo es un campo numérico");
                return false;
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "La fecha debe contener formato fecha (05/02/2017)");
                Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {            
            JOptionPane.showMessageDialog(this, "Los campos deben estar rellenos");
            return false;
        }
    }//get data from articulo
    
    public boolean addNewCliente() throws SQLException {        
        if (getDataClienteText()) {
            try {
                statement.executeUpdate("INSERT INTO CLIENTES VALUES ("+codigo+", '"+dni+"', '"+nombre+"', '"+fechNac+"')");
                return true;    //success
            } catch (SQLIntegrityConstraintViolationException ex) {               
                JOptionPane.showMessageDialog(this, "El cliente "+nombre+" :"+codigo+" ya existe en el sistema");
                return false;   //error
            }
        } else {return false;}  //error
         
    }//add a new client
    
    public boolean modifyCliente(int codigoToUpdate) {
        if (getDataClienteText()) {
            try {
                statement.executeUpdate("UPDATE CLIENTES SET CODIGO="+codigo+", DNI='"+dni+"', NOMBRE='"+nombre+"', FECNAC='"+fechNac+"' WHERE CODIGO="+codigoToUpdate+"");
                return true;    //success
            } catch (SQLIntegrityConstraintViolationException ex) {               
                JOptionPane.showMessageDialog(this, "El cliente "+nombre+" :"+codigo+" ya existe en el sistema");
                return false;   //error
            } catch (SQLException ex) {
                Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {return false;}  //error
    }
    
    public boolean deleteCliente(int codigoToDelete) {
        try {
            statement.executeUpdate("DELETE FROM CLIENTES WHERE CODIGO="+codigoToDelete+"");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }//delete an specific cliente
    
    private ClientesWindow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        codigoText = new javax.swing.JTextField();
        dniText = new javax.swing.JTextField();
        nombreText = new javax.swing.JTextField();
        fechNacText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        try {
            tableClientes = new jtUserView.JTableUserView();
        } catch (java.sql.SQLException e1) {
            e1.printStackTrace();
        }
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Clientes");

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Codigo");

        jLabel2.setText("DNI");

        jLabel3.setText("Nombre");

        jLabel4.setText("Fecha nac");

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel5.setText("Cliente");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/CleanMyMac-1-icon.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(codigoText)
                            .addComponent(dniText)
                            .addComponent(nombreText)
                            .addComponent(fechNacText)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel5)
                        .addGap(0, 64, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(codigoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(dniText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombreText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fechNacText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tableClientes);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Log-Out-icon.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Go-back-icon.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Zoom-icon.png"))); // NOI18N
        jMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu1MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu1);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add-Icon.png"))); // NOI18N
        jMenu2.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu2MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu2);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_Icon.png"))); // NOI18N
        jMenu3.setToolTipText("");
        jMenu3.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu3MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu3);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Button-Delete-icon.png"))); // NOI18N
        jMenu4.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu4MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(187, 187, 187)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu1MenuSelected
        try {
            selectCliente();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la busqueda de cliente");
            Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenu1MenuSelected

    private void jMenu2MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu2MenuSelected
        try {
            if(addNewCliente()) {
                JOptionPane.showMessageDialog(this, "Cliente "+nombre+" creado correctamente");
                getClientes();  //reset the table
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenu2MenuSelected

    private void jMenu3MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu3MenuSelected
        if (codigoText.getText().length()>0) {            
            try {
                if (modifyCliente( Integer.parseInt( codigoText.getText() ) )) {
                    JOptionPane.showMessageDialog(this, "Cliente "+nombre+" actualizado correctamente");
                    getClientes();  //reset the table
                } else {JOptionPane.showMessageDialog(this, "Error al actualizar");}                             
            } catch (SQLException ex) {
                Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {JOptionPane.showMessageDialog(this, "Debes tener seleccionado un cliente para modificar");}
    }//GEN-LAST:event_jMenu3MenuSelected

    private void jMenu4MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu4MenuSelected
        int clientToDelete = Integer.parseInt( JOptionPane.showInputDialog("Introduce codigo de cliente para borrar") );
        if (deleteCliente(clientToDelete)) {            
            try {
                JOptionPane.showMessageDialog(this, "Cliente borrado con éxito");
                getClientes();  //reset the table
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "ERROR "+ex.getMessage());
                Logger.getLogger(ClientesWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al borrar cliente");
        }
    }//GEN-LAST:event_jMenu4MenuSelected

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        cleanTextFields();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            //close connection and the programm
            connection.close();
            System.exit(0);
        } catch (SQLException ex) {
            Logger.getLogger(ArticulosWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //go back to login
        loginWindow.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientesWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientesWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField codigoText;
    private javax.swing.JTextField dniText;
    private javax.swing.JTextField fechNacText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nombreText;
    private jtUserView.JTableUserView tableClientes;
    // End of variables declaration//GEN-END:variables
}
